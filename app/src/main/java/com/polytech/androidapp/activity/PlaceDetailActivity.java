package com.polytech.androidapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

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
                        Intent preference = new Intent(PlaceDetailActivity.this, FirstActivity.class);
                        startActivity(preference);
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
        String phoneString = intent.getStringExtra("name");
        String websiteString = intent.getStringExtra("name");
        int rateInt = intent.getIntExtra("rating",0 );
        double latitude = intent.getDoubleExtra("latitude", 0.00000);
        double longitude = intent.getDoubleExtra("longitude", 0.00000);

        ArrayList<String> typeList = intent.getStringArrayListExtra("types");
        HorairesHebdo horairesHebdo = intent.getParcelableExtra("horaires_hebdo");
        ArrayList<Comment> commentArrayList = intent.getParcelableArrayListExtra("comments");

        name.setText(nameString);
        address.setText(addressString);
        rate.setRating(rateInt);
        phone.setText(phoneString);
        website.setText(websiteString);



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
