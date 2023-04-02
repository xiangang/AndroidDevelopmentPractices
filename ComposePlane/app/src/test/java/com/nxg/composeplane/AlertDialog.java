//package com.nxg.composeplane;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//
//import androidx.annotation.NonNull;
//
//public class AlertDialog extends Dialog implements DialogInterface {
//    private final String title;
//    private final String message;
//    private CharSequence mPositiveButtonText;
//    private DialogInterface.OnClickListener mPositiveButtonListener;
//    private CharSequence mNegativeButtonText;
//    private DialogInterface.OnClickListener mNegativeButtonListener;
//
//    public AlertDialog(@NonNull Context context, String title, String message) {
//        super(context);
//        this.title = title;
//        this.message = message;
//    }
//
//    public CharSequence getPositiveButtonText() {
//        return mPositiveButtonText;
//    }
//
//    public void setPositiveButtonText(CharSequence mPositiveButtonText) {
//        this.mPositiveButtonText = mPositiveButtonText;
//    }
//
//    public OnClickListener getPositiveButtonListener() {
//        return mPositiveButtonListener;
//    }
//
//    public void setPositiveButtonListener(OnClickListener mPositiveButtonListener) {
//        this.mPositiveButtonListener = mPositiveButtonListener;
//    }
//
//    public CharSequence getNegativeButtonText() {
//        return mNegativeButtonText;
//    }
//
//    public void setNegativeButtonText(CharSequence mNegativeButtonText) {
//        this.mNegativeButtonText = mNegativeButtonText;
//    }
//
//    public OnClickListener getNegativeButtonListener() {
//        return mNegativeButtonListener;
//    }
//
//    public void setNegativeButtonListener(OnClickListener mNegativeButtonListener) {
//        this.mNegativeButtonListener = mNegativeButtonListener;
//    }
//
//    public static class Builder {
//
//        private final Context context;
//        private final String title;
//        private final String message;
//        private CharSequence mPositiveButtonText;
//        private DialogInterface.OnClickListener mPositiveButtonListener;
//        private CharSequence mNegativeButtonText;
//        private DialogInterface.OnClickListener mNegativeButtonListener;
//
//
//        public Builder(Context context, String title, String message) {
//            this.context = context;
//            this.title = title;
//            this.message = message;
//        }
//
//        public Builder setPositiveButtonListener(CharSequence text, DialogInterface.OnClickListener positiveButtonListener) {
//            mPositiveButtonText = text;
//            mPositiveButtonListener = positiveButtonListener;
//            return this;
//        }
//
//        public Builder setNegativeButtonListener(CharSequence text, DialogInterface.OnClickListener negativeButtonListener) {
//            mNegativeButtonText = text;
//            mNegativeButtonListener = negativeButtonListener;
//            return this;
//        }
//
//        public AlertDialog create() {
//            final AlertDialog dialog = new AlertDialog(context, title, message);
//            if (mPositiveButtonText != null) {
//                dialog.setPositiveButtonText(mPositiveButtonText);
//            }
//            if (mPositiveButtonListener != null) {
//                dialog.setPositiveButtonListener(mPositiveButtonListener);
//            }
//            if (mNegativeButtonText != null) {
//                dialog.setNegativeButtonText(mNegativeButtonText);
//            }
//            if (mNegativeButtonListener != null) {
//                dialog.setPositiveButtonListener(mNegativeButtonListener);
//            }
//            return dialog;
//        }
//
//        public AlertDialog show() {
//            final AlertDialog dialog = create();
//            dialog.show();
//            return dialog;
//        }
//
//    }
//
//}