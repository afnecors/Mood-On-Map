package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;

import it.unitn.lpsmt.moodonmap.utils.OwnIconRendered;
import it.unitn.lpsmt.moodonmap.utils.PermissionUtils;
import it.unitn.lpsmt.moodonmap.utils.Place;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;     // Oggetto mappa
    private GoogleApiClient client;     // Oggetto per usare le API di google
    private ClusterManager<Place> mClusterManager;      //Array per avere i cluster di marker

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        // Nuovo marker
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {        // Imposto il suo clicklistener
            @Override
            public void onClick(View view) {        // TODO: definire azione
               
                Intent iinent= new Intent(getBaseContext(),NewMarkerActivity.class);
                startActivity(iinent);

            }
        });

        // Bottone di sinistra: Marker vicini
        Button but_sx = (Button) findViewById(R.id.but_sx);
        but_sx.setOnClickListener(new View.OnClickListener() {        // Imposto il suo clicklistener
            @Override
            public void onClick(View view) {        // TODO: definire azione

                Intent iinent= new Intent(getBaseContext(),NearMarkerActivity.class);
                startActivity(iinent);
            }
        });

        // Bottone di destra: I miei Marker
        Button but_dx = (Button) findViewById(R.id.but_dx);
        but_dx.setOnClickListener(new View.OnClickListener() {        // Imposto il suo clicklistener
            @Override
            public void onClick(View view) {        // TODO: definire azione

                Intent iinent= new Intent(getBaseContext(),MyMarkerActivity.class);
                startActivity(iinent);
            }
        });


        // Robe auto-generate da google:
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        if (id == R.id.action_settings) {

            Intent iinent= new Intent(getBaseContext(),SettingActivity.class);
            startActivity(iinent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    // Quando la mappa è pronta chiama questa (per info vedi commenti sopra):
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Abilita i permessi della posizione per Android 6
        enableMyLocation();
        // Nasconde i bottoni direzione in basso a dx
        mMap.getUiSettings().setMapToolbarEnabled(false);

        double[] lat = new double[1024];    // latitudine
        double[] lng = new double[1024];    // longitudine
        LatLng[] user = new LatLng[1024];   // lat e long
        double seed_lat = 46.0500;  // seme per generare latitudini (for testing purposes)
        double seed_lng = 11.1300;  // seme per generare longitudini (for testing purposes)

        mClusterManager = new ClusterManager<Place>(this, mMap); // per clusterizzare i marker

        //Declare HashMap to store mapping of marker to Activity
        HashMap<String, String> markerMap = new HashMap<String, String>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);

        // setto lat e lng
        for (int i = 0; i < 10; i++) {
            lat[i] = seed_lat + 0.001;
            lng[i] = seed_lng + 0.001;
            seed_lat = seed_lat + 0.001;
            seed_lng = seed_lng + 0.001;
        }

        for (int i = 10; i < 20; i++) {
            lat[i] = seed_lat + 0.002;
            lng[i] = seed_lng - 0.001;
            seed_lat = seed_lat + 0.001;
            seed_lng = seed_lng + 0.001;
        }

        /* VERSIONE COI MARKER
        // Aggiungo dei marker
        for (int i = 0; i < 5; i++) {
            user[i] = new LatLng(lat[i], lng[i]);
            mMap.addMarker(new MarkerOptions().position(user[i])
                        .title("yolo")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.lol))
                        .alpha(trans)
            );
        }

        // Aggiungo dei marker
        for(int i = 6 ; i < 10 ; i++) {
            user[i] = new LatLng(lat[i], lng[i]);
            mMap.addMarker(new MarkerOptions().position(user[i])
                            .title("yolo")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.sad))
                            .alpha(trans)
            );
        }
        */

        /****************************/
        /*  Clusterizzo i marker    */
        /****************************/

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);

        String title = "TITOLO";
        String snippet = "snippet";
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.sad);

        // Add cluster items (markers) to the cluster manager.
        for (int i = 0; i < 20; i++) {
            user[i] = new LatLng(lat[i], lng[i]);
            mClusterManager.addItem(new Place(user[i].latitude, user[i].longitude, title, snippet, icon));
        }

        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        /***********************************************************/
        /*  Aprire nuova activity quando si clicca su un marker    */
        /***********************************************************/

        final Context mContext = this;

        mMap.setOnMarkerClickListener(mClusterManager); // Inutile?
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Place>() {
            @Override
            public boolean onClusterItemClick(Place item) {
                Log.d("TAG: ", "SIETE TUTTI GAY PORCO DIO SOFFOCATEVI SULLA MIA CAPPELLA");
                Intent i = new Intent(mContext, ShowInfoMarkerActivity.class);
                startActivity(i);
                return false;
            }
        });


        /*  Versione coi marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("TAG: ", "SIETE TUTTI GAY PORCO DIO SOFFOCATEVI SULLA MIA CAPPELLA");
                Intent i = new Intent(mContext, NewMarkerActivity.class);
                startActivity(i);
                return false;
            }
        });
        */

        // Muovere la mappa sulla propria posizione.... WIP
        //1) mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMap.getCameraPosition()));
        //2) mMap.moveCamera(CameraUpdateFactory.newLatLng(mMap.getCameraPosition().target));
        /*3)
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMap.getCameraPosition()));
            }
        });
        */

        CameraUpdate center=    // imposto dove posizionare la vista iniziale
                CameraUpdateFactory.newLatLng(new LatLng(seed_lat, seed_lng));

        mMap.moveCamera(center);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));     // imposta lo zoom
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://it.unitn.lpsmt.moodonmap/http/host/path")

                // Uri.parse("android-app://com.example.mattia.googlemapstest/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://it.unitn.lpsmt.moodonmap/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

}