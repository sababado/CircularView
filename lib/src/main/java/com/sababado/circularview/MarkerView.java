package com.sababado.circularview;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class MarkerView extends View{
    float radius;
    float sectionMin;
    float sectionMax;
    private Bitmap bitmap;

    public MarkerView(final Context context) {
        super(context);
    }

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    MarkerView(final Context context, final float x, final float y, final float radius) {
        this(context);
        setX(x);
        setY(y);
        this.radius = radius;
    }

    MarkerView(final Context context, final float x, final float y, final float radius, final float sectionMin, final float sectionMax) {
        this(context, x, y, radius);
        this.sectionMin = sectionMin;
        this.sectionMax = sectionMax;
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

    @Override
    protected void onDraw(final Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap,
                    getX() - bitmap.getWidth() / 2f,
                    getY() - bitmap.getHeight() / 2f,
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

        // TODO Invalidate.
    }

    /**
     * Set the marker's visual by using a resource id.
     *
     * @param resId Resource id of the drawable to display.
     */
    public void setSrc(final int resId) {
        setSrc(BitmapFactory.decodeResource(getResources(), resId));
    }

    public boolean hasInSection(final float x) {
        if (sectionMin <= sectionMax) {
            return x <= sectionMax && x >= sectionMin;
        }
        final float endDifference = 360f - sectionMin;
        return (x <= sectionMax && x >= -endDifference) ||
                (x <= sectionMax + endDifference + sectionMin && x >= sectionMin);
    }

//    public void animate() {
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playSequentially(
//                ObjectAnimator.ofFloat(this, "x", x + 50).setDuration(500),
//                ObjectAnimator.ofFloat(this, "x", x).setDuration(500));
//    }

    public void animateMe() {
        animator = animate();
        post(animateUpDown);
    }

    public boolean isAnimating() {
        return animator != null;
    }

    ViewPropertyAnimator animator;
    final Runnable animateUpDown = new Runnable() {
        @Override
        public void run() {
            final float y = getY();
            animator.y(y - 50).setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animator.y(y).setDuration(500)
                                    .setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            animator = null;
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {
                                        }
                                    }).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
        }
    };

    @Override
    public String toString() {
        return "MarkerView{" +
                ", radius=" + radius +
                ", sectionMin=" + sectionMin +
                ", sectionMax=" + sectionMax +
                '}';
    }
}