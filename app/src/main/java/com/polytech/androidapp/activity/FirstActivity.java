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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.polytech.androidapp.model.Aspect;
import com.polytech.androidapp.model.Comment;
import com.polytech.androidapp.model.HorairesHebdo;
import com.polytech.androidapp.model.HorairesJour;
import com.polytech.androidapp.model.Photo;
import com.polytech.androidapp.model.Place;
import com.polytech.androidapp.model.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class FirstActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient mGoogleApiClient;

    ListViewCompat maListView;
    double longitude;
    double latitude;
    private ArrayList<Place> places = new ArrayList<Place>();
    private AdapterView<android.widget.Adapter> adapterView;
    private View view;
    private int position;
    private long id;

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

        topToolBar.setNavigationIcon(R.drawable.carte24white);
        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(FirstActivity.this, MapActivity.class);
                map.putExtra("places", places) ;
                map.putExtra("longitude", longitude) ;
                map.putExtra("latitude", latitude) ;
                startActivity(map);
            }
        });

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


        /*autocomplete search bar initialisation*/
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_autocomplete));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteTextView.setText("");
                Result result = (Result) parent.getItemAtPosition(position);
                Intent intent;
                if (result.getPlace_id() != null)
                {
                    intent = new Intent(FirstActivity.this, PlaceDetailActivity.class) ;
                    intent.putExtra("place_id", ((Result) parent.getItemAtPosition(position)).getPlace_id());
                    intent.putExtra("description", ((Result) parent.getItemAtPosition(position)).getDescription());
                    intent.putExtra("ourlatitude", latitude);
                    intent.putExtra("ourlongitude", longitude);
                }
                else
                {
                    intent = new Intent(FirstActivity.this, FirstActivity.class) ;
                    intent.putExtra("place_id", "null");
                    intent.putExtra("description", ((Result) parent.getItemAtPosition(position)).getDescription());
                };
                startActivity(intent);
            }
        });

        // On récupère le json de la requête
        Intent intent = getIntent();
        String url_request ="";
        if(intent != null)
        {
            //Recherche par préférence
            if (intent.getStringExtra("name_categorie") != null)
            {
                String name_categorie = intent.getStringExtra("name_categorie");
                url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&pref="+ name_categorie;
            }
            else if (intent.getStringExtra("place_id") != null)
            {
                String recherche = intent.getStringExtra("description");
                String[] piece = recherche.split(" ");
                recherche ="";
                for (int i = 0 ; i < piece.length; i++)
                {
                    recherche = piece[i] +"+"+recherche;
                }
                url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&sort=dist&search="+recherche;
            }
            else if (intent.getStringExtra("rayon") != null){
                String rayon = intent.getStringExtra("rayon");
                url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&rayon=" + (Integer.parseInt(rayon.substring(0, rayon.length()-2))*1000);
                if (intent.getStringArrayListExtra("types") != null) {
                    ArrayList<String> arrayTypes = intent.getStringArrayListExtra("types");
                    String typesString = "";
                    for (int i = 0; i < arrayTypes.size(); i++) {
                        typesString += arrayTypes.get(i) + "=" ;
                    }
                    url_request = url_request + "&types=" + typesString;
                    Log.e("TYPESTRING ", typesString);
                }

                if (intent.getIntExtra("maxPrice", 0) != 0) {
                    String maxPrice = String.valueOf(intent.getIntExtra("maxPrice", 0));
                    url_request = url_request + "&maxprice=" + maxPrice ;
                }

                if (intent.getStringExtra("tri") != null) {
                    String tri = intent.getStringExtra("tri");
                    url_request = url_request + "&tri=" + tri;
                }

                if(intent.getBooleanExtra("openNow", false)){
                    String open = String.valueOf(intent.getBooleanExtra("openNow", false));
                    url_request = url_request + "&open=" + open;
                }
            }
            else
            {
                url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&sort=dist";
            }
        }
        else
        {
            url_request = "https://nearbyappli.herokuapp.com/greeting?latitude=" + latitude + "&longitude=" + longitude + "&sort=dist";
        }
        Log.e("url: ",url_request);
        new JSONTask().execute(url_request);
    }

    public static ArrayList<Result> autocomplete(String input) {
        ArrayList<Result> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            String key = "AIzaSyA8dc_npRU5uwQdlpV1QkOdDYUQtlHGEj8";
            //String str = new String("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + URLEncoder.encode(input, "utf8") + "&types=establishment&language=fr&key=" + key) ;
            String str = new String("https://maps.googleapis.com/maps/api/place/queryautocomplete/json?key="+ key+ "&language=fr&input="+ URLEncoder.encode(input, "utf8")) ;
            URL url = new URL(str.toString());

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read ;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("aaa", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("bbb", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Result result = new Result();
                if (predsJsonArray.getJSONObject(i).has("place_id"))
                {
                    result.setPlace_id(predsJsonArray.getJSONObject(i).getString("place_id"));
                }
                result.setDescription(predsJsonArray.getJSONObject(i).getString("description"));
                resultList.add(result);

                //System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                //System.out.println("============================================================");
                //resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("cccc", "Cannot process JSON results", e);
        }
        return resultList;

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<Result> implements Filterable {
        private ArrayList<Result> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public int getCount() {
            return resultList.size();
        }

        public Result getItem(int index) {
            return resultList.get(index);
        }

        public String getDescription(int index){
            return resultList.get(index).getDescription() ;
        }

        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
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
            Intent refresh = new Intent(FirstActivity.this, FirstActivity.class);
            startActivity(refresh);
        }
        if(id == R.id.advanceSearch){
            Intent advanceSearch = new Intent(FirstActivity.this, AdvanceSearchActivity.class);
            startActivity(advanceSearch);
        }
        if(id == R.id.searchByTheme){
            Intent theme = new Intent(FirstActivity.this, PreferenceActivity.class);
            startActivity(theme);
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

                            JSONArray arrayHorairesString = jsonArray.getJSONObject(i).getJSONObject("horaires_hebdo").getJSONArray("horairesHebdo");
                            ArrayList<String> arrayString = new ArrayList<>();
                            for (int l = 0; l < arrayHorairesString.length(); l++){
                                arrayString.add(arrayHorairesString.optString(l));
                            }
                            horairesHebdo.setHorairesHebdo(arrayString);
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
        }
    }


}
