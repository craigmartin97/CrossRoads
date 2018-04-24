package com.kitkat.crossroads.ExternalClasses;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;

/**
 * ExifInterface is used to rotate or flip images after they have been selected by the user
 * THis makes sure they are the correct way around to be displayed to the user.
 */
public class ExifInterfaceImageRotate
{
    /**
     * SetUp image transfer for the image to be rotated or flipped.
     * A bitmap is created and transferred to be rotated
     *
     * @param uri     - The uri of the image to be rotated
     * @param context - The activity or fragment the image has came from
     * @return - The rotated or flipped image, after it has been modified
     */
    public Bitmap setUpImageTransfer(Uri uri, ContentResolver context)
    {
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context, uri);
            InputStream inputStream = context.openInputStream(uri);
            return modifyOrientation(bitmap, inputStream);
        } catch (IOException e)
        {
            e.getStackTrace();
        }
        return null;
    }

    /**
     * Send the image to be rotated dependant upon its needs
     * Gets the orientation that the image needs to be rotated or flipped by
     *
     * @param bitmap              - The bitmap image to be rotated
     * @param image_absolute_path - The path of the image
     * @return - Modified bitmap
     * @throws IOException - Catches an exception, if the ExifInterface cannot be used
     */
    private static Bitmap modifyOrientation(Bitmap bitmap, InputStream image_absolute_path) throws IOException
    {
        ExifInterface exifInterface = new ExifInterface(image_absolute_path);
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
     * @param bitmap  - Bitmap image that needs to be rotated
     * @param degrees - Amount that the bitmap needs to be rotated
     * @return - Rotated bitmap
     */
    private static Bitmap rotate(Bitmap bitmap, float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * If the uploaded image needed to be flipped
     *
     * @param bitmap     - Bitmap image that needs to be flipped
     * @param horizontal - If the image needs to be flipped horizontally
     * @param vertical   - If the image needs to be flipped vertically
     * @return - Flipped bitmap
     */
    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
