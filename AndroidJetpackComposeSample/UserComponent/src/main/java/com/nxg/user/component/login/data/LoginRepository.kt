package com.nxg.user.component.login.data

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.nxg.im.http.bean.LoginData
import com.nxg.mvvm.logger.SimpleLogger
import java.lang.Exception


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) : SimpleLogger {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        Utils.getApp().applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // in-memory cache of the loggedInUser object
    var loginData: LoginData? = null
        private set

    val isLoggedIn: Boolean
        get() = loginData != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        try {
            loginData = null
            val userJson = sharedPreferences.getString("me", "")
            loginData = if (userJson.isNullOrEmpty()) {
                null
            } else {
                GsonUtils.fromJson(userJson, LoginData::class.java)
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    suspend fun logout() {
        loginData = null
        dataSource.logout()
    }

    suspend fun login(username: String, password: String): Result<LoginData> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loginData: LoginData) {
        this.loginData = loginData
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore


        // use the shared preferences and editor as you normally would
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("me", GsonUtils.toJson(this.loginData))
        editor.apply()
    }
}