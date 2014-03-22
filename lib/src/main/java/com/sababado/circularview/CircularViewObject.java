package com.sababado.circularview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rjszabo on 3/22/14.
 */
public class CircularViewObject {
    private static final AtomicInteger sAtomicIdCounter = new AtomicInteger(0);
    protected int id;
    protected float radius;
    protected float x;
    protected float y;
    protected Context mContext;
    protected Bitmap bitmap;
    protected CircularView.AdapterDataSetObserver mAdapterDataSetObserver;

    /**
     * Create a new CircularViewObject with the current context.
     * @param context Current context.
     */
    public CircularViewObject(final Context context) {
        mContext = context;
        id = sAtomicIdCounter.getAndAdd(1);
    }

    CircularViewObject(final Context context, final float x, final float y, final float radius) {
        this(context);
        init(x, y, radius, null);
    }

    protected void init(final float x, final float y, final float radius, final CircularView.AdapterDataSetObserver adapterDataSetObserver) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mAdapterDataSetObserver = adapterDataSetObserver;
    }

    protected void draw(final Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap,
                    x - bitmap.getWidth() / 2f,
                    y - bitmap.getHeight() / 2f,
                    null);
        }
    }

    /**
     * Get this Object's unique ID. The ID is generated atomically on initialization.
     * @return Atomically generated ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the object's bitmap.
     *
     * @return The object's bitmap.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Set the object's visual as a bitmap.
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
     * Set the object's visual by using a resource id.
     *
     * @param resId Resource id of the drawable to display.
     */
    public void setSrc(final int resId) {
        setSrc(BitmapFactory.decodeResource(mContext.getResources(), resId));
    }

    /**
     * Get the y position.
     * @return The y position.
     */
    public float getY() {
        return y;
    }

    /**
     * Set the y position.
     * @param y The new y position.
     */
    public void setY(float y) {
        this.y = y;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    /**
     * Get the x position.
     * @return The x position.
     */
    public float getX() {
        return x;
    }

    /**
     * Set the x position.
     * @param x The new x position.
     */
    public void setX(float x) {
        this.x = x;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    /**
     * Get the radius of the object.
     * @return The radius.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the radius of the object.
     * @param radius The new radius.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        if(mAdapterDataSetObserver != null) {
            mAdapterDataSetObserver.onChanged();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircularViewObject circularViewObject = (CircularViewObject) o;

        if (id != circularViewObject.id) return false;
        if (Float.compare(circularViewObject.radius, radius) != 0) return false;
        if (Float.compare(circularViewObject.x, x) != 0) return false;
        if (Float.compare(circularViewObject.y, y) != 0) return false;
        if (bitmap != null ? !bitmap.equals(circularViewObject.bitmap) : circularViewObject.bitmap != null) return false;
        if (mAdapterDataSetObserver != null ? !mAdapterDataSetObserver.equals(circularViewObject.mAdapterDataSetObserver) : circularViewObject.mAdapterDataSetObserver != null)
            return false;
        if (mContext != null ? !mContext.equals(circularViewObject.mContext) : circularViewObject.mContext != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
        result = 31 * result + (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (mContext != null ? mContext.hashCode() : 0);
        result = 31 * result + (bitmap != null ? bitmap.hashCode() : 0);
        result = 31 * result + (mAdapterDataSetObserver != null ? mAdapterDataSetObserver.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CircularViewObject{" +
                "id=" + id +
                ", radius=" + radius +
                ", x=" + x +
                ", y=" + y +
                ", mContext=" + mContext +
                ", bitmap=" + bitmap +
                ", mAdapterDataSetObserver=" + mAdapterDataSetObserver +
                '}';
    }
}