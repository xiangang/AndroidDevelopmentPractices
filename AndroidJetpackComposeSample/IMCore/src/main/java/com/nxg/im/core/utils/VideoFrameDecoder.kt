package com.nxg.im.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import coil.decode.AssetMetadata
import coil.decode.ContentMetadata
import coil.decode.DecodeResult
import coil.decode.DecodeUtils
import coil.decode.Decoder
import coil.decode.ImageSource
import coil.decode.ResourceMetadata
import coil.fetch.MediaDataSourceFetcher
import coil.request.Options
import coil.request.videoFrameMicros
import coil.request.videoFrameOption
import coil.request.videoFramePercent
import coil.size.Dimension
import coil.size.Scale
import coil.size.Size
import coil.size.isOriginal
import coil.size.pxOrElse
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * A [Decoder] that uses [MediaMetadataRetriever] to fetch and decode a frame from a video.
 */
class VideoFrameDecoder(
    private val context: Context,
    private val options: Options = Options(context = context)
) {

    fun decode( filePath:String) = MediaMetadataRetriever().use { retriever ->
        retriever.setDataSource(filePath)
        val option =
            options.parameters.videoFrameOption() ?: MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = computeFrameMicros(retriever)

        // Resolve the dimensions to decode the video frame at accounting
        // for the source's aspect ratio and the target's size.
        var srcWidth: Int
        var srcHeight: Int
        val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toIntOrNull() ?: 0
        if (rotation == 90 || rotation == 270) {
            srcWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
            srcHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
        } else {
            srcWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
            srcHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
        }

        val dstSize = if (srcWidth > 0 && srcHeight > 0) {
            val dstWidth = options.size.widthPx(options.scale) { srcWidth }
            val dstHeight = options.size.heightPx(options.scale) { srcHeight }
            val rawScale = DecodeUtils.computeSizeMultiplier(
                srcWidth = srcWidth,
                srcHeight = srcHeight,
                dstWidth = dstWidth,
                dstHeight = dstHeight,
                scale = options.scale
            )
            val scale = if (options.allowInexactSize) {
                rawScale.coerceAtMost(1.0)
            } else {
                rawScale
            }
            val width = (scale * srcWidth).roundToInt()
            val height = (scale * srcHeight).roundToInt()
            Size(width, height)
        } else {
            // We were unable to decode the video's dimensions.
            // Fall back to decoding the video frame at the original size.
            // We'll scale the resulting bitmap after decoding if necessary.
            Size.ORIGINAL
        }

        val (dstWidth, dstHeight) = dstSize
        val rawBitmap: Bitmap? =
            if (Build.VERSION.SDK_INT >= 27 && dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
                retriever.getScaledFrameAtTime(
                    frameMicros,
                    option,
                    dstWidth.px,
                    dstHeight.px,
                    options.config
                )
            } else {
                retriever.getFrameAtTime(frameMicros, option, options.config)?.also {
                    srcWidth = it.width
                    srcHeight = it.height
                }
            }

        // If you encounter this exception make sure your video is encoded in a supported codec.
        // https://developer.android.com/guide/topics/media/media-formats#video-formats
        checkNotNull(rawBitmap) { "Failed to decode frame at $frameMicros microseconds." }

        val bitmap = normalizeBitmap(rawBitmap, dstSize)

        val isSampled = if (srcWidth > 0 && srcHeight > 0) {
            DecodeUtils.computeSizeMultiplier(
                srcWidth = srcWidth,
                srcHeight = srcHeight,
                dstWidth = bitmap.width,
                dstHeight = bitmap.height,
                scale = options.scale
            ) < 1.0
        } else {
            // We were unable to determine the original size of the video. Assume it is sampled.
            true
        }

        DecodeResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = isSampled
        )
    }

    private fun computeFrameMicros(retriever: MediaMetadataRetriever): Long {
        val frameMicros = options.parameters.videoFrameMicros()
        if (frameMicros != null) {
            return frameMicros
        }

        val framePercent = options.parameters.videoFramePercent()
        if (framePercent != null) {
            val durationMillis =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
            return 1000 * (framePercent * durationMillis).roundToLong()
        }

        return 0
    }

    /** Return [inBitmap] or a copy of [inBitmap] that is valid for the input [options] and [size]. */
    private fun normalizeBitmap(inBitmap: Bitmap, size: Size): Bitmap {
        // Fast path: if the input bitmap is valid, return it.
        if (isConfigValid(inBitmap, options) && isSizeValid(inBitmap, options, size)) {
            return inBitmap
        }

        // Slow path: re-render the bitmap with the correct size + config.
        val scale = DecodeUtils.computeSizeMultiplier(
            srcWidth = inBitmap.width,
            srcHeight = inBitmap.height,
            dstWidth = size.width.pxOrElse { inBitmap.width },
            dstHeight = size.height.pxOrElse { inBitmap.height },
            scale = options.scale
        ).toFloat()
        val dstWidth = (scale * inBitmap.width).roundToInt()
        val dstHeight = (scale * inBitmap.height).roundToInt()
        val safeConfig = when {
            Build.VERSION.SDK_INT >= 26 && options.config == Bitmap.Config.HARDWARE -> Bitmap.Config.ARGB_8888
            else -> options.config
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val outBitmap = createBitmap(dstWidth, dstHeight, safeConfig)
        outBitmap.applyCanvas {
            scale(scale, scale)
            drawBitmap(inBitmap, 0f, 0f, paint)
        }
        inBitmap.recycle()

        return outBitmap
    }

    private fun isConfigValid(bitmap: Bitmap, options: Options): Boolean {
        return Build.VERSION.SDK_INT < 26 ||
                bitmap.config != Bitmap.Config.HARDWARE ||
                options.config == Bitmap.Config.HARDWARE
    }

    private fun isSizeValid(bitmap: Bitmap, options: Options, size: Size): Boolean {
        if (options.allowInexactSize) return true
        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = bitmap.width,
            srcHeight = bitmap.height,
            dstWidth = size.width.pxOrElse { bitmap.width },
            dstHeight = size.height.pxOrElse { bitmap.height },
            scale = options.scale
        )
        return multiplier == 1.0
    }

    private fun MediaMetadataRetriever.setDataSource(source: ImageSource) {
        if (Build.VERSION.SDK_INT >= 23 && source.metadata is MediaDataSourceFetcher.MediaSourceMetadata) {
            setDataSource((source.metadata as MediaDataSourceFetcher.MediaSourceMetadata).mediaDataSource)
            return
        }

        when (val metadata = source.metadata) {
            is AssetMetadata -> {
                options.context.assets.openFd(metadata.filePath).use {
                    setDataSource(it.fileDescriptor, it.startOffset, it.length)
                }
            }

            is ContentMetadata -> {
                setDataSource(options.context, metadata.uri)
            }

            is ResourceMetadata -> {
                setDataSource("android.resource://${metadata.packageName}/${metadata.resId}")
            }

            else -> {
                setDataSource(source.file().toFile().path)
            }
        }
    }

    companion object {
        const val VIDEO_FRAME_MICROS_KEY = "coil#video_frame_micros"
        const val VIDEO_FRAME_PERCENT_KEY = "coil#video_frame_percent"
        const val VIDEO_FRAME_OPTION_KEY = "coil#video_frame_option"

    }
}


/** [MediaMetadataRetriever] doesn't implement [AutoCloseable] until API 29. */
internal inline fun <T> MediaMetadataRetriever.use(block: (MediaMetadataRetriever) -> T): T {
    try {
        return block(this)
    } finally {
        // We must call 'close' on API 29+ to avoid a strict mode warning.
        if (Build.VERSION.SDK_INT >= 29) {
            close()
        } else {
            release()
        }
    }
}

internal fun MediaMetadataRetriever.getFrameAtTime(
    timeUs: Long,
    option: Int,
    config: Bitmap.Config,
): Bitmap? = if (Build.VERSION.SDK_INT >= 30) {
    val params = MediaMetadataRetriever.BitmapParams().apply { preferredConfig = config }
    getFrameAtTime(timeUs, option, params)
} else {
    getFrameAtTime(timeUs, option)
}

@RequiresApi(27)
internal fun MediaMetadataRetriever.getScaledFrameAtTime(
    timeUs: Long,
    option: Int,
    dstWidth: Int,
    dstHeight: Int,
    config: Bitmap.Config,
): Bitmap? = if (Build.VERSION.SDK_INT >= 30) {
    val params = MediaMetadataRetriever.BitmapParams().apply { preferredConfig = config }
    getScaledFrameAtTime(timeUs, option, dstWidth, dstHeight, params)
} else {
    getScaledFrameAtTime(timeUs, option, dstWidth, dstHeight)
}

internal inline fun Size.widthPx(scale: Scale, original: () -> Int): Int {
    return if (isOriginal) original() else width.toPx(scale)
}

internal inline fun Size.heightPx(scale: Scale, original: () -> Int): Int {
    return if (isOriginal) original() else height.toPx(scale)
}

internal fun Dimension.toPx(scale: Scale) = pxOrElse {
    when (scale) {
        Scale.FILL -> Int.MIN_VALUE
        Scale.FIT -> Int.MAX_VALUE
    }
}


