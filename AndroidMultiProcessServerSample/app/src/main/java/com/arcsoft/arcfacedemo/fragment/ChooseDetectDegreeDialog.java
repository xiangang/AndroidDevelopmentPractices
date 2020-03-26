package com.arcsoft.arcfacedemo.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.ChooseFunctionActivity;
import com.arcsoft.arcfacedemo.util.ConfigUtil;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_0_ONLY;
import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_180_ONLY;
import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_270_ONLY;
import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_90_ONLY;
import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_ALL_OUT;

public class ChooseDetectDegreeDialog extends DialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_choose_detect_degree, container);
        initView(dialogView);
        return dialogView;
    }

    private void initView(View dialogView) {
        ImageView ivClose = dialogView.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        //设置视频模式下的人脸优先检测方向
        RadioGroup radioGroupFtOrient = dialogView.findViewById(R.id.radio_group_ft_orient);
        RadioButton rbOrient0 = dialogView.findViewById(R.id.rb_orient_0);
        RadioButton rbOrient90 = dialogView.findViewById(R.id.rb_orient_90);
        RadioButton rbOrient180 = dialogView.findViewById(R.id.rb_orient_180);
        RadioButton rbOrient270 = dialogView.findViewById(R.id.rb_orient_270);
        RadioButton rbOrientAll = dialogView.findViewById(R.id.rb_orient_all);
        switch (ConfigUtil.getFtOrient(getActivity())) {
            case ASF_OP_90_ONLY:
                rbOrient90.setChecked(true);
                break;
            case ASF_OP_180_ONLY:
                rbOrient180.setChecked(true);
                break;
            case ASF_OP_270_ONLY:
                rbOrient270.setChecked(true);
                break;
            case ASF_OP_ALL_OUT:
                rbOrientAll.setChecked(true);
                break;
            case ASF_OP_0_ONLY:
            default:
                rbOrient0.setChecked(true);
                break;
        }
        radioGroupFtOrient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_orient_90:
                        ConfigUtil.setFtOrient(getActivity(), ASF_OP_90_ONLY);
                        break;
                    case R.id.rb_orient_180:
                        ConfigUtil.setFtOrient(getActivity(), ASF_OP_180_ONLY);
                        break;
                    case R.id.rb_orient_270:
                        ConfigUtil.setFtOrient(getActivity(), ASF_OP_270_ONLY);
                        break;
                    case R.id.rb_orient_all:
                        ConfigUtil.setFtOrient(getActivity(), ASF_OP_ALL_OUT);
                        break;
                    case R.id.rb_orient_0:
                    default:
                        ConfigUtil.setFtOrient(getActivity(), ASF_OP_0_ONLY);
                        break;
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
