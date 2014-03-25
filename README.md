CircularView
============
[![Build Status](https://travis-ci.org/sababado/CircularView.svg?branch=master)](https://travis-ci.org/sababado/CircularView)

A custom view for Android. It consists of a larger center circle that it surrounded by other circles. Each of the surrounding circles (or `CircularViewObject`'s

![Quick example screenshot](http://s27.postimg.org/a9nzujq8z/Circular_View_example.png)

##Usage
The `CircularView` can be definied in a XML layout or in code. 

##Quick Setup
###1. Adding the view to a layout
```XML
<com.sababado.circularview.CircularView
    android:id="@+id/circular_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:centerBackgroundColor="#33b5e5"
    app:centerDrawable="@drawable/center_bg"/>
```

Using the custom attributes requires the following in the layout file. [Example](sample/src/main/res/layout/activity_main.xml)
```
xmlns:app="http://schemas.android.com/apk/res-auto"
```

##Customization
