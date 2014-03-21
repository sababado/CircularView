package com.sababado.circularview;

public abstract class CircularViewAdapter{
    /**
     * Get the count of how many markers will show around the circular view.
     * @return Number of markers to show.
     */
    public abstract int getCount();

    /**
     * Get the marker that should show at a given position. The position will be between 0 and the value returned by {@link #getCount()}.
     * @param position Position of the marker to show.
     * @param oldMarkerView The old marker that was used the last time this was called for this position. Use this object if it isn't null to optimize the adapter.
     * @return A non null marker to show.
     */
    public abstract MarkerView getMarker(int position, MarkerView oldMarkerView);
}
