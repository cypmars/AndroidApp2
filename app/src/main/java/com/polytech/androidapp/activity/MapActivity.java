package com.polytech.androidapp.activity;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polytech.androidapp.R;
import com.polytech.androidapp.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Laora on 03/05/2017.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap map;
    private Place placeT;
    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        topToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        topToolBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent back = new Intent(MapActivity.this, FirstActivity.class);
                        startActivity(back);
                    }
                }
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        double longitude = getIntent().getDoubleExtra("longitude", 0.0) ;
        double latitude = getIntent().getDoubleExtra("latitude", 0.0) ;
        LatLng myPosition =  new LatLng(latitude,longitude);

        map.getUiSettings().setZoomControlsEnabled(true) ;
        map.getUiSettings().setMapToolbarEnabled(false) ;

        places = getIntent().getParcelableArrayListExtra("places") ;
        HashMap<LatLng, Place> mapMarker= new HashMap<>();
        for (Place p : places) {
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))) ;
            mapMarker.put(new LatLng(p.getLatitude(), p.getLongitude()), p);
        }

        Marker markerPosition = map.addMarker(new MarkerOptions()
                .position(myPosition)
                .title("Ma position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13));

        YourCustomInfoWindowAdpater yourInfo=new YourCustomInfoWindowAdpater(getApplicationContext(), mapMarker);
        map.setInfoWindowAdapter(yourInfo);
    }

    public class YourCustomInfoWindowAdpater implements GoogleMap.InfoWindowAdapter {
        private final View mymarkerview;
        private Context context;
        private HashMap<LatLng, Place> mapPlaces;

        public YourCustomInfoWindowAdpater(Context context, HashMap<LatLng, Place> mapPlaces) {
            this.context = context;
            this.mapPlaces = mapPlaces;
            mymarkerview = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.infos_window, null);
        }

        public View getInfoWindow( Marker marker) {
            render(marker, mymarkerview);
            return mymarkerview;
        }

        public View getInfoContents(Marker marker) {
            View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.infos_window, null);

            return v;
        }

        private void render(Marker marker, View v) {
            ImageView image = (ImageView) v.findViewById(R.id.image) ;
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView adresse = (TextView) v.findViewById(R.id.adresse);
            TextView telephone = (TextView) v.findViewById(R.id.telephone);

            if (mapPlaces.get(marker.getPosition()) != null)
            {
                name.setText(mapPlaces.get(marker.getPosition()).getName());
                adresse.setText(mapPlaces.get(marker.getPosition()).getAddress());
                telephone.setText(mapPlaces.get(marker.getPosition()).getPhoneNumber());
            }
        }

        public void setModels(HashMap<LatLng, Place> mapPlaces) {
            this.mapPlaces = mapPlaces;
        }
    }
}
