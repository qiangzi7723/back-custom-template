package com.custom.util;

public class PaddingUid {
    static int padding = 618275;

    public static int encrypt(int uid){
        uid += padding;
        return uid;
    }

    public static int decrypt(int uid){
        uid -= padding;
        return uid;
    }

}
