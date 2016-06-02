package it.unitn.lpsmt.moodonmap;

import android.graphics.Bitmap;
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
public class NearMarkerActivity extends AppCompatActivity {

    int numberOfMarkers;
    double myLat;
    double myLng;
    double[] usersLat = new double[1024];    // tutte le latitudini degli utenti
    double[] usersLng = new double[1024];    // tutte le longitudini degli utenti
    String[] usersMsg = new String[1024];   // messaggi
    Integer[] usersEmoji = new Integer[1024];   // id dell'emoji

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        // Prendo gli extra passati dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("myLat");
            myLng = extras.getDouble("myLng");
            numberOfMarkers = extras.getInt("numberOfMarkers");

            for(int i = 0; i < numberOfMarkers; i ++){
                usersLat[i] = extras.getDouble("usersLat" + i);
                usersLng[i] = extras.getDouble("usersLng" + i);
                usersMsg[i] = extras.getString("usersMsg" + i);
                usersEmoji[i] = extras.getInt("usersEmoji" + i);
            }
        }

        // Da myLat e myLng creo un oggetto Location per definire dove si trova l'utente
        Location myLocation = new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

        List<Integer> distance = new ArrayList<>(); // lista di distanze tra myLocation e tutti gli altri marker

        // tutti gli altri marker li salvo in usersLocation, usando tutte le lat e lng
        Location[] usersLocation = new Location[numberOfMarkers];
        for (int i = 0; i < numberOfMarkers; i++){
            usersLocation[i] = new Location("");
            usersLocation[i].setLatitude(usersLat[i]);
            usersLocation[i].setLongitude(usersLng[i]);
            distance.add((int) myLocation.distanceTo(usersLocation[i])); // calcola la distanza tra myLocation e usersLocation[i] e la mette in distance
        }

        Collections.sort(distance); // ordino la lista secondo le distanze

        // Definisco degli array di bottoni e textview, in cui ogni elemento rappresenta delle
        // info dei marker degli altri utenti
        ImageButton[] emojiArray = new ImageButton[numberOfMarkers];
        TextView[] messageArray = new TextView[numberOfMarkers];
        TextView[] distanceArray = new TextView[numberOfMarkers];
        Button[] buttonDirectionsArray = new Button[numberOfMarkers];

        RelativeLayout info = (RelativeLayout) findViewById(R.id.info);

        /*** IDEA DI LAYOUT DI 'STA ACTIVITY: ***/

        /*   /=====\     DISTANZA             /=====\    */
        /*   |EMOJI|                          | --> |    */
        /*   \=====/     MESSAGGIO            \=====/    */

        // Ciclo per creare i bottoni e le textview
        for (int i = 0; i < numberOfMarkers; i++){

            emojiArray[i] = new ImageButton(this);  // Creo i singoli bottoni e le singole tv
            messageArray[i] = new TextView(this);
            distanceArray[i] = new TextView(this);
            buttonDirectionsArray[i] = new Button(this);

            emojiArray[i].setId(i + 0);     // Imposto gli ID degli elementi...
            messageArray[i].setId(i + 500);     // ...modi migliori non ne ho trovati per farlo
            distanceArray[i].setId(i + 1000);   // perchè gli id devono essere INTEGER e unici
            buttonDirectionsArray[i].setId(i + 1500);

            /************************************************************************/

            emojiArray[i].setImageResource(usersEmoji[i]);  // setto l'imageButton con l'immagine presa dall'id dell'emoji

            // TODO: Tentativo per far diventare l'immagine più grande... ci penserò dopo
            /*emojiArray[i].setMinimumHeight(200);
            emojiArray[i].setMinimumWidth(200);
            emojiArray[i].setPadding(10, 10, 10, 10);
            emojiArray[i].setAdjustViewBounds(true);
            emojiArray[i].setScaleType(ImageView.ScaleType.FIT_CENTER);*/

            TypedValue outValue = new TypedValue(); // cose per rendere lo sfondo del bottone trasparente
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            emojiArray[i].setBackgroundResource(outValue.resourceId);

            // Parametri per posizionare l'elemento in questione nel posto che voglio
            RelativeLayout.LayoutParams rlpEmoji = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0){ // se è il primo elemento dell'array lo metto in cima al layout
                rlpEmoji.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            else{   // altrimenti sotto quello precedente
                rlpEmoji.addRule(RelativeLayout.BELOW, emojiArray[i].getId() - 1);
            }
            rlpEmoji.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            emojiArray[i].setLayoutParams(rlpEmoji);    // setto i parametri definiti all'elemento
            (info).addView(emojiArray[i]);  // aggiungo l'elemento alla view

            // Gli altri elementi sotto funzionano tutti allo stesso modo, quindi evito di commentare
            /************************************************************************/

            messageArray[i].setText("" + usersMsg[i]);

            RelativeLayout.LayoutParams rlpMessage = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlpMessage.setMargins(15, 17, 0, 0); // sx, su, dx, giù
            if(i == 0){
                rlpMessage.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            else{
                rlpMessage.addRule(RelativeLayout.BELOW, distanceArray[i].getId() - 1);
            }
            rlpMessage.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId() + 1);
            //TODO: se non metto + 1, viene sbagliato il posizionamento del primo elemento
            //TODO: se lo metto viene sbagliato l'ultimo... boh
            messageArray[i].setLayoutParams(rlpMessage);
            (info).addView(messageArray[i]);

            /************************************************************************/

            distanceArray[i].setText(distance.get(i) + " m");

            RelativeLayout.LayoutParams rlpDistance = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlpDistance.setMargins(15, 0, 0, 0); // sx, su, dx, giù
            rlpDistance.addRule(RelativeLayout.BELOW, messageArray[i].getId());
            rlpDistance.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId() + 1);
            distanceArray[i].setLayoutParams(rlpDistance);
            (info).addView(distanceArray[i]);

            /************************************************************************/

            buttonDirectionsArray[i].setText("-->");
            RelativeLayout.LayoutParams rlpButtonDirections = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0){
                rlpButtonDirections.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            else{
                rlpButtonDirections.addRule(RelativeLayout.BELOW, buttonDirectionsArray[i].getId() - 1);
            }
            rlpButtonDirections.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            buttonDirectionsArray[i].setLayoutParams(rlpButtonDirections);
            (info).addView(buttonDirectionsArray[i]);
        }
    }
}
