package com.polytech.androidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.polytech.androidapp.R;
import com.polytech.androidapp.utils.MultiSpinner;

import java.util.ArrayList;

public class AdvanceSearchActivity extends AppCompatActivity {

    Spinner spinner, spinner3;
    MultiSpinner spinner2;
    SeekBar seekBar;
    TextView seekBarValue;
    CheckBox checkBox;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);

        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        topToolBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent back = new Intent(AdvanceSearchActivity.this, FirstActivity.class);
                        startActivity(back);
                    }
                }
        );

        // On récupère les vues dont on a besoin
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (MultiSpinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        button = (Button) findViewById(R.id.button3);
        seekBarValue = (TextView) findViewById(R.id.seekBarValue);

        ArrayList<String> arrayRayon = new ArrayList<>();
        arrayRayon.add("Choisissez un rayon");
        arrayRayon.add("1km");
        arrayRayon.add("2km");
        arrayRayon.add("5km");
        arrayRayon.add("10km");
        arrayRayon.add("25km");
        arrayRayon.add("50km");
        ArrayAdapter<String> adapter_rayon = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayRayon);
        adapter_rayon.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter_rayon);

        ArrayList<String> arrayTypes = new ArrayList<>();
        arrayTypes.add("Choisissez un ou plusieurs types");
        arrayTypes.add("");
        arrayTypes.add("");
        ArrayAdapter<String> adapter_types = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayTypes);
        adapter_types.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter_types);

        ArrayList<String> arrayRankby = new ArrayList<>();
        arrayRankby.add("Choisissez un mode de tri");
        arrayRankby.add("Prominence");
        arrayRankby.add("Distance");
        ArrayAdapter<String> adapter_rankby = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayRankby);
        adapter_rankby.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter_rankby);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
