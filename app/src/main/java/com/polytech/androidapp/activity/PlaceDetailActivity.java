package com.polytech.androidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.polytech.androidapp.R;
import com.polytech.androidapp.model.Comment;
import com.polytech.androidapp.model.HorairesHebdo;
import com.polytech.androidapp.model.Place;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

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

        ImageView imageTitle = (ImageView) findViewById(R.id.imageTitle);
        TextView open = (TextView) findViewById(R.id.open);
        RatingBar rate = (RatingBar) findViewById(R.id.rate);
        TextView dist = (TextView) findViewById(R.id.dist);
        TextView name = (TextView) findViewById(R.id.name);
        TextView address = (TextView) findViewById(R.id.address);

        TextView phone = (TextView) findViewById(R.id.num);
        TextView website = (TextView) findViewById(R.id.website);

        ListViewCompat typeListView = (ListViewCompat) findViewById(R.id.typelist);
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
        Log.e("WARNING!!!!", place.toString());
        TypesAdapter typeAdapter = new TypesAdapter(getApplicationContext(), R.layout.row_types, place.getTypes());
        //typeListView.setAdapter(typeAdapter);

        HoursAdapter hoursAdapter = new HoursAdapter(getApplicationContext(), R.layout.row_hours, place.getHoraires_hebdo().getHorairesHebdo());
        //hoursListView.setAdapter(hoursAdapter);

        CommentsAdapter commentAdapter = new CommentsAdapter(getApplicationContext(), R.layout.row_comments, place.getComment());
        //commentListView.setAdapter(commentAdapter);

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

    public class TypesAdapter extends ArrayAdapter {

        private List<String> list_types;
        private int resource;
        private LayoutInflater inflater;

        public TypesAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            list_types = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(resource, null);
            holder.typeButton = (Button) convertView.findViewById(R.id.buttonType);
            holder.typeButton.setText(list_types.get(position));
            convertView.setTag(holder);

            return convertView;
        }

        class ViewHolder {
            private Button typeButton;
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
            holder.HoraireJourString.setText(list_jours.get(position));
            convertView.setTag(holder);

            return convertView;
        }

        class ViewHolder {
            private TextView HoraireJourString;
        }
    }

    public class CommentsAdapter extends ArrayAdapter {

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
            convertView.setTag(holder);

            return convertView;
        }

        class ViewHolder {
        }
    }
}
