package it.unitn.lpsmt.moodonmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.Place;

/**
 * Created by jack on 12/05/2016.
 */
public class NearMarkerActivity extends AppCompatActivity {

    double myLat;
    double myLng;
    ArrayList<Double> usersLat = new ArrayList<>();
    ArrayList<Double> usersLng = new ArrayList<>();

    final List<String> distance = new ArrayList<>(); // lista di distanze tra myLocation e tutti gli altri marker
    ArrayList<Location> usersLocation = new ArrayList<>();  // posizione degli altri marker
    Location myLocation = new Location("");     // la mia posizione

    VolleyResponseListener listener;

    ArrayList<String> listViewMessage = new ArrayList<>();
    ArrayList<Integer> listViewImage = new ArrayList<>();
    ArrayList<String> listViewAddress = new ArrayList<>();
    //ArrayList<Integer> listViewDistance = new ArrayList<>();
    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

    String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_marker_activity);

        // Prendo gli extra passati dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("myLat");
            myLng = extras.getDouble("myLng");
        }

        Log.wtf("myLat is", ""+myLat);
        Log.wtf("myLng is", ""+myLng);

        // Da myLat e myLng creo un oggetto Location per definire dove si trova l'utente
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

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

                    p = gson.fromJson(jo.toString(), Place.class); // genera l'oggetto Java dal json
                    p.forceImageFromIdEmo(); // aggiunge l'immagine
                    p.forcePosition(); // aggiunge la posizione

                    usersLat.add(p.getLatitude());
                    usersLng.add(p.getLongitude());

                    // uso l'oggetto usersLocation per calcolare le distanze tra me e gli altri
                    usersLocation.add(new Location(""));
                    usersLocation.get(i).setLatitude(usersLat.get(i));
                    usersLocation.get(i).setLongitude(usersLng.get(i));
                    //distance.add(myLocation.distanceTo(usersLocation.get(i))); // calcola la distanza tra myLocation e usersLocation[i] e la mette in distance

                    DecimalFormat df = new DecimalFormat(); // cose per i km e metri e arrotondamenti

                    if(myLocation.distanceTo(usersLocation.get(i)) > 1000){
                        df.setMaximumFractionDigits(2);
                        distance.add(df.format(myLocation.distanceTo(usersLocation.get(i)) / 1000));
                        unit = "Km";
                    }
                    else{
                        df.setMaximumFractionDigits(0);
                        distance.add(df.format(myLocation.distanceTo(usersLocation.get(i))));
                        unit = "Metri";
                    }

                    Log.wtf("Distanza", i + " - " + distance.get(i).toString());

                    // gestione dell'indirizzo, tipo se è troppo lungo lo taglio
                    String address;
                    if(p.getAddress(NearMarkerActivity.this).length() > 27) {
                        address = p.getAddress(NearMarkerActivity.this).substring(0, 27);
                        address = address.concat("...");
                    }
                    else{
                        address = p.getAddress(NearMarkerActivity.this);
                    }

                    // gestione del messaggio, tipo se è troppo lungo lo taglio
                    String message;
                    if(p.getMessage().length() > 28) {
                        message = p.getMessage().substring(0, 28);
                        message = message.concat("...");
                    }
                    else{
                        message = p.getMessage();
                    }

                    listViewImage.add(p.getId_emo());
                    listViewMessage.add(message);
                    listViewAddress.add(address + " -> " + distance.get(i) + " " + unit);
                    //listViewDistance.add(distance.get(i));
                }

                //Collections.sort(listViewDistance); // ordino la lista secondo le distanze

                // Preparo la listview
                for (int i = 0; i < listViewMessage.size(); i++) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("listview_message", listViewMessage.get(i));
                    hm.put("listview_image", "" + listViewImage.get(i));
                    hm.put("listview_address", "" + listViewAddress.get(i));
                    //hm.put("listview_distance", "" + listViewDistance.get(i));
                    aList.add(hm);
                }

                String[] from = {"listview_image", "listview_message", "listview_address"};
                int[] to = {R.id.listview_image, R.id.listview_message, R.id.listview_address};

                final SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.near_marker_listview, from, to);
                final ListView androidListView = (ListView) findViewById(R.id.list_view);
                androidListView.setAdapter(simpleAdapter);

                // Click Listener
                androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);

                        intent.putExtra("selectedLat", usersLat.get(position));
                        intent.putExtra("selectedLng", usersLng.get(position));
                        intent.putExtra("activity_id", "NearMarker");   // al titorno alla Main fa la stessa cosa di Near
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        //Toast.makeText(getBaseContext(), listViewMessage.get(position),Toast.LENGTH_SHORT).show();
                    }
                });

                // LongClick Listener
                androidListView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        return false;
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(NearMarkerActivity.this, "No data available!", Toast.LENGTH_LONG).show();
            }
        };
        getData(listener);

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
