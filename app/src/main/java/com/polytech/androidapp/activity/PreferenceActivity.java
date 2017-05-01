package com.polytech.androidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.constraint.solver.SolverVariable;
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

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.polytech.androidapp.R;
import com.polytech.androidapp.model.Comment;
import com.polytech.androidapp.model.HorairesHebdo;
import com.polytech.androidapp.model.Photo;
import com.polytech.androidapp.model.Place;
import com.polytech.androidapp.model.Result;
import com.polytech.androidapp.model.TypeCategorie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PreferenceActivity extends AppCompatActivity {
    private ListViewCompat maListView;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        topToolBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent back = new Intent(PreferenceActivity.this, FirstActivity.class);
                        startActivity(back);
                    }
                }
        );

        maListView = (ListViewCompat) findViewById(R.id.list);

        TypeCategorie categorie;
        ArrayList<TypeCategorie> listPreference = new ArrayList<>();
        categorie = new TypeCategorie("Culture", R.drawable.culture);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Sport", R.drawable.sport);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Santé", R.drawable.sante);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Loisir", R.drawable.loisirs);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Bar/Restaurant", R.drawable.resto);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Magasin", R.drawable.shop);
        listPreference.add(categorie);
        categorie = new TypeCategorie("Transport", R.drawable.transport);
        listPreference.add(categorie);
        categorie = new TypeCategorie("DAB", R.drawable.dab);
        listPreference.add(categorie);

        PreferenceAdapter adapter = new PreferenceAdapter(getApplicationContext(), R.layout.row_pref, listPreference);
        maListView.setAdapter(adapter);

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
                    intent = new Intent(PreferenceActivity.this, PlaceDetailActivity.class) ;
                    intent.putExtra("place_id", ((Result) parent.getItemAtPosition(position)).getPlace_id());
                    intent.putExtra("description", ((Result) parent.getItemAtPosition(position)).getDescription());
                    intent.putExtra("ourlatitude", latitude);
                    intent.putExtra("ourlongitude", longitude);
                }
                else
                {
                    intent = new Intent(PreferenceActivity.this, FirstActivity.class) ;
                    intent.putExtra("place_id", "null");
                    intent.putExtra("description", ((Result) parent.getItemAtPosition(position)).getDescription());
                };
                startActivity(intent);
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_pref, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){
            Intent advanceSearch = new Intent(PreferenceActivity.this, AdvanceSearchActivity.class);
            startActivity(advanceSearch);
        }
        return super.onOptionsItemSelected(item);
    }

    public class PreferenceAdapter extends ArrayAdapter {

        private List<TypeCategorie> listPreference;
        private int resource;
        private LayoutInflater inflater;

        public PreferenceAdapter(Context context, int resource, List<TypeCategorie> objects) {
            super(context, resource, objects);
            this.listPreference = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(resource, null);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.image = (ImageView)convertView.findViewById(R.id.image) ;

            convertView.setTag(holder);

            holder.name.setText(listPreference.get(position).getName());
            holder.image.setImageResource(listPreference.get(position).getImageDrawable());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PreferenceActivity.this, FirstActivity.class);
                    String name_categorie = listPreference.get(position).getName();
                    intent.putExtra("name_categorie", name_categorie);

                    //based on item add info to intent
                    startActivity(intent);
                }
            });

            return convertView;
        }


        class ViewHolder{
            private TextView name;
            private ImageView image;
        }

    }
}
