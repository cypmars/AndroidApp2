package com.polytech.androidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.polytech.androidapp.R;
import com.polytech.androidapp.model.Comment;
import com.polytech.androidapp.model.Place;

import java.util.Calendar;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TypesHorizontalAdapter horizontalAdapter;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        topToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        topToolBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        final ImageView imageTitle = (ImageView) findViewById(R.id.imageTitle);
        TextView open = (TextView) findViewById(R.id.open);
        RatingBar rate = (RatingBar) findViewById(R.id.rate);
        TextView dist = (TextView) findViewById(R.id.dist);
        TextView name = (TextView) findViewById(R.id.name);
        TextView address = (TextView) findViewById(R.id.address);

        TextView phone = (TextView) findViewById(R.id.num);
        TextView website = (TextView) findViewById(R.id.website);

        RecyclerView typeHorizontalView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        ListViewCompat hoursListView = (ListViewCompat) findViewById(R.id.hourslist);
        ListViewCompat commentListView = (ListViewCompat) findViewById(R.id.commentlist);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("ourlatitude", 0.00000);
        double longitude = intent.getDoubleExtra("ourlongitude", 0.00000);

        Place place = intent.getParcelableExtra("place");

        name.setText(place.getName());
        address.setText(place.getAddress());
        rate.setRating(place.getRating());
        phone.setText(place.getPhoneNumber());
        website.setText(place.getWebsite());

        // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
        String placeId=place.getPlace_id();
        final ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
            @Override
            public void onResult(PlacePhotoResult placePhotoResult) {
                if (!placePhotoResult.getStatus().isSuccess()) {
                    return;
                }
                imageTitle.setImageBitmap(placePhotoResult.getBitmap());
            }
        };

/**
 * Load a bitmap from the photos API asynchronously
 * by using buffers and result callbacks.
 */
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(PlacePhotoMetadataResult photos) {
                if (!photos.getStatus().isSuccess()) {
                    return;
                }

                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    photoMetadataBuffer.get(0)
                            .getScaledPhoto(mGoogleApiClient,
                                    imageTitle.getWidth(),
                                    imageTitle.getHeight())
                            .setResultCallback(mDisplayPhotoResultCallback);
                }
                photoMetadataBuffer.release();
            }
        });

        float res[] = new float[1];
        Location.distanceBetween(latitude, longitude, place.getLatitude(), place.getLongitude(), res);
        Log.e("distance: ", String.valueOf(res[0]));
        float distance = ((int) res[0]) / 1000.0f;

        if (res != null) {
            dist.setText(String.valueOf(distance) + " km");
        }


        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY) + 2;
        Log.e("heure :", String.valueOf(hour));
        int minute = calendar.get(Calendar.MINUTE);
        if (place.getHoraires_hebdo() != null) {
            if (place.isOpen(dayOfWeek, hour, minute) == 1) {
                open.setTextColor(Color.GREEN);
                open.setText("Ouvert");
            } else if (place.isOpen(dayOfWeek, hour, minute) == 0)
                open.setText("Fermé");
            else
                open.setText("N/D");
        } else {
            open.setText("N/D");
        }

        Log.e("types= ", place.getTypes().toString());
        horizontalAdapter = new TypesHorizontalAdapter(place.getTypes());
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(PlaceDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        typeHorizontalView.setLayoutManager(horizontalLayoutManager);

        typeHorizontalView.setAdapter(horizontalAdapter);

        if (place.getHoraires_hebdo() != null)
        {
            HoursAdapter hoursAdapter = new HoursAdapter(getApplicationContext(), R.layout.row_hours, place.getHoraires_hebdo().getHorairesHebdo());
            hoursListView.setAdapter(hoursAdapter);
        }

        CommentsAdapter commentAdapter = new CommentsAdapter(getApplicationContext(), R.layout.row_comments, place.getComment());
        commentListView.setAdapter(commentAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Intent preference = new Intent(PlaceDetailActivity.this, FirstActivity.class);
            startActivity(preference);
        }
        if (id == R.id.action_carte) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class TypesHorizontalAdapter extends RecyclerView.Adapter<TypesHorizontalAdapter.MyViewHolder> {

        private List<String> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView typeTextView;

            public MyViewHolder(View view) {
                super(view);
                typeTextView = (TextView) view.findViewById(R.id.type);

            }
        }


        public TypesHorizontalAdapter(List<String> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_types, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.typeTextView.setText(horizontalList.get(position));

            holder.typeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

    public class HoursAdapter extends ArrayAdapter {

        private List<String> list_jours;
        private int resource;
        private LayoutInflater inflater;

        public HoursAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            list_jours = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(resource, null);
            holder.HoraireJourString = (TextView) convertView.findViewById(R.id.textHorairesJour);
            convertView.setTag(holder);
            holder.HoraireJourString.setText(list_jours.get(position));

            return convertView;
        }

        class ViewHolder {
            private TextView HoraireJourString;
        }
    }

    public class CommentsAdapter extends ArrayAdapter<Comment> {

        private List<Comment> list_comment;
        private int resource;
        private LayoutInflater inflater;

        public CommentsAdapter(Context context, int resource, List<Comment> objects) {
            super(context, resource, objects);
            list_comment = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(resource, null);
            holder.person_name = (TextView) convertView.findViewById(R.id.person_name);
            holder.commentaire = (TextView) convertView.findViewById(R.id.commentaire);
            holder.rating = (TextView) convertView.findViewById(R.id.person_rating);
            convertView.setTag(holder);

            holder.person_name.setText(list_comment.get(position).getAuteur());
            holder.commentaire.setText(list_comment.get(position).getCommentaire());
            holder.rating.setText(String.valueOf(list_comment.get(position).getRating()));
            return convertView;
        }

        class ViewHolder {
            private TextView person_name;
            private TextView commentaire;
            private TextView rating;
        }
    }
}
