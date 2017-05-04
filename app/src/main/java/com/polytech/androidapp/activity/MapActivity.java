package com.polytech.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

/**
 * Created by Laora on 03/05/2017.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap map;
    private Place placeT = new Place() ;
    ArrayList<Place> places = new ArrayList<>() ;

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
        for (Place p : places) {
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))) ;

            placeT = p ;
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.infos_window, null);

                    ImageView image = (ImageView) v.findViewById(R.id.image) ;
                    TextView name = (TextView) v.findViewById(R.id.name);
                    TextView adresse = (TextView) v.findViewById(R.id.adresse);
                    TextView telephone = (TextView) v.findViewById(R.id.telephone);

                    //for(Place p : places){
                      //  LatLng position = new LatLng(p.getLatitude(), p.getLongitude()) ;
                        //if(marker.getPosition() == position) {
                            name.setText(placeT.getName());
                            adresse.setText(placeT.getAddress());
                            telephone.setText(placeT.getPhoneNumber());
                        //}
                    //}

                    return v;
                }
            });
        }

        Marker markerPosition = map.addMarker(new MarkerOptions()
                                        .position(myPosition)
                                        .title("Ma position")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13));
    }
}
