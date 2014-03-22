package com.sababado.circularview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rjszabo on 3/21/14.
 */
public class Marker extends CircularViewObject {
    private float sectionMin;
    private float sectionMax;

    final static int ANIMATION_DURATION = 650;
    private AnimatorSet animatorSet;

    /**
     * Create a new Marker with the current context.
     * @param context Current context.
     */
    public Marker(final Context context) {
        super(context);
    }

    Marker(final Context context, final float x, final float y, final float radius) {
        super(context);
        init(x, y, radius, 0, 0, null);
    }

    void init(final float x, final float y, final float radius, final float sectionMin, final float sectionMax, final CircularView.AdapterDataSetObserver adapterDataSetObserver) {
        super.init(x, y, radius, adapterDataSetObserver);
        this.sectionMin = sectionMin;
        this.sectionMax = sectionMax;
    }

    protected void draw(final Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap,
                    x - bitmap.getWidth() / 2f,
                    y - bitmap.getHeight() / 2f,
                    null);
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
        final float end = y - 25;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Marker marker = (Marker) o;

        if (Float.compare(marker.sectionMax, sectionMax) != 0) return false;
        if (Float.compare(marker.sectionMin, sectionMin) != 0) return false;
        if (animatorSet != null ? !animatorSet.equals(marker.animatorSet) : marker.animatorSet != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sectionMin != +0.0f ? Float.floatToIntBits(sectionMin) : 0);
        result = 31 * result + (sectionMax != +0.0f ? Float.floatToIntBits(sectionMax) : 0);
        result = 31 * result + (animatorSet != null ? animatorSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "sectionMin=" + sectionMin +
                ", sectionMax=" + sectionMax +
                ", animatorSet=" + animatorSet +
                "} " + super.toString();
    }
}