package com.example.bdroomcomcamera.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageUtils {

    private static final int DEFAULT_MAX_WIDTH = 1024;
    private static final int DEFAULT_MAX_HEIGHT = 1024;
    private static final int THUMB_MAX_WIDTH = 160;
    private static final int THUMB_MAX_HEIGHT = 160;
    private static final int JPEG_QUALITY = 75;

    private ImageUtils() {
    }

    public static Bitmap decodeByteArrayReduced(byte[] bytes) {
        return decodeByteArrayReduced(bytes, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
    }

    public static Bitmap decodeThumbnail(byte[] bytes) {
        return decodeByteArrayReduced(bytes, THUMB_MAX_WIDTH, THUMB_MAX_HEIGHT);
    }

    public static Bitmap decodeByteArrayReduced(byte[] bytes, int maxWidth, int maxHeight) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bounds);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxWidth, maxHeight);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static Bitmap decodeUriReduced(ContentResolver resolver, Uri uri) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = resolver.openInputStream(uri)) {
            BitmapFactory.decodeStream(input, null, bounds);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSampleSize(
                bounds.outWidth,
                bounds.outHeight,
                DEFAULT_MAX_WIDTH,
                DEFAULT_MAX_HEIGHT
        );

        try (InputStream input = resolver.openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (bitmap == null) {
                throw new IOException("Imagem invalida");
            }
            return bitmap;
        }
    }

    public static byte[] compressToJpeg(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream);
        return stream.toByteArray();
    }

    private static int calculateSampleSize(int width, int height, int maxWidth, int maxHeight) {
        int sample = 1;
        if (width <= 0 || height <= 0) {
            return sample;
        }

        while ((height / sample) > maxHeight || (width / sample) > maxWidth) {
            sample *= 2;
        }
        return sample;
    }
}
