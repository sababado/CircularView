package com.sababado.circularview;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

public abstract class CircularViewAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    /**
     * Get the count of how many markers will show around the circular view. This number should be zero or positive.
     *
     * @return Number of markers to show.
     */
    public abstract int getCount();

    /**
     * Setup the marker that should show at a given position. The position will be between 0 and the value returned by {@link #getCount()}.
     *
     * @param position  Position of the marker to show.
     * @param marker The marker that will be used to display.
     */
    public abstract void setupMarker(int position, Marker marker);

    void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }
}
