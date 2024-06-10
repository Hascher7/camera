package com.githcode.magiccamera.owcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurUtil {

    public static Bitmap blurBitmap(RenderScript rs, Bitmap bitmap, float radius) {
        Bitmap inputBitmap = bitmap.copy(bitmap.getConfig(), true);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        Allocation input = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation output = Allocation.createFromBitmap(rs, outputBitmap);

        ScriptIntrinsicBlur script = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(outputBitmap);
        }

        return outputBitmap;
    }

    public static Drawable createBlurredThumb(Context context, int color, float radius) {
        int size = (int) (24 * context.getResources().getDisplayMetrics().density); // 24dp
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        RenderScript rs = RenderScript.create(context);
        Bitmap blurredBitmap = blurBitmap(rs, bitmap, radius);

        return new BitmapDrawable(context.getResources(), blurredBitmap);
    }
}

