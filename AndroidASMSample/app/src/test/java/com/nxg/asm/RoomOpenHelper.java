package com.nxg.asm;

/**
 * 模拟Room的RoomOpenHelper类
 */
public class RoomOpenHelper {

    public void onUpgrade(SupportSQLiteDatabase db) {

    }

    public void onDowngrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db);
    }

    public void updateIdentity(SupportSQLiteDatabase db) {

    }

    public static class SupportSQLiteDatabase {

    }

}
