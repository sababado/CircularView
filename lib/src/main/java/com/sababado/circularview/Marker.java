package com.sababado.circularview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;

/**
 * TODO Document
 */
public class Marker extends CircularViewObject {
    private float sectionMin;
    private float sectionMax;
    private boolean isHighlighted;
    private boolean shouldAnimateWhenHighlighted;

    public final static int ANIMATION_DURATION = 650;
    private AnimatorSet animatorSet;

    /**
     * Create a new Marker with the current context.
     *
     * @param context Current context.
     */
    Marker(final Context context) {
        super(context);
        isHighlighted = false;
        shouldAnimateWhenHighlighted = false;
    }

    void init(final float x, final float y, final float radius, final float sectionMin, final float sectionMax, final CircularView.AdapterDataSetObserver adapterDataSetObserver) {
        super.init(x, y, radius, adapterDataSetObserver);
        this.sectionMin = sectionMin;
        this.sectionMax = sectionMax;
    }

    public boolean hasInSection(final float x) {
        if (sectionMin <= sectionMax) {
            return x <= sectionMax && x >= sectionMin;
        }
        final float endDifference = 360f - sectionMin;
        return (x <= sectionMax && x >= -endDifference) ||
                (x <= sectionMax + endDifference + sectionMin && x >= sectionMin);
    }

    /**
     * Animate a simple up and down motion.
     */
    public void animateBounce() {
        if (animatorSet != null) {
            animatorSet.end();
        } else {
            animatorSet = createNewBounceAnimation();
        }
        animatorSet.start();
    }

    AnimatorSet createNewBounceAnimation() {
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(Marker.this.isHighlighted && shouldAnimateWhenHighlighted) {
                    animatorSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        final float start = y;
        final float end = y - 25;
        final ObjectAnimator up = ObjectAnimator.ofFloat(Marker.this, "y", start, end).setDuration(ANIMATION_DURATION);
        final ObjectAnimator down = ObjectAnimator.ofFloat(Marker.this, "y", end, start).setDuration(ANIMATION_DURATION);
        animatorSet.playSequentially(up, down);
        return animatorSet;
    }

    public boolean isAnimating() {
        return animatorSet != null && animatorSet.isRunning();
    }

    /**
     * Check if this marker is highlighted.
     * @return Flag that determines if this marker is highlighted.
     */
    public boolean isHighlighted() {
        return isHighlighted;
    }

    /**
     * Set the flag that determines if this marker is highlighted. Setting this to true and then calling
     * {@link #animateBounce()} will allow the animation to repeat.
     * Setting this to false while an animation is running will allow it to gracefully end.
     * @param highlighted True to mark this object as highlighted.
     */
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        mAdapterDataSetObserver.onChanged();
    }

    boolean isShouldAnimateWhenHighlighted() {
        return shouldAnimateWhenHighlighted;
    }

    void setShouldAnimateWhenHighlighted(boolean shouldAnimateWhenHighlighted) {
        this.shouldAnimateWhenHighlighted = shouldAnimateWhenHighlighted;
        mAdapterDataSetObserver.onChanged();
    }

    /**
     * Cancel any running animations on this marker.
     */
    public void cancelAnimation() {
        if(animatorSet != null) {
            this.isHighlighted = false;
            animatorSet.cancel();
        }
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