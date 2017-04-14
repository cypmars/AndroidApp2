package com.polytech.androidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.polytech.androidapp.R;

public class PlaceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        String nameString = intent.getStringExtra("name");
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(nameString);
    }
}
