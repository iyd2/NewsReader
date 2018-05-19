package iyd2.projects.newsviewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class BitmapUtil {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int rawWidth = options.outWidth;
        int rawHeight = options.outHeight;
        int inSampleSize = 1;

        if (rawWidth > reqWidth) {

            final int halfWidth = rawWidth / 2;
            final int halfHeight = rawHeight / 2;

            while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeByteArray(byte[] bytes, int reqWidth, int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        // avoids in memory allocation
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


        Bitmap bmOverlay = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);

        return bitmap2;

    }


    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static Point getImageSize(Activity activity) {
        Point points = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(points);

        if (points.x > points.y) {
            points.x = points.y;
        }

        points.y = (int) activity.getResources().getDimension(R.dimen.bitmapHeight);
        return points;
    }

    private static Bitmap cropBitmap1(int reqWidth, int reqHeight, Bitmap bitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(30, 30, 100, 100, paint);

        return bmOverlay;
    }


}
