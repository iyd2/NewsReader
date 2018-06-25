package iyd2.projects.newsviewer.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import iyd2.projects.newsviewer.R;

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


        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        return bitmap;

    }

    public static Point getImageSize(Activity activity) {
        Point points = new Point();

        int padding = (int) activity.getResources().getDimension(R.dimen.sidePadding);

        activity.getWindowManager().getDefaultDisplay().getSize(points);

        if (points.x > points.y) {
            points.x = points.y;
        }

        points.x -= padding * 2;

        points.y = (int) activity.getResources().getDimension(R.dimen.bitmapHeight);
        return points;
    }


}
