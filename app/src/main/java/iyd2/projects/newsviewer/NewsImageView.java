package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


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
        int height = (int) resources.getDimension(R.dimen.bitmapHeight);

        setMeasuredDimension(width, height);
    }
}
