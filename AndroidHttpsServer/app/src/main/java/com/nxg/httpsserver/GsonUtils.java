package com.nxg.httpsserver;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.nxg.httpsserver.api.ApiResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class GsonUtils {

    private static class InstanceHolder {
        private static final Gson INSTANCE = new Gson();
    }

    public static Gson getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static <T> ApiResult<T> fromJson(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(ApiResult.class, new Class[]{clazz});
        return GsonUtils.getInstance().fromJson(json, type);
    }


    public static <T> ApiResult<List<T>> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(ApiResult.class, new Type[]{listType});
        return GsonUtils.getInstance().fromJson(json, type);
    }

    static class ParameterizedTypeImpl implements ParameterizedType {

        private final Class raw;
        private final Type[] args;

        ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @NonNull
        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
