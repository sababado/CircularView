package com.sababado.circularview;

import android.database.DataSetObserver;

/**
 * This adapter facilitates access to markers and is used by {@link com.sababado.circularview.CircularView}.
 */
public interface BaseCircularViewAdapter {
    /**
     * Get the count of how many markers will show around the circular view. This number should be zero or positive.
     *
     * @return Number of markers to show.
     */
    public int getCount();

    /**
     * Setup the marker that should show at a given position. The position will be between 0 and the value returned by {@link #getCount()}.
     *
     * @param position  Position of the marker to show.
     * @param marker The marker that will be used to display.
     */
    public void setupMarker(int position, Marker marker);

    /**
     * Register an observer on this adapter.
     * @param observer observer to register.
     */
    public void registerDataSetObserver(DataSetObserver observer);

    /**
     * Unregister an observer on this adapter.
     * @param observer observer to unregister.
     */
    public void unregisterDataSetObserver(DataSetObserver observer);

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged();

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated();
}
