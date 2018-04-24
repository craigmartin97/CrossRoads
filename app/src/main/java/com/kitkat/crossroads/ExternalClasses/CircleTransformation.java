package com.kitkat.crossroads.ExternalClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * CircleTransformation, changed the image from a square into a circle image
 * This class is used for the users profile image to make it feel more professional.
 * It get's the bitmaps height and width, and divides them in half.
 * A new bitmap is created, from those dimensions.
 * Background edges are changed colour to hide them. The bitmap is then returned
 */
public class CircleTransformation implements Transformation
{
    @Override
    public Bitmap transform(Bitmap source)
    {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source)
        {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size + 10, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key()
    {
        return "circle";
    }
}