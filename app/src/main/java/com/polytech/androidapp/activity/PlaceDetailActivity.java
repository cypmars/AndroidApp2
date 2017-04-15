package com.polytech.androidapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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

        ListViewCompat TypeListView = (ListViewCompat) findViewById(R.id.typelist);
        ListViewCompat HoursListView = (ListViewCompat) findViewById(R.id.hourslist);
        ListViewCompat CommentListView = (ListViewCompat) findViewById(R.id.commentlist);

        Intent intent = getIntent();
        String nameString = intent.getStringExtra("name");
        String addressString = intent.getStringExtra("address");
        String phoneString = intent.getStringExtra("phoneNumber");
        String websiteString = intent.getStringExtra("website");
        int rateInt = intent.getIntExtra("rating",0 );
        double ourlatitude = intent.getDoubleExtra("ourlatitude", 0.00000);
        double ourlongitude = intent.getDoubleExtra("ourlongitude", 0.00000);
        double latitude = intent.getDoubleExtra("latitude", 0.00000);
        double longitude = intent.getDoubleExtra("longitude", 0.00000);

        List<String> typeList = intent.getStringArrayListExtra("types");
        HorairesHebdo horairesHebdo = intent.getParcelableExtra("horaires_hebdo");
        ArrayList<Comment> commentArrayList = intent.getParcelableArrayListExtra("comments");

        Place place = intent.getParcelableExtra("place");

        name.setText(nameString);
        address.setText(addressString);
        rate.setRating(rateInt);
        phone.setText(phoneString);
        website.setText(websiteString);

        float res[] =  new float[1];
        Location.distanceBetween(latitude, longitude, ourlatitude, ourlongitude, res);
        Log.e("distance: ", String.valueOf(res[0]));
        float distance = ((int) res[0])/1000.0f;

        if (res != null)
        {
            dist.setText(String.valueOf(distance)+" km");
        }


        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY) + 2;
        Log.e("heure :", String.valueOf(hour));
        int minute = calendar.get(Calendar.MINUTE);
        if (horairesHebdo != null)
        {
            if (place.isOpen(dayOfWeek, hour, minute) == 1){
                open.setTextColor(Color.GREEN);
                open.setText("Ouvert");
            }
            else if(place.isOpen(dayOfWeek, hour, minute) == 0)
                open.setText("Fermé");
            else
                open.setText("N/D");
        }
        else{
            open.setText("N/D");
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
            Intent preference = new Intent(PlaceDetailActivity.this, FirstActivity.class);
            startActivity(preference);
        }
        if(id == R.id.action_carte){
        }
        return super.onOptionsItemSelected(item);
    }

}
