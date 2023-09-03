package com.nxg.opencv.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RenderTextureView extends TextureView {

    public RenderTextureView(@NonNull Context context) {
        super(context);
    }

    public RenderTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RenderTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

