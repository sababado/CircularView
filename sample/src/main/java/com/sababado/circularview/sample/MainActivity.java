package com.sababado.circularview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sababado.circularview.CircularView;
import com.sababado.circularview.CircularViewObject;
import com.sababado.circularview.SimpleCircularViewAdapter;
import com.sababado.circularview.Marker;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MySimpleCircularViewAdapter mAdapter;
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
            public void onClick(final CircularView view) {
                Toast.makeText(MainActivity.this, "Clicked center", Toast.LENGTH_SHORT).show();

                // Start animation from the bottom of the circle, going clockwise.
                final float start = CircularView.BOTTOM;
                final float end = start + 360f + (float) (Math.random() * 720f);
                // animate the highlighted degree value but also make sure it isn't so fast that it's skipping marker animations.
                final long duration = (long) (Marker.ANIMATION_DURATION * 2L * end / (270L - mAdapter.getCount()));

                circularView.animateHighlightedDegree(start, end, duration);
            }

            public void onMarkerClick(CircularView view, Marker marker, int position) {
                Toast.makeText(MainActivity.this, "Clicked " + marker.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        circularView.setOnHighlightAnimationEndListener(new CircularView.OnHighlightAnimationEndListener() {
            @Override
            public void onHighlightAnimationEnd(CircularView view, Marker marker, int position) {
                Toast.makeText(MainActivity.this, "Spin ends on " + marker.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MySimpleCircularViewAdapter extends SimpleCircularViewAdapter {
        int count = 29;

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public void setupMarker(final int position, final Marker marker) {
            marker.setSrc(R.drawable.center_bg);
            marker.setFitToCircle(true);
            marker.setRadius(10 + 2 * position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        boolean handled = false;
        if (id == R.id.increment) {
            mAdapter.count++;
            handled = true;
            mAdapter.notifyDataSetChanged();
        } else if (id == R.id.decrement) {
            mAdapter.count--;
            handled = true;
            mAdapter.notifyDataSetChanged();
        }
        Toast.makeText(MainActivity.this, "Object count " + mAdapter.getCount(), Toast.LENGTH_SHORT).show();
        return handled || super.onOptionsItemSelected(item);
    }
}