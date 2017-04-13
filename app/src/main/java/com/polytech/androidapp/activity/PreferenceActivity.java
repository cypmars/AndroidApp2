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
import android.widget.Button;
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
import com.polytech.androidapp.model.TypeCategorie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PreferenceActivity extends AppCompatActivity {
    private ListViewCompat maListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        setTitle(null);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo_nearby_2);

        maListView = (ListViewCompat) findViewById(R.id.list);
        maListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                this.onItemClick(parent, view, position);
            }

            private void onItemClick(AdapterView<?> adapter, View v, int position) {
                String name_categorie = ((TypeCategorie) adapter.getItemAtPosition(position)).getName();
                Intent intent = new Intent(PreferenceActivity.this, FirstActivity.class);
                intent.putExtra("name_categorie", name_categorie);
                //based on item add info to intent
                startActivity(intent);
            }
        });
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
        if(id == R.id.action_pref){
            Intent preference = new Intent(PreferenceActivity.this, PreferenceActivity.class);
            startActivity(preference);

        }
        if(id == R.id.action_carte){
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
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            convertView = inflater.inflate(resource, null);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.image = (ImageView)convertView.findViewById(R.id.image) ;

            convertView.setTag(holder);

            holder.name.setText(listPreference.get(position).getName());
            holder.image.setImageResource(listPreference.get(position).getImageDrawable());

            return convertView;
        }


        class ViewHolder{
            private TextView name;
            private ImageView image;
        }

    }
}
