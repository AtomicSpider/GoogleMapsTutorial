package com.multimeleon.android.googlemapstutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.multimeleon.android.googlemapstutorial.Model.CustomMarker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseGoogleMapsActivity implements ClusterManager.OnClusterClickListener<CustomMarker>, ClusterManager.OnClusterInfoWindowClickListener<CustomMarker>, ClusterManager.OnClusterItemClickListener<CustomMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<CustomMarker> {
    
    private ClusterManager<CustomMarker> mClusterManager;

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<CustomMarker> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(CustomMarker person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<CustomMarker> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupMap(googleMap);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.167616, 28.079329), 10));

        mClusterManager = new ClusterManager<>(this, googleMap);

        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        addPersonItems();
        mClusterManager.cluster();
    }

    private void addPersonItems() {
        for (int i = 0; i < 3; i++) {
            mClusterManager.addItem(new CustomMarker(-26.187616, 28.079329, "PJ", "https://twitter.com/pjapplez"));
            mClusterManager.addItem(new CustomMarker(-26.207616, 28.079329, "PJ2", "https://twitter.com/pjapplez"));
            mClusterManager.addItem(new CustomMarker(-26.217616, 28.079329, "PJ3", "https://twitter.com/pjapplez"));
        }
    }

    @Override
    public boolean onClusterClick(Cluster<CustomMarker> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<CustomMarker> cluster) {

    }

    @Override
    public boolean onClusterItemClick(CustomMarker customMarker) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(CustomMarker customMarker) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class RenderClusterInfoWindow extends DefaultClusterRenderer<CustomMarker> {

        RenderClusterInfoWindow(Context context, GoogleMap map, ClusterManager<CustomMarker> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterRendered(Cluster<CustomMarker> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(CustomMarker item, MarkerOptions markerOptions) {
            markerOptions.title(item.getTitle());

            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}
