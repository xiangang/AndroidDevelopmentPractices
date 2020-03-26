package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.model.ItemShowInfo;
import com.arcsoft.arcfacedemo.widget.MultiFaceInfoAdapter;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.util.ImageUtils;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MultiImageActivity extends BaseActivity {
    private static final String TAG = "MultiImageActivity";

    private static final int ACTION_CHOOSE_MAIN_IMAGE = 0x201;
    private static final int ACTION_ADD_RECYCLER_ITEM_IMAGE = 0x202;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    private ImageView ivMainImage;
    private TextView tvMainImageInfo;
    /**
     * 选择图片时的类型
     */
    private static final int TYPE_MAIN = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * 主图的第0张人脸的特征数据
     */
    private FaceFeature mainFeature;

    private MultiFaceInfoAdapter multiFaceInfoAdapter;
    private List<ItemShowInfo> showInfoList;

    private FaceEngine faceEngine;
    private int faceEngineCode = -1;

    private Bitmap mainBitmap;

    private static String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_multi_image);
        /**
         * 在选择图片的时候，在android 7.0及以上通过FileProvider获取Uri，不需要文件权限
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            List<String> permissionList = new ArrayList<>(Arrays.asList(NEEDED_PERMISSIONS));
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            NEEDED_PERMISSIONS = permissionList.toArray(new String[0]);
        }

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
        }
        initView();
    }

    private void initView() {
        ivMainImage = findViewById(R.id.iv_main_image);
        tvMainImageInfo = findViewById(R.id.tv_main_image_info);
        RecyclerView recyclerFaces = findViewById(R.id.recycler_faces);
        showInfoList = new ArrayList<>();
        multiFaceInfoAdapter = new MultiFaceInfoAdapter(showInfoList, this);
        recyclerFaces.setAdapter(multiFaceInfoAdapter);
        recyclerFaces.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerFaces.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initEngine() {

        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);

        Log.i(TAG, "initEngine: init " + faceEngineCode);

        if (faceEngineCode != ErrorInfo.MOK) {
            showToast(getString(R.string.init_failed, faceEngineCode));
        }
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + faceEngineCode);
        }
    }

    @Override
    protected void onDestroy() {
        unInitEngine();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null || data.getData() == null) {
            showToast(getString(R.string.get_picture_failed));
            return;
        }
        if (requestCode == ACTION_CHOOSE_MAIN_IMAGE) {
            try {
                mainBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
                showToast(getString(R.string.get_picture_failed));
                return;
            }
            if (mainBitmap == null) {
                showToast(getString(R.string.get_picture_failed));
                return;
            }
            processImage(mainBitmap, TYPE_MAIN);
        } else if (requestCode == ACTION_ADD_RECYCLER_ITEM_IMAGE) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
                showToast(getString(R.string.get_picture_failed));
                return;
            }
            if (bitmap == null) {
                showToast(getString(R.string.get_picture_failed));
                return;
            }
            if (mainFeature == null) {
                return;
            }
            processImage(bitmap, TYPE_ITEM);
        }
    }


    public void processImage(Bitmap bitmap, int type) {
        if (bitmap == null) {
            return;
        }

        if (faceEngine == null) {
            return;
        }

        // 接口需要的bgr24宽度必须为4的倍数
        bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true);

        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // bitmap转bgr24
        long start = System.currentTimeMillis();
        byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            showToast("failed to transform bitmap to imageData, code is " + transformCode);
            return;
        }
//        Log.i(TAG, "processImage:bitmapToBgr24 cost =  " + (System.currentTimeMillis() - start));

        if (bgr24 != null) {

            List<FaceInfo> faceInfoList = new ArrayList<>();
            //人脸检测
            int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
            if (detectCode != 0 || faceInfoList.size() == 0) {
                showToast("face detection finished, code is " + detectCode + ", face num is " + faceInfoList.size());
                return;
            }
            //绘制bitmap
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);
            paint.setColor(Color.YELLOW);

            if (faceInfoList.size() > 0) {

                for (int i = 0; i < faceInfoList.size(); i++) {
                    //绘制人脸框
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(faceInfoList.get(i).getRect(), paint);
                    //绘制人脸序号
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setTextSize(faceInfoList.get(i).getRect().width() / 2);
                    canvas.drawText("" + i, faceInfoList.get(i).getRect().left, faceInfoList.get(i).getRect().top, paint);

                }
            }

            int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
            Log.i(TAG, "processImage: " + faceProcessCode);
            if (faceProcessCode != ErrorInfo.MOK) {
                showToast("face process finished, code is " + faceProcessCode);
                return;
            }
            //年龄信息结果
            List<AgeInfo> ageInfoList = new ArrayList<>();
            //性别信息结果
            List<GenderInfo> genderInfoList = new ArrayList<>();
            //三维角度结果
            List<Face3DAngle> face3DAngleList = new ArrayList<>();
            //获取年龄、性别、三维角度
            int ageCode = faceEngine.getAge(ageInfoList);
            int genderCode = faceEngine.getGender(genderInfoList);
            int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);

            if ((ageCode | genderCode | face3DAngleCode) != ErrorInfo.MOK) {
                showToast("at lease one of age、gender、face3DAngle detect failed! codes are: " + ageCode
                        + " ," + genderCode + " ," + face3DAngleCode);
                return;
            }

            //人脸比对数据显示
            if (faceInfoList.size() > 0) {
                if (type == TYPE_MAIN) {
                    int size = showInfoList.size();
                    showInfoList.clear();
                    multiFaceInfoAdapter.notifyItemRangeRemoved(0, size);
                    mainFeature = new FaceFeature();
                    int res = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), mainFeature);
                    if (res != ErrorInfo.MOK) {
                        mainFeature = null;
                    }
                    Glide.with(ivMainImage.getContext())
                            .load(bitmap)
                            .into(ivMainImage);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (faceInfoList.size() > 0) {
                        stringBuilder.append("face info:\n\n");
                    }
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        stringBuilder.append("face[")
                                .append(i)
                                .append("]:\n")
                                .append(faceInfoList.get(i))
                                .append("\nage:")
                                .append(ageInfoList.get(i).getAge())
                                .append("\ngender:")
                                .append(genderInfoList.get(i).getGender() == GenderInfo.MALE ? "MALE"
                                        : (genderInfoList.get(i).getGender() == GenderInfo.FEMALE ? "FEMALE" : "UNKNOWN"))
                                .append("\nface3DAngle:")
                                .append(face3DAngleList.get(i))
                                .append("\n\n");
                    }
                    tvMainImageInfo.setText(stringBuilder);
                } else if (type == TYPE_ITEM) {
                    FaceFeature faceFeature = new FaceFeature();
                    int res = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
                    if (res == 0) {
                        FaceSimilar faceSimilar = new FaceSimilar();
                        int compareResult = faceEngine.compareFaceFeature(mainFeature, faceFeature, faceSimilar);
                        if (compareResult == ErrorInfo.MOK) {

                            ItemShowInfo showInfo = new ItemShowInfo(bitmap, ageInfoList.get(0).getAge(), genderInfoList.get(0).getGender(), faceSimilar.getScore());
                            showInfoList.add(showInfo);
                            multiFaceInfoAdapter.notifyItemInserted(showInfoList.size() - 1);
                        } else {
                            showToast(getString(R.string.compare_failed, compareResult));
                        }
                    }
                }
            } else {
                if (type == TYPE_MAIN) {
                    mainBitmap = null;
                }
            }

        } else {
            showToast("can not get bgr24 from bitmap!");
        }
    }

    /**
     * 从本地选择文件
     *
     * @param action 可为选择主图{@link #ACTION_CHOOSE_MAIN_IMAGE}或者选择item图{@link #ACTION_ADD_RECYCLER_ITEM_IMAGE}
     */
    public void chooseLocalImage(int action) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, action);
    }

    public void addItemFace(View view) {
        if (faceEngineCode != ErrorInfo.MOK) {
            showToast(getString(R.string.engine_not_initialized, faceEngineCode));
            return;
        }
        if (mainBitmap == null) {
            showToast(getString(R.string.notice_choose_main_img));
            return;
        }
        chooseLocalImage(ACTION_ADD_RECYCLER_ITEM_IMAGE);
    }

    public void chooseMainImage(View view) {

        if (faceEngineCode != ErrorInfo.MOK) {
            showToast(getString(R.string.engine_not_initialized, faceEngineCode));
            return;
        }
        chooseLocalImage(ACTION_CHOOSE_MAIN_IMAGE);
    }

    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                initEngine();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }
}
