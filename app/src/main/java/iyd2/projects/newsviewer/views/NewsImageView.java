package iyd2.projects.newsviewer.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import iyd2.projects.newsviewer.R;


public class NewsImageView extends android.support.v7.widget.AppCompatImageView {

    public NewsImageView(Context context) {
        super(context);
    }

    public NewsImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Resources resources = getContext().getResources();
        int width = (int) (resources.getDisplayMetrics().widthPixels - resources.getDimension(R.dimen.sidePadding) * 2);
        int height = (resources.getDisplayMetrics().heightPixels / 3);

        /*Drawable drawable = getDrawable();

         if (drawable != null) {
            height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
        }*/
        setMeasuredDimension(width, height);
    }
}
