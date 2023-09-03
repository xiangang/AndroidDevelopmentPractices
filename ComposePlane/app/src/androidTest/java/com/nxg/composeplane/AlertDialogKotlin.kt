package com.nxg.composeplane

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

class AlertDialog(context: Context, private val title: String, private val message: String) :
    Dialog(context), DialogInterface {
    private var positiveButtonText: CharSequence? = null
    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    private var negativeButtonText: CharSequence? = null
    private var negativeButtonListener: DialogInterface.OnClickListener? = null

    fun setPositiveButtonListener(
        text: CharSequence?,
        positiveButtonListener: DialogInterface.OnClickListener?
    ): AlertDialog {
        positiveButtonText = text
        this.positiveButtonListener = positiveButtonListener
        return this
    }

    fun setNegativeButtonListener(
        text: CharSequence?,
        negativeButtonListener: DialogInterface.OnClickListener?
    ): AlertDialog {
        negativeButtonText = text
        this.negativeButtonListener = negativeButtonListener
        return this
    }

    companion object {
        inline fun build(
            context: Context,
            title: String,
            message: String,
            block: Builder.() -> Unit
        ) = Builder(context, title, message).apply(block)
    }

    class Builder(
        private var context: Context,
        private var title: String,
        private var message: String
    ) {

        private var mPositiveButtonText: CharSequence? = null
        private var mPositiveButtonListener: DialogInterface.OnClickListener? = null
        private var mNegativeButtonText: CharSequence? = null
        private var mNegativeButtonListener: DialogInterface.OnClickListener? = null
        fun setPositiveButtonListener(
            text: CharSequence?,
            positiveButtonListener: DialogInterface.OnClickListener?
        ): Builder {
            mPositiveButtonText = text
            mPositiveButtonListener = positiveButtonListener
            return this
        }

        fun setNegativeButtonListener(
            text: CharSequence?,
            negativeButtonListener: DialogInterface.OnClickListener?
        ): Builder {
            mNegativeButtonText = text
            mNegativeButtonListener = negativeButtonListener
            return this
        }

        fun create(): AlertDialog {
            val dialog = AlertDialog(context, title, message)
            if (mPositiveButtonText != null) {
                dialog.positiveButtonText = mPositiveButtonText
            }
            if (mPositiveButtonListener != null) {
                dialog.positiveButtonListener = mPositiveButtonListener
            }
            if (mNegativeButtonText != null) {
                dialog.negativeButtonText = mNegativeButtonText
            }
            if (mNegativeButtonListener != null) {
                dialog.positiveButtonListener = mNegativeButtonListener
            }
            return dialog
        }

        fun show(): AlertDialog {
            val dialog = create()
            dialog.show()
            return dialog
        }
    }
}