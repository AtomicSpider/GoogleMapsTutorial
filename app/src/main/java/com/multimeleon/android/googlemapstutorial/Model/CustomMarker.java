package com.multimeleon.android.googlemapstutorial.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Peter-John on 2017-04-11.
 * GoogleMapsTutorial
 */

public class CustomMarker implements ClusterItem {

    private final LatLng mPosition;
    private String title;
    private String snippet;

    public CustomMarker(double lat, double lng, String title, String snippet) {
        this.title = title;
        this.snippet = snippet;
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
