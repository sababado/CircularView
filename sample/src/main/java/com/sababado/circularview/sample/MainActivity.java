package com.sababado.circularview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sababado.circularview.CircularView;
import com.sababado.circularview.CircularViewObject;
import com.sababado.circularview.SimpleCircularViewAdapter;
import com.sababado.circularview.Marker;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SimpleCircularViewAdapter mAdapter;
    private CircularView circularView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new MySimpleCircularViewAdapter();

        circularView = (CircularView) findViewById(R.id.circular_view);
        circularView.setAdapter(mAdapter);

        // Allow markers to continuously animate on their own when the highlight animation isn't running.
        circularView.setAnimateMarkerOnStillHighlight(true);
        // Combine the above line with the following so that the marker at it's position will animate at the start.
        circularView.setHighlightedDegree(circularView.BOTTOM);

        circularView.setOnCircularViewObjectClickListener(new CircularView.OnClickListener() {
            @Override
            public void onClick(final CircularView view, final CircularViewObject circularViewObject) {
                Toast.makeText(MainActivity.this, "Clicked "+circularViewObject.getId(), Toast.LENGTH_SHORT).show();

                // Only animate the highlighted degree if the center circle is clicked.
                if(circularView.getCenterCircle().getId() == circularViewObject.getId()) {
                    // Start animation from the bottom of the circle, going clockwise.
                    final float start = CircularView.BOTTOM;
                    final float end = start + 360f + (float) (Math.random() * 720f);
                    // animate the highlighted degree value but also make sure it isn't so fast that it's skipping marker animations.
                    final long duration = (long) (Marker.ANIMATION_DURATION * 2L * end / (270L - mAdapter.getCount()));

                    circularView.animateHighlightedDegree(start, end, duration);
                }
            }
        });
    }

    public class MySimpleCircularViewAdapter extends SimpleCircularViewAdapter {
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public void setupMarker(final int position, final Marker marker) {
            marker.setSrc(R.drawable.center_bg);
            marker.setFitToCircle(true);
            marker.setRadius(10 + 2 * position);
        }
    }
}