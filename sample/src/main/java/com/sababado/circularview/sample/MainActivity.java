package com.sababado.circularview.sample;

import android.app.Activity;
import android.os.Bundle;

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
    }

    public class MyCircularViewAdapter extends CircularViewAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Marker getMarker(final int position, final Marker oldMarkerView) {
            final Marker markerView;
            if (oldMarkerView != null) {
                markerView = oldMarkerView;
            } else {
                markerView = new Marker(MainActivity.this);
                markerView.setSrc(R.drawable.ic_launcher);
            }
            return markerView;
        }
    }
}