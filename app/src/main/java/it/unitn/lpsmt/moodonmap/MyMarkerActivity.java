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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unitn.lpsmt.moodonmap.utils.Place;

/**
 * Created by jack on 12/05/2016.
 */
public class MyMarkerActivity extends AppCompatActivity {

    /**
     * Funziona praticamente uguale alla NearMarker, tranne qualche modifica
     */

    int numberOfMarkers;
    double myLat;
    double myLng;
    String android_id;
    ArrayList<Double> usersLat = new ArrayList<>();    // tutte le latitudini degli utenti
    ArrayList<Double> usersLng = new ArrayList<>();    // tutte le longitudini degli utenti
    ArrayList<String> usersMsg = new ArrayList<>();   // messaggi
    ArrayList<Integer> usersEmoji = new ArrayList<>();   // id dell'emoji degli utenti
    ArrayList<String> usersId_device = new ArrayList<>();   // id dei dispositivi degli utenti

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        int myMarker_counter = 0;

        // Prendo gli extra passati dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("myLat");
            myLng = extras.getDouble("myLng");
            numberOfMarkers = extras.getInt("numberOfMarkers");
            android_id = extras.getString("android_id");

            for (int i = 0; i < numberOfMarkers; i++) {

                usersId_device.add(extras.getString("usersId_device" + i));

                // Prendo i dati solo dei marker col mio id
                if(android_id.equals(usersId_device.get(i))) {
                    myMarker_counter++;
                    usersLat.add(extras.getDouble("usersLat" + i));
                    usersLng.add(extras.getDouble("usersLng" + i));
                    usersMsg.add(extras.getString("usersMsg" + i));
                    usersEmoji.add(extras.getInt("usersEmoji" + i));
                }
            }
        }

        // Da myLat e myLng creo un oggetto Location per definire dove si trova l'utente
        Location myLocation = new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

        List<Integer> distance = new ArrayList<>(); // lista di distanze tra myLocation e tutti gli altri marker

        // tutti gli altri marker li salvo in usersLocation, usando tutte le lat e lng
        Location[] usersLocation = new Location[myMarker_counter];
        for (int i = 0; i < myMarker_counter; i++) {
            usersLocation[i] = new Location("");
            usersLocation[i].setLatitude(usersLat.get(i));
            usersLocation[i].setLongitude(usersLng.get(i));
            distance.add((int) myLocation.distanceTo(usersLocation[i])); // calcola la distanza tra myLocation e usersLocation[i] e la mette in distance
        }

        Collections.sort(distance); // ordino la lista secondo le distanze

        // Definisco degli array di bottoni e textview, in cui ogni elemento rappresenta delle
        // info dei marker degli altri utenti
        ImageButton[] emojiArray = new ImageButton[myMarker_counter];
        TextView[] messageArray = new TextView[myMarker_counter];
        TextView[] distanceArray = new TextView[myMarker_counter];
        Button[] buttonDirectionsArray = new Button[myMarker_counter];
        View[] line = new View[myMarker_counter];

        RelativeLayout info = (RelativeLayout) findViewById(R.id.info);
        info.setId(900000 + 1);

        //*** IDEA DI LAYOUT DI 'STA ACTIVITY: ***/

        //*   /=====\     DISTANZA             /=====\    */
        //*   |EMOJI|                          | --> |    */
        //*   \=====/     MESSAGGIO            \=====/    */

        // Ciclo per creare i bottoni e le textview
        for (int i = 0; i < myMarker_counter; i++) {

            final int final_i = i;  // per accedere a 'i' dentro le classi interne (tipo onClick)

            emojiArray[i] = new ImageButton(this);  // Creo i singoli bottoni e le singole tv
            messageArray[i] = new TextView(this);
            distanceArray[i] = new TextView(this);
            buttonDirectionsArray[i] = new Button(this);
            line[i] = new View(this);   // linea separatoria

            emojiArray[i].setId(i + 1);     // Imposto gli ID degli elementi...
            messageArray[i].setId(i + 500);     // ...modi migliori non ne ho trovati per farlo
            distanceArray[i].setId(i + 1000);   // perchè gli id devono essere INTEGER e UNICI
            buttonDirectionsArray[i].setId(i + 1500);
            line[i].setId(i + 2000);

            /************************************************************************/

            emojiArray[i].setImageResource(usersEmoji.get(i));  // setto l'imageButton con l'immagine presa dall'id dell'emoji

            emojiArray[i].setOnClickListener(
                    new View.OnClickListener() {        // Imposto il suo clicklistener
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("selectedLat", usersLat.get(final_i));
                            intent.putExtra("selectedLng", usersLng.get(final_i));
                            intent.putExtra("activity_id", "NearMarker");   // al titorno alla Main fa la stessa cosa di Near
                            startActivity(intent);
                        }
                    }
            );

            TypedValue outValue = new TypedValue(); // cose per rendere lo sfondo del bottone trasparente
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
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

            messageArray[i].setText("" + usersMsg.get(i));
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
}
