package com.arcsoft.arcfacedemo.util;

/**
 * ================================================
 * Created by xiangang on 2020/2/24 21:55
 * <a href="mailto:xiangang12202@gmail.com">Contact me</a>
 * <a href="https://github.com/xiangang">Follow me</a>
 * ================================================
 */


import com.orhanobut.logger.Logger;

/**
 * 日志工具类，封装第三方日志库调用，方便随时替换，。
 */
public final class LoggerUtil {
    /**
     * 这里使用 Logger.t()方法来设置tag，也可以在Logger初始化的时候配置全局TAG
     */
    private static final String TAG = "LoggerUtil";

    private static boolean ENABLE = true;

    public static void v(String tag, String message) {
        if (ENABLE) {
            Logger.t(tag).v(message);
        }
    }

    public static void v(String message) {
        if (ENABLE) {
            Logger.t(TAG).v(message);
        }
    }

    public static void d(String tag, Object message) {
        if (ENABLE) {
            Logger.t(tag).d(message);
        }
    }

    public static void d(Object message) {
        if (ENABLE) {
            Logger.d(message);
        }
    }

    public static void i(String tag, String message) {
        if (ENABLE) {
            Logger.t(tag).i(message);
        }
    }

    public static void i(String message) {
        if (ENABLE) {
            Logger.t(TAG).i(message);
        }
    }

    public static void w(String tag, String message) {
        if (ENABLE) {
            Logger.t(tag).w(message);
        }
    }

    public static void w(String message) {
        if (ENABLE) {
            Logger.t(TAG).w(message);
        }
    }

    public static void e(String tag, String message) {
        if (ENABLE) {
            Logger.t(tag).e(message);
        }
    }

    public static void e(String message) {
        if (ENABLE) {
            Logger.t(TAG).e(message);
        }
    }

    public static void e(Exception e, String message) {
        if (ENABLE) {
            Logger.e(e, message);
        }
    }

    public static void json(String tag, String json) {
        if (ENABLE) {
            Logger.t(tag).json(json);
        }
    }

    public static void json(String json) {
        if (ENABLE) {
            Logger.t(TAG).json(json);
        }
    }

    public static void xml(String xml) {
        if (ENABLE) {
            Logger.t(TAG).xml(xml);
        }
    }

    public static void xml(String tag, String xml) {
        if (ENABLE) {
            Logger.t(tag).xml(xml);
        }
    }
}
