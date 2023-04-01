package com.nxg.commonutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**启动Activity*/
inline fun <reified T : Activity> Context.onStart() {
    startActivity(Intent(this, T::class.java))
}

/**启动Activity*/
inline fun <reified T : Activity> Context.onStart(mKey: String, mBundle: Bundle?) {
    val mIntent = Intent(this, T::class.java)
    mIntent.putExtra(mKey, mBundle)
    startActivity(mIntent)
}

/**启动Activity*/
inline fun <reified T : Activity> Context.onStart(vararg pair: Pair<String, String>?) {
    val mIntent = Intent(this, T::class.java)
    pair.let {
        pair.forEach {
            mIntent.putExtra(it!!.first, it.second)
        }
    }
    startActivity(mIntent)
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStart() {
    startActivity(Intent(this, T::class.java))
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStart(mKey: String, mBundle: Bundle?) {
    val mIntent = Intent(this, T::class.java)
    mIntent.putExtra(mKey, mBundle)
    startActivity(mIntent)
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStart(vararg pair: Pair<String, String>?) {
    val mIntent = Intent(this, T::class.java)
    pair.let {
        pair.forEach {
            mIntent.putExtra(it!!.first, it.second)
        }
    }
    startActivity(mIntent)
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStartResult(requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java), requestCode, null)
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStartResult(requestCode: Int, mBundle: Bundle?) {
    startActivityForResult(Intent(this, T::class.java), requestCode, mBundle)
}

/**启动Activity*/
inline fun <reified T : Activity> Activity.onStartResult(requestCode: Int, vararg pair: Pair<String, String>) {
    val mIntent = Intent(this, T::class.java)
    val mBundle = Bundle()
    pair.let {
        pair.forEach {
            mBundle.putString(it!!.first, it.second)
        }
    }
    startActivityForResult(mIntent, requestCode, mBundle)
}