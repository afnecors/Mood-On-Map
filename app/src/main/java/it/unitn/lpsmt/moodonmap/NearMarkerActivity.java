package it.unitn.lpsmt.moodonmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    double[] userLat = new double[1024];    // tutte le latitudini degli utenti
    double[] userLng = new double[1024];    // tutte le longitudini degli utenti
    private ClusterManager<Place> mClusterManager;      //Array per avere i cluster di marker

    /*List<ImageButton> emojiList;
    List<TextView> messageList;
    List<TextView> distanceList;
    List<Button> buttonDirectionsList;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_marker_activity);

        /*emojiList = new ArrayList<>();
        messageList = new ArrayList<>();
        distanceList = new ArrayList<>();
        buttonDirectionsList = new ArrayList<>();*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mLinearLayout);

        // Prendo gli extra passati dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myLat = extras.getDouble("lat");
            myLng = extras.getDouble("lng");
            userLat = extras.getDoubleArray("userLat");
            userLng = extras.getDoubleArray("userLng");
        }

        numberOfMarkers = userLat.length;

        ImageButton[] emojiArray = new ImageButton[numberOfMarkers];
        TextView[] messageArray = new TextView[numberOfMarkers];
        TextView[] distanceArray = new TextView[numberOfMarkers];
        Button[] buttonDirectionsArray = new Button[numberOfMarkers];

        RelativeLayout info = (RelativeLayout) findViewById(R.id.info);
        //LinearLayout dist_mess = (LinearLayout) findViewById(R.id.dist_mess);
        //LinearLayout dist_mess = new LinearLayout(this);
        //dist_mess.setOrientation(LinearLayout.VERTICAL);

        //info.addView(dist_mess);

        for (int i = 0; i < 10; i++){

            emojiArray[i] = new ImageButton(this);
            messageArray[i] = new TextView(this);
            distanceArray[i] = new TextView(this);
            buttonDirectionsArray[i] = new Button(this);

            emojiArray[i].setId(i + 0);
            messageArray[i].setId(i + 500);
            distanceArray[i].setId(i + 1000);
            buttonDirectionsArray[i].setId(i + 1500);

            /************************************************************************/

            emojiArray[i].setImageResource(R.drawable.bored);
            RelativeLayout.LayoutParams rlpEmoji = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0){
                rlpEmoji.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            else{
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

            distanceArray[i].setText("distanza n: " + i);

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
