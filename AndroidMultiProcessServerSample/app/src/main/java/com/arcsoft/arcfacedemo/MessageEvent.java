package com.arcsoft.arcfacedemo;

public class MessageEvent {


    public static class ArcFaceEvent{

        public static final int UNKNOWN_ERROR = -1;
        public static final int REGISTER_STATUS_READY = 0;
        public static final int REGISTER_STATUS_PROCESSING = 1;
        public static final int REGISTER_STATUS_DONE = 2;
        public static final int REGISTER_STATUS_SUCCESS = 3;
        public static final int REGISTER_STATUS_FAILED = 4;
        public static final int RECOGNIZE_STATUS_SUCCESS = 5;
        public static final int RECOGNIZE_STATUS_FAILED = 6;
        public static final int ACTIVITY_FINISH = 100;
        public static final int ACTIVITY_START = 101;


        public int code;
        public String msg;
        public String data;

        public ArcFaceEvent(int code, String msg, String data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

    }

}
