package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.Place;

/**
 * Created by jack on 12/05/2016.
 */
public class MyMarkerActivity extends AppCompatActivity {

    /**
     * Forse questa activity devo rifarla, è molto pesante e il layout (non so come) viene tutto
     * sfasato.
     * L'altro metodo è passare dalla MainActivity direttamente solo i miei marker, e non fare
     * la chiamata al server (+ controllo se sono miei) qua.
     */

    //Integer[] numberOfMarkers = new Integer[1];    // numero di miei marker, è un problema adesso
    double myLat;   // mia lat attuale
    double myLng;   // mia lng attuale

    // le cose dei miei marker
    ArrayList<Double> oldLat = new ArrayList<>();
    ArrayList<Double> oldLng = new ArrayList<>();
    ArrayList<String> oldTitle = new ArrayList<>();
    ArrayList<Integer> oldIcon = new ArrayList<>();

    VolleyResponseListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        final String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.wtf("ANDRODOWN ID", android_id);

        // Prendo gli extra passati dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("myLat");
            myLng = extras.getDouble("myLng");
        }

        /**
         * Recupero i dati dal server
         */
        listener = new VolleyResponseListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo = null;

                // cicla la lista di oggetti json
                for (int i = 0; i < response.length(); i++) {

                    try {
                        jo = response.getJSONObject(i); // ritorna un singolo oggetto json

                        if(android_id.equals(jo.getString("id_device")) && jo.getInt("visible") == 1) {
                            //numberOfMarkers++;
                            oldTitle.add(jo.getString("message"));
                            oldLat.add(jo.getDouble("latitude"));
                            oldLng.add(jo.getDouble("longitude"));
                            oldIcon.add(jo.getInt("id_emo"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Da myLat e myLng creo un oggetto Location per definire dove si trova l'utente
                Location myLocation = new Location("");
                myLocation.setLatitude(myLat);
                myLocation.setLongitude(myLng);

                List<Integer> distance = new ArrayList<>(); // lista di distanze tra myLocation e tutti gli altri marker

                // tutti gli altri marker li salvo in usersLocation, usando tutte le lat e lng
                Location[] usersLocation = new Location[oldLat.size()];
                for (int i = 0; i < oldLat.size(); i++) {
                    usersLocation[i] = new Location("");
                    usersLocation[i].setLatitude(oldLat.get(i));
                    usersLocation[i].setLongitude(oldLng.get(i));
                    distance.add((int) myLocation.distanceTo(usersLocation[i])); // calcola la distanza tra myLocation e usersLocation[i] e la mette in distance
                }

                Collections.sort(distance); // ordino la lista secondo le distanze

                // Definisco degli array di bottoni e textview, in cui ogni elemento rappresenta delle
                // info dei marker degli altri utenti
                ImageButton[] emojiArray = new ImageButton[oldLat.size()];
                TextView[] messageArray = new TextView[oldLat.size()];
                TextView[] distanceArray = new TextView[oldLat.size()];
                Button[] buttonDirectionsArray = new Button[oldLat.size()];
                View[] line = new View[oldLat.size()];

                RelativeLayout info = (RelativeLayout) findViewById(R.id.info);
                info.setId(900000 + 1);

                //*** IDEA DI LAYOUT DI 'STA ACTIVITY: ***/

                //   /=====\     DISTANZA             /=====\    //
                //   |EMOJI|                          | --> |    //
                //   \=====/     MESSAGGIO            \=====/    //

                // Ciclo per creare i bottoni e le textview
                for (int i = 0; i < oldLat.size(); i++) {

                    final int final_i = i;  // per accedere a 'i' dentro le classi interne (tipo onClick)

                    emojiArray[i] = new ImageButton(getApplicationContext());  // Creo i singoli bottoni e le singole tv
                    messageArray[i] = new TextView(getApplicationContext());
                    distanceArray[i] = new TextView(getApplicationContext());
                    buttonDirectionsArray[i] = new Button(getApplicationContext());
                    line[i] = new View(getApplicationContext());   // linea separatoria

                    emojiArray[i].setId(i + 1);     // Imposto gli ID degli elementi...
                    messageArray[i].setId(i + 500);     // ...modi migliori non ne ho trovati per farlo
                    distanceArray[i].setId(i + 1000);   // perchè gli id devono essere INTEGER e UNICI
                    buttonDirectionsArray[i].setId(i + 1500);
                    line[i].setId(i + 2000);

                    /************************************************************************/

                    emojiArray[i].setImageResource(oldIcon.get(i));  // setto l'imageButton con l'immagine presa dall'id dell'emoji
                    emojiArray[i].setOnClickListener(
                            new View.OnClickListener() {        // Imposto il suo clicklistener
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra("selectedLat", oldLat.get(final_i));
                                    intent.putExtra("selectedLng", oldLng.get(final_i));
                                    intent.putExtra("activity_id", "NearMarker");
                                    startActivity(intent);
                                }
                            }
                    );

                    TypedValue outValue = new TypedValue(); // cose per rendere lo sfondo del bottone trasparente
                    getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    emojiArray[i].setBackgroundResource(outValue.resourceId);

                    // Parametri per posizionare l'elemento in questione nel posto che voglio
                    RelativeLayout.LayoutParams rlpEmoji = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlpEmoji.setMargins(15, 0, 0, 0); // sx, su, dx, giù

                    if (i == 0) { // se è il primo elemento dell'array lo metto in cima al layout
                        rlpEmoji.addRule(RelativeLayout.ALIGN_TOP, info.getId());
                    }
                    else {   // altrimenti sotto quello precedente
                        rlpEmoji.addRule(RelativeLayout.BELOW, line[i - 1].getId());
                    }
                    rlpEmoji.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    emojiArray[i].setLayoutParams(rlpEmoji);    // setto i parametri definiti all'elemento
                    info.addView(emojiArray[i]);  // aggiungo l'elemento alla view

                    // Gli altri elementi sotto funzionano tutti allo stesso modo, quindi evito di commentare
                    /************************************************************************/

                    messageArray[i].setText("" + oldTitle.get(i));
                    messageArray[i].setTextSize(20);
                    messageArray[i].setTypeface(null, Typeface.BOLD);

                    RelativeLayout.LayoutParams rlpMessage = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlpMessage.setMargins(15, 10, 0, 0); // sx, su, dx, giù

                    rlpMessage.addRule(RelativeLayout.ALIGN_TOP, emojiArray[i].getId());
                    rlpMessage.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId());

                    messageArray[i].setLayoutParams(rlpMessage);
                    info.addView(messageArray[i]);

                    /************************************************************************/

                    distanceArray[i].setText(distance.get(i) + " m");
                    messageArray[i].setTypeface(null, Typeface.ITALIC);

                    RelativeLayout.LayoutParams rlpDistance = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlpDistance.setMargins(17, 0, 0, 10); // sx, su, dx, giù

                    rlpDistance.addRule(RelativeLayout.BELOW, messageArray[i].getId());
                    rlpDistance.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId());

                    distanceArray[i].setLayoutParams(rlpDistance);
                    info.addView(distanceArray[i]);

                    /************************************************************************/

                    buttonDirectionsArray[i].setText("-->");
                    RelativeLayout.LayoutParams rlpButtonDirections = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    rlpButtonDirections.addRule(RelativeLayout.ALIGN_BOTTOM, emojiArray[i].getId());
                    rlpButtonDirections.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                    buttonDirectionsArray[i].setLayoutParams(rlpButtonDirections);
                    info.addView(buttonDirectionsArray[i]);

                    /************************************************************************/

                    RelativeLayout.LayoutParams rlpLine = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            1);

                    rlpLine.addRule(RelativeLayout.BELOW, emojiArray[i].getId());
                    //rlpLine.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    rlpLine.setMargins(70, 5, 0, 0);
                    line[i].setBackgroundColor(Color.parseColor("#B3B3B3"));

                    line[i].setLayoutParams(rlpLine);
                    info.addView(line[i]);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MyMarkerActivity.this, "No data available!", Toast.LENGTH_LONG).show();
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
