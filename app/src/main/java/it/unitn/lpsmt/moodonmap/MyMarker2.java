package it.unitn.lpsmt.moodonmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.Place;

public class MyMarker2 extends AppCompatActivity {

    VolleyResponseListener listener;

    ArrayList<String> listViewMessage = new ArrayList<>();
    ArrayList<Integer> listViewImage = new ArrayList<>();
    ArrayList<String> listViewAddress = new ArrayList<>();

    ArrayList<Double> usersLat = new ArrayList<>();
    ArrayList<Double> usersLng = new ArrayList<>();

    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_marker2_activity);

        listener = new VolleyResponseListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo = null;
                Gson gson = new Gson();
                Place p = null;

                if (response.length() == 0) {
                    Toast.makeText(MyMarker2.this, "Non hai ancora inserito nessun marker!", Toast.LENGTH_LONG).show();
                }

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

                    listViewImage.add(p.getId_emo());
                    listViewMessage.add(p.getMessage());
                    listViewAddress.add(p.getAddress(MyMarker2.this));

                    usersLat.add(p.getLatitude());
                    usersLng.add(p.getLongitude());
                }

                // Preparo la listview
                for (int i = 0; i < listViewMessage.size(); i++) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("listview_message", listViewMessage.get(i));
                    hm.put("listview_image", ""+listViewImage.get(i));
                    hm.put("listview_address", ""+listViewAddress.get(i));
                    aList.add(hm);
                }

                String[] from = {"listview_image", "listview_message", "listview_address"};
                int[] to = {R.id.listview_image, R.id.listview_message, R.id.listview_address};

                final SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.my_marker2_listview, from, to);
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
                Toast.makeText(MyMarker2.this, "No data available!", Toast.LENGTH_LONG).show();
            }
        };
        getData(listener);
    }

    protected void getData(final VolleyResponseListener listener){
        Bundle extras = getIntent().getExtras();
        String android_id = extras.getString("android_id");
        String url = "http://afnecors.altervista.org/android2016/api.php/markers?id_device="+android_id;

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

