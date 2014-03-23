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
    private int mCenterBackgroundColor = 0;
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mCircleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Paint mCirclePaint;
    private static final float CIRCLE_WEIGHT_LONG_ORIENTATION = 0.8f;
    private static final float CIRCLE_TO_MARKER_PADDING = 20f;
    private float mMarkerRadius = 40;
    private float mMarkerStartingPoint;

    private CircularViewAdapter mAdapter;
    private final AdapterDataSetObserver mAdapterDataSetObserver = new AdapterDataSetObserver();

    private ArrayList<Marker> mMarkerList;
    private CircularViewObject mCircle;
    private float mCircleCenter;
    private float mHighlightedDegree;
    private boolean mAnimateMarkerOnHighlight;
    private boolean mIsAnimating;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private int mWidth;
    private int mHeight;

    public static final int TOP = 270;
    public static final int BOTTOM = 90;
    public static final int LEFT = 180;
    public static final int RIGHT = 0;

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
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularView, defStyle, 0);

        mText = a.getString(
                R.styleable.CircularView_text);
        mCenterBackgroundColor = a.getColor(
                R.styleable.CircularView_centerBackgroundColor,
                mCenterBackgroundColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.CircularView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.CircularView_centerDrawable)) {
            mCircleDrawable = a.getDrawable(
                    R.styleable.CircularView_centerDrawable);
            mCircleDrawable.setCallback(this);
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

        mHighlightedDegree = 0f;
        mAnimateMarkerOnHighlight = false;
        mIsAnimating = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // init circle dimens
        final int shortDimension = Math.min(
                mHeight = getMeasuredHeight(),
                mWidth = getMeasuredWidth());
        final float circleRadius = (shortDimension * CIRCLE_WEIGHT_LONG_ORIENTATION - mMarkerRadius * 4f - CIRCLE_TO_MARKER_PADDING * 2f) / 2f;
        mCircleCenter = shortDimension / 2f;
        mCircle = new CircularViewObject(getContext(),
                mCircleCenter, mCircleCenter,
                circleRadius, CIRCLE_TO_MARKER_PADDING,
                mCenterBackgroundColor);
        mCircle.setSrc(mCircleDrawable);
        mCircle.setAdapterDataSetObserver(mAdapterDataSetObserver);
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
        setupMarkerList();
    }

    private void setupMarkerList() {
        if (mAdapter != null) {
            // init marker dimens
            final int markerCount = mAdapter.getCount();
            assert (markerCount >= 0);
            if (mMarkerList == null) {
                mMarkerList = new ArrayList(markerCount);
            }
            final int markerViewListSize = mMarkerList.size();
            final float degreeInterval = 360.0f / markerCount;
            final float radiusFromCenter = mCircle.getRadius() + CIRCLE_TO_MARKER_PADDING + mMarkerRadius;
            int position = 0;
            // loop clockwise
            for (float i = 0; i < 360f; i += degreeInterval) {
                final boolean positionHasExistingMarkerInList = position < markerViewListSize;
                final float actualDegree = normalizeDegree(i + 90f);
                final double rad = Math.toRadians(actualDegree);
                final float sectionMin = actualDegree - degreeInterval / 2f;

                // get the old marker view if it exists.
                final Marker oldMarker;
                if (positionHasExistingMarkerInList) {
                    oldMarker = mMarkerList.get(position);
                } else {
                    oldMarker = null;
                }

                // get the new marker view.
                final Marker newMarker = mAdapter.getMarker(position, oldMarker);
                assert (newMarker != null);

                // Initialize all other necessary values
                newMarker.init(
                        (float) (radiusFromCenter * Math.cos(rad)) + mCircleCenter,
                        (float) (radiusFromCenter * Math.sin(rad)) + mCircleCenter,
                        mMarkerRadius,
                        normalizeDegree(sectionMin),
                        normalizeDegree(sectionMin + degreeInterval) - 0.001f,
                        mAdapterDataSetObserver);
                // Make sure it's drawable has the callback set
                newMarker.setCallback(this);

                // Add to list
                if (positionHasExistingMarkerInList) {
                    mMarkerList.set(position, newMarker);
                } else {
                    mMarkerList.add(newMarker);
                }
                position++;
            }
            mMarkerList.trimToSize();
        }
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
//        mTextWidth = mTextPaint.measureText(mText);
        mTextWidth = mTextPaint.measureText(String.valueOf(mHighlightedDegree));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    //TODO always draw the animating markers on top.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = mWidth - paddingLeft - paddingRight;
        int contentHeight = mHeight - paddingTop - paddingBottom;

        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);
        // Draw CircularViewObject
        mCircle.draw(canvas);
        if (mCircleDrawable != null) {
            mCircleDrawable.draw(canvas);
        }
        // Draw Markers
        if (mMarkerList != null && !mMarkerList.isEmpty()) {
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setColor(Color.BLUE);
            for (final Marker marker : mMarkerList) {
                if ((mIsAnimating || mAnimateMarkerOnHighlight) && marker.hasInSection(mHighlightedDegree % 360)) {
                    if (!marker.isAnimating()) {
                        marker.animateBounce();
                    }
                    mCirclePaint.setStyle(Paint.Style.FILL);
                    marker.draw(canvas);
                    mCirclePaint.setStyle(Paint.Style.STROKE);
                } else {
                    marker.draw(canvas);
                }
            }
        }

        // Draw line
        if (mIsAnimating) {
            final float radiusFromCenter = mCircle.getRadius() + CIRCLE_TO_MARKER_PADDING + mMarkerRadius;
            final float x = (float) Math.cos(Math.toRadians(mHighlightedDegree)) * radiusFromCenter + mCircleCenter;
            final float y = (float) Math.sin(Math.toRadians(mHighlightedDegree)) * radiusFromCenter + mCircleCenter;
            canvas.drawLine(mCircleCenter, mCircleCenter, x, y, mCirclePaint);
        }


        // Draw the text.
        if (mIsAnimating) {
            canvas.drawText(String.valueOf(mHighlightedDegree),
                    mCircle.getX() - mTextWidth / 2f,
                    mCircle.getY() - mTextHeight / 2f,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
                    mTextPaint);
        }
    }

    /**
     * Set the adapter to use on this view.
     *
     * @param adapter Adapter to set.
     */
    public void setAdapter(final CircularViewAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mAdapterDataSetObserver);
        }
    }

    /**
     * Get the adapter that has been set on this view.
     *
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
     * Gets the center background color attribute value.
     *
     * @return The center background color attribute value.
     */
    public int getCenterBackgroundColor() {
        return mCircle == null ? mCenterBackgroundColor : mCircle.getCenterBackgroundColor();
    }

    /**
     * Sets the view's center background color attribute value.
     *
     * @param centerBackgroundColor The color attribute value to use.
     */
    public void setCenterBackgroundColor(int centerBackgroundColor) {
        mCenterBackgroundColor = centerBackgroundColor;
        if (mCircle != null) {
            mCircle.setCenterBackgroundColor(mCenterBackgroundColor);
        }
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
     * Gets the drawable for the center circle.
     *
     * @return The center circle drawable.
     */
    public Drawable getCenterDrawable() {
        return mCircle == null ? mCircleDrawable : mCircle.getDrawable();
    }

    /**
     * Sets the drawable for the center circle.
     *
     * @param centerDrawable The example drawable attribute value to use.
     */
    public void setCenterDrawable(final Drawable centerDrawable) {
        mCircleDrawable = centerDrawable;
        if (mCircle != null) {
            mCircle.setSrc(centerDrawable);
        }
    }

    public float getHighlightedDegree() {
        return mHighlightedDegree;
    }

    /**
     * Set the degree that will trigger highlighting a marker.
     *
     * @param highlightedDegree Value in degrees.
     */
    public void setHighlightedDegree(final float highlightedDegree) {
        this.mHighlightedDegree = highlightedDegree;
        invalidateTextPaintAndMeasurements();
        invalidate();
    }

    /**
     * Check if a marker should animate when it is highlighted. By default this is false and when it is
     * set to true the marker will constantly be animating.
     *
     * @return True if a marker should animate when it is highlighted, false if not.
     * @see #setHighlightedDegree(float)
     */
    public boolean isAnimateMarkerOnHighlight() {
        return mAnimateMarkerOnHighlight;
    }

    /**
     * If set to true the marker that is highlighted with {@link #setHighlightedDegree(float)} will
     * animate continuously. This is set to false by default.
     *
     * @param animateMarkerOnHighlight True to continuously animate, false to turn it off.
     */
    public void setAnimateMarkerOnHighlight(boolean animateMarkerOnHighlight) {
        this.mAnimateMarkerOnHighlight = animateMarkerOnHighlight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            animateHighlightedDegree();
        }
        return true;
    }

    public void setOnCenterClickListener(OnClickListener l) {
        //TODO
    }

    // TODO make variable
    public void animateHighlightedDegree() {
        mHighlightedDegree = 90f;
        final float end = 450f + (float) (Math.random() * 720f);
        final int markerCount = mMarkerList == null ? 0 : mMarkerList.size();
        // animate the highlighted degree value but also make sure it isn't so fast that it's skipping marker animations.
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(CircularView.this, "highlightedDegree", 90f, end)
                .setDuration((long) (Marker.ANIMATION_DURATION * 2L * end / (270L - markerCount)));
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.v(TAG, "animation: start");
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(TAG, "animation: end");
                mIsAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    /**
     * Get the starting point for the markers.
     *
     * @return The starting point for the markers.
     */
    public float getMarkerStartingPoint() {
        return mMarkerStartingPoint;
    }

    /**
     * Set the starting point for the markers
     *
     * @param startingPoint Starting point for the markers
     */
    public void setMarkerStartingPoint(final float startingPoint) {
        mMarkerStartingPoint = startingPoint;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Remove all callback references from the center circle
        mCircle.setCallback(null);
        // Remove all callback references from the markers
        if (mMarkerList != null) {
            for (final Marker marker : mMarkerList) {
                marker.setCallback(null);
            }
        }
        // Unregister adapter observer
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataSetObserver);
        }
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