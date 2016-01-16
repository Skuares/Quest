package com.skuares.studio.quest;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by salim on 1/16/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    GoogleMap googleMapB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMapB  = googleMap;

        // 1- move the camera to user's location

        // might not work now, test devices don't have the place field
        if(MainActivity.myUser != null){
            if(MainActivity.myUser.getaPlace() != null){
                double userLat = MainActivity.myUser.getaPlace().getPlaceLatitude();
                double userLng = MainActivity.myUser.getaPlace().getPlaceLongitude();
                LatLng userLatLng = new LatLng(userLat,userLng);
                // set up the marker
                googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You Are Here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
            }

        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);





        // 2- diplay all the quests markers
        if(StreamFragment.questCards != null){
            MarkersInBackground marker = new MarkersInBackground();
            marker.execute(StreamFragment.questCards);

        }



        // loop through the quests and get the todos ..... WORKING BUT SHOULD BE IN BACGKROUND
        /*
        if(StreamFragment.questCards != null){

            for(int i = 0; i < StreamFragment.questCards.size(); i++){

                QuestCard questCard = StreamFragment.questCards.get(i);
                List<ToDo> toDos = questCard.getTodos();
                // extract one todo at a time
                for(int j = 0; j < toDos.size(); j++){
                    ToDo todo = toDos.get(j);
                    APlace place = todo.getaPlace();
                    double latitude = place.getPlaceLatitude();
                    double longitude = place.getPlaceLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getPlaceName()));
                }
            }
        }
        */

        // get the places from todos
        // assign markers to them
        /*
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));


        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Population: 4,137,400"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
        */
    }

    private class MarkersInBackground extends AsyncTask<List<QuestCard>,Void,List<APlace>>{

        List<QuestCard> questCardsB;
        List<APlace> places;
        @Override
        protected List<APlace> doInBackground(List<QuestCard>... params) {
            questCardsB = params[0];
            places = new ArrayList<APlace>();

            for(int i = 0; i < questCardsB.size(); i++){

                QuestCard questCard = questCardsB.get(i);
                List<ToDo> toDos = questCard.getTodos();
                // extract one todo at a time
                for(int j = 0; j < toDos.size(); j++){
                    ToDo todo = toDos.get(j);
                    APlace place = todo.getaPlace();

                    places.add(place);
                    //double latitude = place.getPlaceLatitude();
                    //double longitude = place.getPlaceLongitude();
                    //LatLng latLng = new LatLng(latitude, longitude);
                    //googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getPlaceName()));
                }
            }



            return places;
        }

        @Override
        protected void onPostExecute(List<APlace> aPlaces) {
            super.onPostExecute(aPlaces);
            for(int i = 0; i < aPlaces.size(); i++){
                APlace place = aPlaces.get(i);
                double latitude = place.getPlaceLatitude();
                double longitude = place.getPlaceLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                googleMapB.addMarker(new MarkerOptions().position(latLng).title(place.getPlaceName()));
            }

        }
    }


}
