package com.polytech.androidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.polytech.androidapp.model.Aspect;
import com.polytech.androidapp.model.Comment;
import com.polytech.androidapp.model.HorairesHebdo;
import com.polytech.androidapp.model.HorairesJour;
import com.polytech.androidapp.model.Photo;
import com.polytech.androidapp.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TypesHorizontalAdapter horizontalAdapter;
    private double latitude, longitude;
    GoogleApiClient mGoogleApiClient;

    ImageView imageTitle;
    TextView open;
    RatingBar rate;
    TextView dist;
    TextView name;
    TextView address;

    TextView phone;
    TextView website;

    RecyclerView typeHorizontalView;
    ListViewCompat hoursListView;
    LinearLayout linearComment;

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

        imageTitle = (ImageView) findViewById(R.id.imageTitle);
        open = (TextView) findViewById(R.id.open);
        rate = (RatingBar) findViewById(R.id.rate);
        dist = (TextView) findViewById(R.id.dist);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);

        phone = (TextView) findViewById(R.id.num);
        website = (TextView) findViewById(R.id.website);

        typeHorizontalView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        hoursListView = (ListViewCompat) findViewById(R.id.hourslist);
        linearComment = (LinearLayout) findViewById(R.id.linearComment);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("ourlatitude", 0.00000);
        longitude = intent.getDoubleExtra("ourlongitude", 0.00000);
        String placeId ="";

        if (intent.getStringExtra("place_id")!=null)
        {
            String url_request = "https://nearbyappli.herokuapp.com/detail?place_id="+intent.getStringExtra("place_id");
            placeId = intent.getStringExtra("place_id");
            new JSONTask().execute(url_request);
        }
        else
        {
            Place place = intent.getParcelableExtra("place");
            name.setText(place.getName());
            address.setText(place.getAddress());
            rate.setRating(place.getRating());
            phone.setText(place.getPhoneNumber());
            website.setText(place.getWebsite());

            placeId = place.getPlace_id();

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

            final int adapterCount = commentAdapter.getCount();

            for (int i = 0; i < adapterCount; i++) {
                View item = commentAdapter.getView(i, null, null);
                linearComment.addView(item);
            }

        }

        // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
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
        if(id == R.id.advanceSearch){
            Intent advanceSearch = new Intent(PlaceDetailActivity.this, AdvanceSearchActivity.class);
            startActivity(advanceSearch);
        }
        if(id == R.id.searchByTheme){
            Intent theme = new Intent(PlaceDetailActivity.this, PreferenceActivity.class);
            startActivity(theme);
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

            if (list_comment.get(position).getRating() >= 0 && list_comment.get(position).getRating() <2){
                holder.rating.setTextColor(Color.RED);
            }
            else if (list_comment.get(position).getRating() >= 2 && list_comment.get(position).getRating() <4){
                holder.rating.setTextColor(Color.YELLOW);
            }
            else{
                holder.rating.setTextColor(Color.GREEN);
            }
            holder.rating.setText(String.valueOf(list_comment.get(position).getRating()));
            return convertView;
        }

        class ViewHolder {
            private TextView person_name;
            private TextView commentaire;
            private TextView rating;
        }
    }

    private class JSONTask extends AsyncTask<String,Integer, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String finalJson;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                finalJson = buffer.toString();

                return finalJson;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(final String result) {
            Place place = new Place();
            try {

                // make an jsonObject in order to parse the response
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("place_id")) {
                    place.setPlace_id(jsonObject.optString("place_id"));
                    place.setName(jsonObject.optString("name"));
                    place.setAddress(jsonObject.optString("address"));
                    place.setLatitude(jsonObject.optDouble("latitude"));
                    place.setLongitude(jsonObject.optDouble("longitude"));

                    ArrayList<String> typesArrayList= new ArrayList<>();
                    JSONArray arrayTypes = jsonObject.getJSONArray("types");
                    for (int j = 0; j < arrayTypes.length(); j++){
                        typesArrayList.add(arrayTypes.optString(j));
                    }
                    place.setTypes(typesArrayList);

                    place.setRating(jsonObject.optInt("rating"));

                    if(jsonObject.has("phoneNumber")){
                        place.setPhoneNumber(jsonObject.optString("phoneNumber"));
                    }
                    if (jsonObject.has("website")){
                        place.setWebsite(jsonObject.optString("website"));
                    }

                    HorairesHebdo horairesHebdo = new HorairesHebdo();
                    if (jsonObject.has("horaires_hebdo") && !jsonObject.isNull("horaires_hebdo")){
                        JSONArray arrayHoraires = jsonObject.getJSONObject("horaires_hebdo").getJSONArray("horaires_jour");
                        ArrayList<HorairesJour> arrayJour = new ArrayList<>();
                        for (int k = 0; k < arrayHoraires.length(); k++)
                        {
                            HorairesJour horairesJour= new HorairesJour();
                            horairesJour.setOuverture(arrayHoraires.getJSONObject(k).optString("ouverture"));
                            horairesJour.setFermeture(arrayHoraires.getJSONObject(k).optString("fermeture"));
                            arrayJour.add(horairesJour);
                        }
                        horairesHebdo.setHoraires_jour(arrayJour);

                        JSONArray arrayHorairesString = jsonObject.getJSONObject("horaires_hebdo").getJSONArray("horairesHebdo");
                        ArrayList<String> arrayString = new ArrayList<>();
                        for (int l = 0; l < arrayHorairesString.length(); l++){
                            arrayString.add(arrayHorairesString.optString(l));
                        }
                        horairesHebdo.setHorairesHebdo(arrayString);
                        place.setHoraires_hebdo(horairesHebdo);
                    }

                    Photo photo = new Photo();
                    if (jsonObject.has("photoRef") && !jsonObject.isNull("photoRef"))
                    {
                        photo.setHeight(jsonObject.getJSONObject("photoRef").optInt("height"));
                        photo.setWidth(jsonObject.getJSONObject("photoRef").optInt("width"));
                        photo.setReference(jsonObject.getJSONObject("photoRef").optString("reference"));
                        place.setPhotoRef(photo);
                    }

                    ArrayList<Comment> commentArrayList= new ArrayList<>();
                    if (jsonObject.has("comment") && !jsonObject.isNull("comment")){
                        JSONArray arrayComment = jsonObject.getJSONArray("comment");
                        for (int j = 0; j < arrayComment.length(); j++){
                            Comment comment = new Comment();
                            comment.setAuteur(arrayComment.getJSONObject(j).optString("auteur"));
                            comment.setCommentaire(arrayComment.getJSONObject(j).optString("commentaire"));
                            comment.setLanguage(arrayComment.getJSONObject(j).optString("language"));
                            comment.setRating(arrayComment.getJSONObject(j).optInt("rating"));
                            comment.setTime(arrayComment.getJSONObject(j).optInt("time"));

                            if (arrayComment.getJSONObject(j).has("aspects") && !arrayComment.getJSONObject(j).isNull("aspects"))
                            {
                                ArrayList<Aspect> aspectArrayList = new ArrayList<>();
                                JSONArray arrayAspect = arrayComment.getJSONObject(j).getJSONArray("aspects");
                                for (int k = 0; k < arrayAspect.length(); k++)
                                {
                                    Aspect aspect = new Aspect();
                                    aspect.setRating(arrayAspect.getJSONObject(k).optInt("rating"));
                                    aspect.setType(arrayAspect.getJSONObject(k).optString("type"));
                                    aspectArrayList.add(aspect);
                                }
                                comment.setAspects(aspectArrayList);
                            }
                            commentArrayList.add(comment);
                        }
                    }
                    place.setComment(commentArrayList);
                }
                Log.e("place: ", place.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            name.setText(place.getName());
            address.setText(place.getAddress());
            rate.setRating(place.getRating());
            phone.setText(place.getPhoneNumber());
            website.setText(place.getWebsite());

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
        }
    }
}
