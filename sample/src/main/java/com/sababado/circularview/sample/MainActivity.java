package com.sababado.circularview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sababado.circularview.CircularView;
import com.sababado.circularview.CircularViewAdapter;
import com.sababado.circularview.Marker;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private CircularViewAdapter mAdapter;
    private CircularView circularView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new MyCircularViewAdapter();

        circularView = (CircularView) findViewById(R.id.circular_view);
        circularView.setAdapter(mAdapter);
//        circularView.setAnimateMarkerOnHighlight(true);

        circularView.setOnCenterCircleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start animation from the bottom of the circle, going clockwise.
                final float start = CircularView.BOTTOM;
                final float end = start + 360f + (float) (Math.random() * 720f);
                // animate the highlighted degree value but also make sure it isn't so fast that it's skipping marker animations.
                final long duration = (long) (Marker.ANIMATION_DURATION * 2L * end / (270L - mAdapter.getCount()));

                circularView.animateHighlightedDegree(start, end, duration);
            }
        });
    }

    public class MyCircularViewAdapter extends CircularViewAdapter {
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public void setupMarker(final int position, final Marker marker) {
            marker.setSrc(R.drawable.ic_launcher);
            marker.setFitToCircle(true);
            marker.setRadius(10 + 2 * position);
        }
    }
}