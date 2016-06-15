package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import android.os.Parcelable;
import android.app.ProgressDialog;

import android.provider.Settings;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.OwnIconRendered;
import it.unitn.lpsmt.moodonmap.utils.PermissionUtils;
import it.unitn.lpsmt.moodonmap.utils.Place;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;     // Oggetto mappa
    private GoogleApiClient client;     // Oggetto per usare le API di google

    private double myLat, myLng;    // lat e lng corrente, cioè dove è l'utente
    List<LatLng> user = new ArrayList<>();   // lat e long di tutti gli utenti
    ArrayList<Double> lat = new ArrayList<>();    // tutte le latitudini degli utenti
    ArrayList<Double> lng = new ArrayList<>();    // tutte le longitudini degli utenti

    ArrayList<String> title = new ArrayList<>();    // titoli dei marker (AKA: messaggi)
    ArrayList<String> snippet = new ArrayList<>();  // snippet dei marker (ancora da usare)
    ArrayList<Integer> icon = new ArrayList<>();    // id degli emoji sui marker

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    VolleyResponseListener listener;

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
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), NewMarkerActivity.class);
                intent.putExtra("myLat", myLat);      // passo myLat e myLng all'activity chiamata
                intent.putExtra("myLng", myLng);
                startActivity(intent);

            }
        });

        // Bottone di sinistra: Marker vicini
        Button but_sx = (Button) findViewById(R.id.but_sx);
        but_sx.setOnClickListener(new View.OnClickListener() {        // Imposto il suo clicklistener
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), NearMarkerActivity.class);
                intent.putExtra("myLat", myLat);      // passo myLat e myLng all'activity chiamata
                intent.putExtra("myLng", myLng);
                intent.putExtra("numberOfMarkers", lat.size()); // il numero di markers sulla mappa

                // Invece di passare tutti i marker passo ogni singoli oggetti che li compongono
                // perchè è più facile, anche se più lungo
                for (int i = 0; i < lat.size(); i++) {
                    intent.putExtra("usersLat" + i, lat.get(i));    // tutte le lat e lng dei marker sulla mappa
                    intent.putExtra("usersLng" + i, lng.get(i));
                    intent.putExtra("usersMsg" + i, title.get(i));  // i messaggi sulla mappa
                    intent.putExtra("usersEmoji" + i, icon.get(i)); // gli id delle emoji
                }

                startActivity(intent);
            }
        });

        // Bottone di destra: I miei Marker
        Button but_dx = (Button) findViewById(R.id.but_dx);
        but_dx.setOnClickListener(new View.OnClickListener() {        // Imposto il suo clicklistener
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), MyMarkerActivity.class);
                intent.putExtra("myLat", myLat);      // passo myLat e myLng all'activity chiamata
                intent.putExtra("myLng", myLng);
                startActivity(intent);
            }
        });


        // Robe auto-generate da google:
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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

            Intent iinent = new Intent(getBaseContext(), SettingActivity.class);
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

        double seed_lat = 46.0500;  // seme per generare latitudini (for testing purposes)
        double seed_lng = 11.1300;  // seme per generare longitudini (for testing purposes)

        final ClusterManager<Place> mClusterManager = new ClusterManager<Place>(this, mMap);  // manager dei cluster di marker

        //Declare HashMap to store mapping of marker to Activity
        HashMap<String, String> markerMap = new HashMap<String, String>();  // per ora inutile

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

        mMap.setMyLocationEnabled(true);    // altri permessi della posizione, uno dei due sarà inutile?

        // setto lat e lng
        /*for (int i = 0; i < 10; i++) {
            lat.add(seed_lat + 0.001);
            lng.add(seed_lng + 0.001);
            seed_lat = seed_lat + 0.001;
            seed_lng = seed_lng + 0.001;
        }

        for (int i = 10; i < 20; i++) {
            lat.add(seed_lat + 0.002);
            lng.add(seed_lng - 0.001);
            seed_lat = seed_lat + 0.001;
            seed_lng = seed_lng + 0.001;
        }
        */

        /****************************/
        /*  Clusterizzo i marker    */
        /****************************/

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
//        for (int i = 0; i < 20; i++) {
//            title.add("messaggio " + i);    // titolo dei marker (AKA: messaggio)
//            snippet.add("snippet");     // chissà se sto snippet un giorno lo useremo...
//
//            // per sparpagliare un po' le emoji
//            if (i < 7) {
//                icon.add(R.drawable.sad);   // aggiungo l'id dell'emoji all'arraylist
//            } else if (i >= 7 && i < 14) {
//                icon.add(R.drawable.lol);
//            } else {
//                icon.add(R.drawable.bored);
//            }
//
//            user.add(new LatLng(lat.get(i), lng.get(i)));   // aggiungo un oggetto LatLng alla lista
//
//            mClusterManager.addItem(    // aggiungo tutti i marker generati al cluster manager
//                    new Place(
//                            "",
//                            user.get(i).latitude,
//                            user.get(i).longitude,
//                            title.get(i),
//                            snippet.get(i),
//                            icon.get(i)   // dall'id dell'emoji genero un oggetto BitmapDescriptor
//                    )
//            );
//            mClusterManager.cluster(); // refresho il cluster
//        }


        /**
         * Recupero i dati dal server
         */
        listener = new VolleyResponseListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo = null;
                Gson gson = new Gson();
                Place p = null;

                // cicla la lista di oggetti json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jo = response.getJSONObject(i); // ritorna un singolo oggetto json
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.wtf("a JSON war exploded: ", jo.toString());    // debug
                    p = gson.fromJson(jo.toString(), Place.class); // genera l'oggetto Java dal json
                    p.forceImageFromIdEmo(); // aggiunge l'immagine
                    p.forcePosition(); // aggiunge la posizione

                    // aggiungo cose alle arraylist, mi serve per passare le singole cose alle altre activity
                    title.add(p.getMessage());
                    lat.add(p.getPosition().latitude);
                    lng.add(p.getPosition().longitude);
                    icon.add(R.drawable.lol);   // per adesso mettiamo che sono tutte lol di default

                    // test per vedere se abbiamo tutti gli stessi id
                    Log.wtf("ID R.drawable.sad ----> ", " " + R.drawable.sad);
                    Log.wtf("ID R.drawable.lol ----> ", " " + R.drawable.lol);
                    Log.wtf("ID R.drawable.bored ----> ", " " + R.drawable.bored);

                    mClusterManager.addItem(p);
                    mClusterManager.cluster();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "No data available!", Toast.LENGTH_LONG).show();
            }
        };
        getData(listener);

        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

        /***********************************************************/
        /*  Aprire nuova activity quando si clicca su un marker    */
        /***********************************************************/

        final Context mContext = this;

        mMap.setOnMarkerClickListener(mClusterManager); // Inutile?
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Place>() {
            @Override
            public boolean onClusterItemClick(Place item) {
                Intent i = new Intent(mContext, ShowInfoMarkerActivity.class);
                startActivity(i);
                return false;
            }
        });

        CameraUpdate center =    // imposto dove posizionare la vista iniziale
                CameraUpdateFactory.newLatLng(new LatLng(seed_lat, seed_lng));
        mMap.moveCamera(center);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));     // imposta lo zoom

        // Azioni fatte quando torno in MainActivity da un'altra activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String activity = extras.getString("activity_id");  // utile per determinare da quale activity vengo

            String message = extras.getString("message");   // da NewMarkerActivity
            double newLng = extras.getDouble("lng");    // da NewMarkerActivity
            double newLat = extras.getDouble("lat");    // da NewMarkerActivity
            int rId = extras.getInt("rId");     // da NewMarkerActivity

            double selectedLng = extras.getDouble("selectedLng");   // da NearMarkerActivity
            double selectedLat = extras.getDouble("selectedLat");   // da NearMarkerActivity

            int pos = extras.getInt("position");//da settingActivity
            int range = extras.getInt("range");//da settingActivity

            final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            message += " android_id:" + android_id;

            switch (activity) {     // guardo da quale activity vengo

                case "NewMarker":
                    //BitmapDescriptor selectedIcon = resizeMarker(rId, 100, 100); // chiamo il metodo per ridimensionare il nuovo marker
                    //BitmapDescriptor selectedIcon = BitmapDescriptorFactory.fromResource(rId); // metto il marker con l'emoji selezionata

                    mClusterManager.addItem(new Place(android_id,newLat, newLng, message, "snippet", rId)); // aggiungno nuovo marker al cluster
                    mClusterManager.cluster(); // refresho il cluster

                    CameraUpdate newLatLng =    // imposto la posizione della mappa
                            CameraUpdateFactory.newLatLng(new LatLng(newLat, newLng));
                    mMap.moveCamera(newLatLng);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));     // imposta lo zoom

                    activity = "";  // resetto l'identificatore di chi ha chiamato l'activity
                    break;

                case "NearMarker":
                    CameraUpdate selectedLatLng =   // imposto la posizione della mappa
                            CameraUpdateFactory.newLatLng(new LatLng(selectedLat, selectedLng));
                    mMap.moveCamera(selectedLatLng);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));     // imposta lo zoom

                    activity = "";
                    break;

                case "setting":
                    Toast.makeText(getApplicationContext(),"setting",Toast.LENGTH_LONG).show();
                    int id_e;
                    if(pos==0){
                        id_e=R.drawable.bored;
                    }else if(pos==1){
                        id_e=R.drawable.lol;
                    }else if(pos==2){
                        id_e=R.drawable.sad;
                    }

                    activity = "";
                    break;

                default:
                    break;
            }
        }
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

    @Override
    public void onConnected(Bundle connectionHint) {
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

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (mLastLocation != null) {
            myLat = mLastLocation.getLatitude();
            myLng = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Ridimensiona un marker coi valori specificati, potrebbe servirmi in futuro
    public BitmapDescriptor resizeMarker(int id_marker, int width, int height){

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(id_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap bMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return BitmapDescriptorFactory.fromBitmap(bMarker);
    }

    protected void getData(final VolleyResponseListener listener){
        String url = "http://afnecors.altervista.org/android2016/api.php/markers";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}