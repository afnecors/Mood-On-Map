package it.unitn.lpsmt.moodonmap;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
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

            //usersLat = extras.getDoubleArray("usersLat");
            //usersLng = extras.getDoubleArray("usersLng");
            //Log.wtf("lat.length", " --------- " + usersLat.length);
            //Log.wtf("lng.length", " --------- " + usersLng.length);
            numberOfMarkers = extras.getInt("numberOfMarkers");

            for(int i = 0; i < numberOfMarkers; i ++){
                usersLat[i] = extras.getDouble("usersLat" + i);
                usersLng[i] = extras.getDouble("usersLng" + i);
            }
        }

        Location myLocation = new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

        List<Float> distance = new ArrayList<>();
        //List<Location> usersLocation = new ArrayList<>();
        Location[] usersLocation = new Location[numberOfMarkers];
        for (int i = 0; i < numberOfMarkers; i++){
            Log.wtf("usersLat[i]: ", "------------------------- - " + usersLat[i]);
            Log.wtf("usersLng[i]: ", "------------------------- - " + usersLng[i]);
            usersLocation[i] = new Location("");
            usersLocation[i].setLatitude(usersLat[i]);
            usersLocation[i].setLongitude(usersLng[i]);
            distance.add(myLocation.distanceTo(usersLocation[i]));
        }

        // Definisco degli array di bottoni e textview, in cui ogni elemento rappresenta delle
        // info dei marker degli altri utenti
        ImageButton[] emojiArray = new ImageButton[numberOfMarkers];
        TextView[] messageArray = new TextView[numberOfMarkers];
        TextView[] distanceArray = new TextView[numberOfMarkers];
        Button[] buttonDirectionsArray = new Button[numberOfMarkers];

        RelativeLayout info = (RelativeLayout) findViewById(R.id.info);

        /*** LAYOUT: ***/

        /*   /=====\     DISTANZA    /=====\    */
        /*   |EMOJI|                 | --> |    */
        /*   \=====/     MESSAGGIO   \=====/    */

        // Ciclo per creare i bottoni e le textview
        for (int i = 0; i < numberOfMarkers; i++){

            emojiArray[i] = new ImageButton(this);  // Creo i singoli bottoni e le singole tv
            messageArray[i] = new TextView(this);
            distanceArray[i] = new TextView(this);
            buttonDirectionsArray[i] = new Button(this);

            emojiArray[i].setId(i + 0);     // Imposto gli ID degli elementi...
            messageArray[i].setId(i + 500);     // ...modi migliori non ne ho trovati per farlo
            distanceArray[i].setId(i + 1000);
            buttonDirectionsArray[i].setId(i + 1500);

            /************************************************************************/

            emojiArray[i].setImageResource(R.drawable.bored);
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
            emojiArray[i].setLayoutParams(rlpEmoji);
            (info).addView(emojiArray[i]);

            /************************************************************************/

            messageArray[i].setText("messaggio n: " + i);
            RelativeLayout.LayoutParams rlpMessage = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0){
                rlpMessage.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            else{
                rlpMessage.addRule(RelativeLayout.BELOW, distanceArray[i].getId() - 1);
            }
            rlpMessage.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId());
            messageArray[i].setLayoutParams(rlpMessage);
            (info).addView(messageArray[i]);

            /************************************************************************/

            distanceArray[i].setText("" + distance.get(i));

            RelativeLayout.LayoutParams rlpDistance = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlpDistance.addRule(RelativeLayout.BELOW, messageArray[i].getId());

            rlpDistance.addRule(RelativeLayout.RIGHT_OF, emojiArray[i].getId());
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
