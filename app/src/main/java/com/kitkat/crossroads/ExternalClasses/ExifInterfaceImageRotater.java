package com.kitkat.crossroads.ExternalClasses;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by craig on 06/04/18.
 */

public class ExifInterfaceImageRotater
{
    public Bitmap setUpImageTransfer(Uri uri, ContentResolver context)
    {
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context, uri);
            ContentResolver contentResolver = context;
            InputStream inputStream = contentResolver.openInputStream(uri);
            return modifyOrientation(bitmap, inputStream);
        } catch (IOException e)
        {
            e.getStackTrace();
        }
        return null;
    }

    /**
     * Send the image to be rotated dependant upon its needs
     *
     * @param bitmap
     * @param image_absolute_path
     * @return
     * @throws IOException
     */
    public static Bitmap modifyOrientation(Bitmap bitmap, InputStream image_absolute_path) throws IOException
    {
        android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(image_absolute_path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);
            default:
                return bitmap;
        }
    }

    /**
     * If the uploaded image needed to be rotated
     *
     * @param bitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap bitmap, float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap1;
    }

    /**
     * If the uploaded image needed to be flipped
     *
     * @param bitmap
     * @param horizontal
     * @param vertical
     * @return
     */
    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap1;
    }
}
