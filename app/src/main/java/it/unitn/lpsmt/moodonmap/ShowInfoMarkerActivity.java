package it.unitn.lpsmt.moodonmap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.OwnIconRendered;
import it.unitn.lpsmt.moodonmap.utils.Place;

/**
 * Created by jack on 12/05/2016.
 */
public class ShowInfoMarkerActivity extends AppCompatActivity {

    VolleyResponseListener listener;

    double myLat, myLng;
    TextView tw_time, tw_message, tw_distance, tw_address;
    ImageView iw_emoji;
    Button button_delete;
    Location myLocation, otherLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_info_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("myLat");
            myLng = extras.getDouble("myLng");
        }

        myLocation = new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

        tw_time = (TextView) findViewById(R.id.tw_time);
        tw_message = (TextView) findViewById(R.id.tw_message);
        tw_distance = (TextView) findViewById(R.id.tw_distance);
        tw_address = (TextView) findViewById(R.id.tw_address);
        iw_emoji = (ImageView) findViewById(R.id.iw_emoji);
        button_delete = (Button) findViewById(R.id.button_delete);

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

                    try {
                        tw_time.append(jo.getString("timestamp"));

                        //iw_emoji.setImageResource(jo.getInt("id_emo"));
                        iw_emoji.setImageResource(p.getId_emo());

                        //tw_message.append(jo.getString("message"));
                        tw_message.append(p.getMessage());

                        tw_address.append(p.getAddress(ShowInfoMarkerActivity.this));

                        otherLocation = new Location("");
                        otherLocation.setLatitude(p.getLatitude());
                        otherLocation.setLongitude(p.getLongitude());
                        int distance = (int) myLocation.distanceTo(otherLocation);
                        tw_distance.append("" + distance + " metri");

                        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        if(android_id.equals(p.getId_device())){

                            button_delete.setVisibility(View.VISIBLE);

                            final Place finalP = p;
                            button_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    deleteData(finalP.getId());
                                    startActivity(intent);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ShowInfoMarkerActivity.this, "No data available!", Toast.LENGTH_LONG).show();
            }
        };
        getData(listener);
    }

    protected void getData(final VolleyResponseListener listener){
        Bundle extras = getIntent().getExtras();
        final int id_marker = extras.getInt("id_marker");
        String url = "http://afnecors.altervista.org/android2016/api.php/markers/"+id_marker;

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

    protected void deleteData(int id){
        String url = "http://afnecors.altervista.org/android2016/api.php/markers/"+id;

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONArray>() {

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
