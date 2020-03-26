package com.arcsoft.arcfacedemo;

import android.app.Application;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * ================================================
 * Created by xiangang on 2020/2/24 21:48
 * <a href="mailto:xiangang12202@gmail.com">Contact me</a>
 * <a href="https://github.com/xiangang">Follow me</a>
 * ================================================
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                .logStrategy(new LogCatStrategy())
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy));
    }

    public class LogCatStrategy implements LogStrategy {
        @Override
        public void log(int priority, String tag, String message) {
            Log.println(priority, randomKey() + tag, message);
        }
        private int last;
        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);
        }
    }
}
