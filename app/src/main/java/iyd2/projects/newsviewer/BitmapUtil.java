package iyd2.projects.newsviewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class BitmapUtil {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {

        int rawWidth = options.outWidth;
        int inSampleSize = 1;

        if (rawWidth > reqWidth) {

            final int halfWidth = rawWidth / 2;

            while ((halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeByteArray(byte[] bytes, int reqWidth) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        // avoids in memory allocation
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

    }

    public static Point getActivitySize(Activity activity) {

        Point points = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(points);

        return points;
    }


}
