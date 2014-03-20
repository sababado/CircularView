package com.sababado.circularview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class CircularView extends View {
    private static final String TAG = CircularView.class.getSimpleName();
    private String mText; //TODO add customization for the text (style, color, etc)
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Paint mCirclePaint;
    private static final float CIRCLE_WEIGHT_LONG_ORIENTATION = 0.8f;
    private static final float CIRCLE_TO_MARKER_PADDING = 20f;
    private float mMarkerRadius = 15;
    private int mMarkerCount = 0;
    private float mMarkerStartingPoint;

    private CircularViewAdapter mAdapter;
    private AdapterDataSetObserver mAdapterDataSetObserver;

    private ArrayList<Marker> mMarkerList;
    private Marker mCircle;
    private float highlightedDegree;
    private boolean isAnimating;

    private int mOrientation;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private int mWidth;
    private int mHeight;

    public static final int TOP = 270;
    public static final int BOTTOM = 90;
    public static final int LEFT = 180;
    public static final int RIGHT = R.styleable.CircularView_markerStartingPoint;

    public CircularView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mAdapterDataSetObserver = new AdapterDataSetObserver();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularView, defStyle, 0);

        mText = a.getString(
                R.styleable.CircularView_text);
        mExampleColor = a.getColor(
                R.styleable.CircularView_exampleColor,
                mExampleColor);
        mMarkerCount = a.getInt(R.styleable.CircularView_markerCount, 0);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.CircularView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.CircularView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.CircularView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        mCirclePaint = new Paint();
        mCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);

        highlightedDegree = Float.MAX_VALUE;
        isAnimating = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mOrientation = getResources().getConfiguration().orientation;

        // init circle dimens
        final int shortDimension = Math.min(
                mHeight = getMeasuredHeight(),
                mWidth = getMeasuredWidth());
        Log.v(TAG, "shortDimensinon: " + shortDimension);
        final float circleRadius = (shortDimension * CIRCLE_WEIGHT_LONG_ORIENTATION - mMarkerRadius * 4f - CIRCLE_TO_MARKER_PADDING * 2f) / 2f;
        final float circleCenter = shortDimension / 2f;
        mCircle = new Marker(getContext(), circleCenter, circleCenter, circleRadius);

        // init marker dimens
        if (mMarkerList != null) {
            mMarkerList.clear();
        } else {
            mMarkerList = new ArrayList(mMarkerCount);
        }
        final float degreeInterval = 360.0f / mMarkerCount;
        final float radiusFromCenter = circleRadius + CIRCLE_TO_MARKER_PADDING + mMarkerRadius;
        // loop clockwise
        for (float i = 0; i < 360f; i += degreeInterval) {
            final float actualDegree = normalizeDegree(i + 90f);
            final double rad = Math.toRadians(actualDegree);
            final float sectionMin = actualDegree - degreeInterval / 2f;
            final Marker marker = new Marker(
                    getContext(),
                    (float) (radiusFromCenter * Math.cos(rad)) + circleCenter,
                    (float) (radiusFromCenter * Math.sin(rad)) + circleCenter,
                    mMarkerRadius,
                    normalizeDegree(sectionMin),
                    normalizeDegree(sectionMin + degreeInterval));
            marker.setSrc(R.drawable.ic_launcher);
            mMarkerList.add(marker);
            Log.v(TAG, "marker: " + marker);
        }
    }

    /**
     * Make sure a degree value is less than or equal to 360 and greater than or equal to 0.
     *
     * @param degree Degree to normalize
     * @return Return a positive degree value
     */
    private float normalizeDegree(float degree) {
        if (degree < 0f) {
            degree = 360f + degree;
        }
        return degree % 360f;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mText);
        mTextWidth = mTextPaint.measureText(String.valueOf(highlightedDegree));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);
        // Draw Circle
        mCircle.draw(canvas, mCirclePaint);
        // Draw Markers
        if (mMarkerList != null && !mMarkerList.isEmpty()) {
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setColor(Color.BLUE);
            for (final Marker marker : mMarkerList) {
                if (isAnimating && marker.hasInSection(highlightedDegree % 360)) {
                    mCirclePaint.setStyle(Paint.Style.FILL);
                    marker.draw(canvas, marker.radius + 8, null);
                    mCirclePaint.setStyle(Paint.Style.STROKE);
                } else {
                    marker.draw(canvas, null);
                }
            }
        }

        // Draw the text.
        if (isAnimating) {
            canvas.drawText(String.valueOf(highlightedDegree),
                    mCircle.x - mTextWidth / 2f,
                    mCircle.y - mTextHeight / 2f,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
                    mTextPaint);
        }

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    /**
     * Set the adapter to use on this view.
     * @param adapter Adapter to set.
     */
    public void setAdapter(final CircularViewAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * Get the adapter that has been set on this view.
     * @return
     * @see #setAdapter(CircularViewAdapter)
     */
    public CircularViewAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Gets the text for this view.
     *
     * @return The text for this view.
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the view's text.
     *
     * @param text The view's text.
     */
    public void setText(String text) {
        mText = text;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public float getHighlightedDegree() {
        Log.v(TAG, "animation: in getHighlightedDegree: " + highlightedDegree);
        return highlightedDegree;
    }

    /**
     * Set the degree to highlight.
     *
     * @param highlightedDegree Value in degrees.
     */
    public void setHighlightedDegree(float highlightedDegree) {
        this.highlightedDegree = highlightedDegree;
        Log.v(TAG, "animation: in setHighlightedDegree: " + highlightedDegree);
        invalidateTextPaintAndMeasurements();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startAnimation();
        }
        return true;
    }

    private Handler mHandler;

    public void startAnimation() {
//        if (mHandler == null) {
//            mHandler = new Handler();
//        }
//        mHandler.removeCallbacks(mAnimationRunnable);
        highlightedDegree = 0;
//        mHandler.post(mAnimationRunnable);
        final float end = 450f + (float) (Math.random() * 720f);
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(CircularView.this, "highlightedDegree", 90f, end)
                .setDuration(2160);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.v(TAG, "animation: start");
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(TAG, "animation: end");
//                setHighlightedDegree(Float.MIN_VALUE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

//    private Runnable mAnimationRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.v(TAG, "running animation: " + mCurrentAnimatingMarker);
//            if (mMarkerList != null && !mMarkerList.isEmpty()) {
//                Log.v(TAG, "running animation: list is good");
////                if (mCurrentAnimatingMarker > 0) {
////                    mMarkerList.get(mCurrentAnimatingMarker - 1).animate(false);
////                }
//                final int size = mMarkerList.size();
//                if (mCurrentAnimatingMarker < size) {
//                    mMarkerList.get(mCurrentAnimatingMarker).animate();
//                }
//                mCurrentAnimatingMarker++;
//                if (mCurrentAnimatingMarker < size) {
//                    mHandler.postDelayed(mAnimationRunnable, 100);
//                }
//            }
//        }
//    };

    /**
     * Get the starting point for the markers.
     * @return The starting point for the markers.
     */
    public float getMarkerStartingPoint() {
        return mMarkerStartingPoint;
    }

    /**
     * Set the starting point for the markers
     * @param startingPoint Starting point for the markers
     */
    public void setMarkerStartingPoint(final float startingPoint) {
        mMarkerStartingPoint = startingPoint;
    }

    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            invalidate();
        }

        /**
         * Does the same thing as {@link #onChanged()}.
         */
        @Override
        public void onInvalidated() {
            onChanged();
        }
    }
}