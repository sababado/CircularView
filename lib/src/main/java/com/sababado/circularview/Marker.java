package com.sababado.circularview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by rjszabo on 3/21/14.
 */
public class Marker {
    private float radius;
    private float sectionMin;
    private float sectionMax;
    private float x;
    private float y;
    private Context mContext;
    private Bitmap bitmap;
    private CircularView.AdapterDataSetObserver mAdapterDataSetObserver;

    final static int ANIMATION_DURATION = 500;
    private AnimatorSet animatorSet;

    public Marker(final Context context) {
        mContext = context;
    }

    Marker(final Context context, final float x, final float y, final float radius) {
        this(context);
        init(x, y, radius, 0, 0, null);
    }

    Marker(final Context context, final float x, final float y, final float radius, final float sectionMin, final float sectionMax) {
        this(context);
        init(x, y, radius, sectionMin, sectionMax, null);
    }

    void init(final float x, final float y, final float radius, final float sectionMin, final float sectionMax, final CircularView.AdapterDataSetObserver adapterDataSetObserver) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.sectionMin = sectionMin;
        this.sectionMax = sectionMax;
        this.mAdapterDataSetObserver = adapterDataSetObserver;
    }

//    void draw(final Canvas canvas, final float radius, final Paint paint) {
//
//        if (bitmap != null) {
//            canvas.drawBitmap(bitmap,
//                    x - bitmap.getWidth() / 2f,
//                    y - bitmap.getHeight() / 2f,
//                    paint);
//        }
////        canvas.drawCircle(x, y, radius, paint);
//
////            mTextPaint.setColor(Color.BLACK);
////            canvas.drawText(String.valueOf(id), x, y, mTextPaint);
////            mTextPaint.setColor(mExampleColor);
//    }
//
//    void draw(final Canvas canvas, final Paint paint) {
//        draw(canvas, this.radius, paint);
//    }

    protected void draw(final Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap,
                    x - bitmap.getWidth() / 2f,
                    y - bitmap.getHeight() / 2f,
                    null);
        }
    }

    /**
     * Get the marker's bitmap.
     *
     * @return The marker's bitmap.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Set the marker's visual as a bitmap.
     *
     * @param bitmap Bitmap to display.
     */
    public void setSrc(Bitmap bitmap) {
        this.bitmap = bitmap;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    /**
     * Set the marker's visual by using a resource id.
     *
     * @param resId Resource id of the drawable to display.
     */
    public void setSrc(final int resId) {
        setSrc(BitmapFactory.decodeResource(mContext.getResources(), resId));
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    public boolean hasInSection(final float x) {
        if (sectionMin <= sectionMax) {
            return x <= sectionMax && x >= sectionMin;
        }
        final float endDifference = 360f - sectionMin;
        return (x <= sectionMax && x >= -endDifference) ||
                (x <= sectionMax + endDifference + sectionMin && x >= sectionMin);
    }

    public void animate() {
        final float start = y;
        final float end = y - 50;
        final ObjectAnimator up = ObjectAnimator.ofFloat(Marker.this, "y", start, end).setDuration(ANIMATION_DURATION);
        final ObjectAnimator down = ObjectAnimator.ofFloat(Marker.this, "y", end, start).setDuration(ANIMATION_DURATION);
        if (animatorSet != null) {
            animatorSet.end();
        } else {
            animatorSet = new AnimatorSet();
        }
        animatorSet.playSequentially(up, down);
        animatorSet.start();
    }

    public boolean isAnimating() {
        return animatorSet != null && animatorSet.isRunning();
    }

    @Override
    public String toString() {
        return "Marker{" +
                "radius=" + radius +
                ", sectionMin=" + sectionMin +
                ", sectionMax=" + sectionMax +
                ", x=" + x +
                ", y=" + y +
                ", mContext=" + mContext +
                ", bitmap=" + bitmap +
                '}';
    }
}
