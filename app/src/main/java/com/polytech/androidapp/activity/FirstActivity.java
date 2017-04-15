package com.polytech.androidapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
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
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FirstActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    PlaceAdapter.ViewHolder finalHolder;
    ListViewCompat maListView;
    double longitude;
    double latitude;
    private ArrayList<Place> places = new ArrayList<Place>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        Log.e("Client Google: ", mGoogleApiClient.toString());
        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        topToolBar.setNavigationIcon(R.drawable.menu24white);
        topToolBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent preference = new Intent(FirstActivity.this, PreferenceActivity.class);
                        startActivity(preference);
                    }
                }
        );

        Log.e("ListView: ", "Je suis la ");
        maListView = (ListViewCompat) findViewById(R.id.list);

        Log.e("ListView: ", "Je suis la ");
        //Localisation Android
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider ="";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // do request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8);
            }
        }
        /*bestProvider = LocationManager.GPS_PROVIDER;
        Log.e("provider: ", bestProvider);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            latitude = location.getLatitude ();
            longitude = location.getLongitude ();
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
        Log.e("latitude", String.valueOf(latitude));
        */
        latitude = 43.2410117;
        longitude = 5.3966877000000295;

        // On récupère le json de la requête
        String url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&sort=dist";
        Log.e("url: ",url_request);
        new JSONTask().execute(url_request);
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
        if(id == R.id.action_refresh){
            Intent preference = new Intent(FirstActivity.this, FirstActivity.class);
            startActivity(preference);
        }
        if(id == R.id.action_carte){
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

            ArrayList<Place> temp = new ArrayList<>();
            try {

                // make an jsonObject in order to parse the response
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    Place place = new Place();
                    if (jsonArray.getJSONObject(i).has("place_id")) {
                        place.setPlace_id(jsonArray.getJSONObject(i).optString("place_id"));
                        place.setName(jsonArray.getJSONObject(i).optString("name"));
                        place.setAddress(jsonArray.getJSONObject(i).optString("address"));
                        place.setLatitude(jsonArray.getJSONObject(i).optDouble("latitude"));
                        place.setLongitude(jsonArray.getJSONObject(i).optDouble("longitude"));

                        ArrayList<String> typesArrayList= new ArrayList<>();
                        JSONArray arrayTypes = jsonArray.getJSONObject(i).getJSONArray("types");
                        for (int j = 0; j < arrayTypes.length(); j++){
                            typesArrayList.add(arrayTypes.optString(j));
                        }
                        place.setTypes(typesArrayList);

                        place.setRating(jsonArray.getJSONObject(i).optInt("rating"));

                        if(jsonArray.getJSONObject(i).has("phoneNumber")){
                            place.setPhoneNumber(jsonArray.getJSONObject(i).optString("phoneNumber"));
                        }
                        if (jsonArray.getJSONObject(i).has("website")){
                            place.setWebsite(jsonArray.getJSONObject(i).optString("website"));
                        }

                        HorairesHebdo horairesHebdo = new HorairesHebdo();
                        if (jsonArray.getJSONObject(i).has("horaires_hebdo") && !jsonArray.getJSONObject(i).isNull("horaires_hebdo")){
                            JSONArray arrayHoraires = jsonArray.getJSONObject(i).getJSONObject("horaires_hebdo").getJSONArray("horaires_jour");
                            ArrayList<HorairesJour> arrayJour = new ArrayList<>();
                            for (int k = 0; k < arrayHoraires.length(); k++)
                            {
                                HorairesJour horairesJour= new HorairesJour();
                                horairesJour.setOuverture(arrayHoraires.getJSONObject(k).optString("ouverture"));
                                horairesJour.setFermeture(arrayHoraires.getJSONObject(k).optString("fermeture"));
                                arrayJour.add(horairesJour);
                            }
                            horairesHebdo.setHoraires_jour(arrayJour);

                            horairesHebdo.setHorairesHebdo(jsonArray.getJSONObject(i).getJSONObject("horaires_hebdo").optString("horairesHebdo"));
                            place.setHoraires_hebdo(horairesHebdo);
                        }

                        Photo photo = new Photo();
                        if (jsonArray.getJSONObject(i).has("photoRef") && !jsonArray.getJSONObject(i).isNull("photoRef"))
                        {
                            photo.setHeight(jsonArray.getJSONObject(i).getJSONObject("photoRef").optInt("height"));
                            photo.setWidth(jsonArray.getJSONObject(i).getJSONObject("photoRef").optInt("width"));
                            photo.setReference(jsonArray.getJSONObject(i).getJSONObject("photoRef").optString("reference"));
                            place.setPhotoRef(photo);
                        }

                        ArrayList<Comment> commentArrayList= new ArrayList<>();
                        if (jsonArray.getJSONObject(i).has("comment") && !jsonArray.getJSONObject(i).isNull("comment")){
                            JSONArray arrayComment = jsonArray.getJSONObject(i).getJSONArray("comment");
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
                        /*if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                    poi.setOpenNow("Oui");
                                } else {
                                    poi.setOpenNow("Non");
                                }
                            }
                        } else {
                            poi.setOpenNow("Non connu");
                        }*/
                    }
                    temp.add(place);
                    Log.e("place: ", place.toString());
                }
                places =temp;


            } catch (Exception e) {
                e.printStackTrace();
            }
            /*maListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    this.onItemClick(parent, view, position);
                }

                private void onItemClick(AdapterView<?> adapter, View v, int position) {

                    String place_id = ((Place) (adapter.getItemAtPosition(position))).getPlace_id();
                    String name = ((Place) (adapter.getItemAtPosition(position))).getName();
                    String address = ((Place) (adapter.getItemAtPosition(position))).getAddress();
                    double latitude = ((Place) (adapter.getItemAtPosition(position))).getLatitude();
                    double longitude = ((Place) (adapter.getItemAtPosition(position))).getLongitude();
                    ArrayList<String> types= ((Place) (adapter.getItemAtPosition(position))).getTypes();
                    int rating = ((Place) (adapter.getItemAtPosition(position))).getRating();
                    String phoneNumber= ((Place) (adapter.getItemAtPosition(position))).getPhoneNumber();
                    String website= ((Place) (adapter.getItemAtPosition(position))).getWebsite();
                    HorairesHebdo horaires_hebdo = ((Place) (adapter.getItemAtPosition(position))).getHoraires_hebdo();
                    Photo photoRef =((Place) (adapter.getItemAtPosition(position))).getPhotoRef();
                    ArrayList<Comment> commentArrayList = ((Place) (adapter.getItemAtPosition(position))).getComment();

                    Log.e("id_place: ", place_id);
                    Intent intent = new Intent(FirstActivity.this, PlaceDetailActivity.class);
                    intent.putExtra("place_id", place_id);
                    intent.putExtra("name", name);
                    intent.putExtra("address", address);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("types", types);
                    intent.putExtra("rating", rating);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("website", website);
                    intent.putExtra("horaire_hebdo", horaires_hebdo);
                    intent.putExtra("photoRef", photoRef);
                    intent.putExtra("comments", commentArrayList);

                    //based on item add info to intent
                    startActivity(intent);
                }
            });*/
            PlaceAdapter adapter = new PlaceAdapter(getApplicationContext(), R.layout.row_place, places);
            maListView.setAdapter(adapter);
        }
    }

    public class PlaceAdapter extends ArrayAdapter {

        private List<Place> list_places;
        private int resource;
        private LayoutInflater inflater;
        public PlaceAdapter(Context context, int resource, List<Place> objects) {
            super(context, resource, objects);
            list_places = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

                convertView = inflater.inflate(resource, null);
                holder.name = (TextView)convertView.findViewById(R.id.name);

                holder.image = (ImageView)convertView.findViewById(R.id.imagePlace) ;
                holder.dist = (TextView)convertView.findViewById(R.id.dist);
                holder.open = (TextView)convertView.findViewById(R.id.open);
                holder.rate = (RatingBar)convertView.findViewById(R.id.rate);
                holder.num = (Button)convertView.findViewById(R.id.buttonNum);
                holder.www = (Button)convertView.findViewById(R.id.buttonWeb);
                convertView.setTag(holder);

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(FirstActivity.this, PlaceDetailActivity.class);
                        intent.putExtra("ourlatitude", latitude);
                        intent.putExtra("ourlongitude", longitude);
                        intent.putExtra("place", list_places.get(position));

                        //based on item add info to intent
                        startActivity(intent);
                    }
                });

            // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
            String placeId=list_places.get(position).getPlace_id();
            final ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
                @Override
                public void onResult(PlacePhotoResult placePhotoResult) {
                    if (!placePhotoResult.getStatus().isSuccess()) {
                        return;
                    }
                    holder.image.setImageBitmap(placePhotoResult.getBitmap());
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
                                        holder.image.getWidth(),
                                        holder.image.getHeight())
                                .setResultCallback(mDisplayPhotoResultCallback);
                    }
                    photoMetadataBuffer.release();
                }
            });
            //distance
            float res[] =  new float[1];
            Location.distanceBetween(list_places.get(position).getLatitude(), list_places.get(position).getLongitude(), latitude, longitude, res);
            Log.e("distance: ", String.valueOf(res[0]));
            float dist = ((int) res[0])/1000.0f;

            if (res != null)
            {
                holder.dist.setText(String.valueOf(dist)+" km");
            }
            holder.name.setText(list_places.get(position).getName());
            holder.rate.setRating(list_places.get(position).getRating());
            holder.num.setText(list_places.get(position).getPhoneNumber());

            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY) + 2;
            Log.e("heure :", String.valueOf(hour));
            int minute = calendar.get(Calendar.MINUTE);
            if (list_places.get(position).getHoraires_hebdo() != null)
            {
                if (list_places.get(position).isOpen(dayOfWeek, hour, minute) == 1){
                    holder.open.setTextColor(Color.GREEN);
                    holder.open.setText("Ouvert");
                }
                else if(list_places.get(position).isOpen(dayOfWeek, hour, minute) == 0)
                    holder.open.setText("Fermé");
                else
                    holder.open.setText("N/D");
            }
            else{
                holder.open.setText("N/D");
            }

            return convertView;
        }


        class ViewHolder{
            private TextView name;
            private TextView dist;
            private TextView open;
            private RatingBar rate;
            private Button num;
            private ImageView image;
            private Button www;
        }

    }

}
