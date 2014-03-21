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
     * Get the marker that should show at a given position. The position will be between 0 and the value returned by {@link #getCount()}.
     *
     * @param position  Position of the marker to show.
     * @param oldMarker The old marker that was used the last time this was called for this position. Use this object if it isn't null to optimize the adapter.
     * @return A non null marker to show.
     */
    public abstract Marker getMarker(int position, Marker oldMarker);

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
