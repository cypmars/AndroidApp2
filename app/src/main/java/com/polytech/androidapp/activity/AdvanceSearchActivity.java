package com.polytech.androidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.polytech.androidapp.R;
import com.polytech.androidapp.utils.MultiSpinner;

import java.util.ArrayList;
import java.util.HashMap;

public class AdvanceSearchActivity extends AppCompatActivity implements MultiSpinner.multispinnerListener {

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
        spinner.setAdapter(adapter_rayon);

        final ArrayList<String> arrayTypes = new ArrayList<>();
        arrayTypes.add("museum");
        arrayTypes.add("art_gallery");
        arrayTypes.add("library");
        arrayTypes.add("university");
        arrayTypes.add("book_store");
        arrayTypes.add("school");
        arrayTypes.add("church");
        arrayTypes.add("hindu_temple");
        arrayTypes.add("airport");
        arrayTypes.add("bus_station");
        arrayTypes.add("parking");
        arrayTypes.add("subway_station");
        arrayTypes.add("taxi_stand");
        arrayTypes.add("train_station");
        arrayTypes.add("gas_station");
        arrayTypes.add("bank");
        arrayTypes.add("atm");
        arrayTypes.add("gym");
        arrayTypes.add("stadium");
        arrayTypes.add("restaurant");
        arrayTypes.add("bakery");
        arrayTypes.add("bar");
        arrayTypes.add("cafe");
        arrayTypes.add("food");
        arrayTypes.add("amusement_park");
        arrayTypes.add("park");
        arrayTypes.add("casino");
        arrayTypes.add("aquarium");
        arrayTypes.add("movie_theater");
        arrayTypes.add("zoo");
        arrayTypes.add("bowling_alley");
        arrayTypes.add("night_club");
        arrayTypes.add("store");
        arrayTypes.add("florist");
        arrayTypes.add("shoe_store");
        arrayTypes.add("electronics_store");
        arrayTypes.add("convenience_store");
        arrayTypes.add("grocery_or_supermarket");
        arrayTypes.add("home_goods_store");
        arrayTypes.add("clothing_store");
        arrayTypes.add("spa");
        arrayTypes.add("hair_care");
        arrayTypes.add("beauty_salon");
        arrayTypes.add("health");
        arrayTypes.add("dentist");
        arrayTypes.add("doctor");
        arrayTypes.add("hospital");
        arrayTypes.add("pharmacy");
        arrayTypes.add("veterinary_care");
        spinner2.setItems(arrayTypes, "Selectionner un (ou plusieurs) types", this);
        spinner2.setFocusable(true);
        spinner2.requestFocus();

        ArrayList<String> arrayRankby = new ArrayList<>();
        arrayRankby.add("Choisissez un mode de tri");
        arrayRankby.add("Pertinence");
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launch_search = new Intent(AdvanceSearchActivity.this, FirstActivity.class);

                boolean tabBool[] = new boolean[5];
                tabBool[0] = false;     //Rayon
                tabBool[1] = false;     //Types
                tabBool[2] = false;     //Price
                tabBool[3] = false;     //Rankby
                tabBool[4] = false;     //OpenNow

                String rayonValue = spinner.getSelectedItem().toString();

                final ArrayList<String> arrayTypes = new ArrayList<String>();
                for (int i = 0; i < arrayTypes.size(); i++)
                {
                    if (spinner2.isCheck(i))
                        arrayTypes.add(arrayTypes.get(i));
                }

                int maxPrice = seekBar.getProgress();
                String rankByValue = spinner3.getSelectedItem().toString();
                Boolean openNow = checkBox.isChecked();

                if (!rayonValue.equals("Choisissez un rayon")) {
                    launch_search.putExtra("rayon", rayonValue);
                    tabBool[0] = true;
                }

                if(!arrayTypes.isEmpty()){
                    launch_search.putExtra("types", arrayTypes);
                    tabBool[1]=true;
                }

                if (maxPrice != 0 ) {
                    launch_search.putExtra("maxPrice", maxPrice);
                    tabBool[2] = true;
                }

                if (!rankByValue.equals("Choisissez un mode de tri")){
                    launch_search.putExtra("tri", rankByValue);
                    tabBool[3] = true;
                }

                if(openNow) {
                    launch_search.putExtra("openNow", openNow);
                    tabBool[4] = true;
                }

                launch_search.putExtra("tab_bool", tabBool);
                if(tabBool[0])
                    startActivity(launch_search);

                else{
                    Toast.makeText(AdvanceSearchActivity.this, "Vous devez renseignez le rayon de la recherche",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemschecked(boolean[] checked) {

    }
}
