package com.sony.mibox.controller;

import android.graphics.Bitmap;
import android.util.Base64;

import java.nio.ByteBuffer;

public class Utils {
    public static String image2String(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        return Base64.encodeToString(buffer.array(), Base64.DEFAULT);
    }

    public static Bitmap string2Image(String imageStringData, int width, int height) {
        byte[] bytes = Base64.decode(imageStringData, Base64.DEFAULT);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }
}
