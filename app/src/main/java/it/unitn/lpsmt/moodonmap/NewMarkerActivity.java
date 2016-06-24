package it.unitn.lpsmt.moodonmap;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;
import it.unitn.lpsmt.moodonmap.utils.Place;

/**
 * Created by Mattia on 27/04/2016.
 */
public class NewMarkerActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton sad;
    ImageButton bored;
    ImageButton lol;
    TextView city;
    EditText message;
    Button buttonSave;
    VolleyResponseListener listener;
    Place newPlace;

    Double lat;
    Double lng;

    int rId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_marker_activity);

        /***************************************/
        /*  Inizializzazione elementi nell'xml */
        /***************************************/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sad = (ImageButton) findViewById(R.id.sad);
        bored = (ImageButton) findViewById(R.id.bored);
        lol = (ImageButton) findViewById(R.id.lol);

        city = (TextView) findViewById(R.id.city);

        message = (EditText) findViewById(R.id.message);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        // Listener vari
        sad.setOnClickListener(this);
        lol.setOnClickListener(this);
        bored.setOnClickListener(this);

        // Prendo lat e lng dalla mainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("myLat");
            lng = extras.getDouble("myLng");
        }

        // Metto nella textView 'city' la città presa dalla lat-lng
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresses != null;
        if (addresses.size() > 0) {// se il gps non è arrivato a prendere la posizione dell'utente, qui crasha
            String locationDescr = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality();
            city.setText(locationDescr);
            //city.setText(addresses.get(0).getLocality());
        }

        // torno alla mainActivity passando messaggio, lat, lng, e id emoji
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rId != 0) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("message", message.getText().toString());
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    intent.putExtra("rId", rId);
                    intent.putExtra("activity_id", "NewMarker");

                    final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    newPlace = new Place(android_id, lat, lng, message.getText().toString(), "snippet",  rId);

                    //String body = new GsonBuilder().create().toJson(p);

                    Log.wtf("Miao", newPlace.toString());

                    // Invia il marker per il salvataggio sul server
                    sendData(newPlace);

                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Scegli un mooooood", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    // Memorizzo l'id dell'emoji selezionata
    @Override
    public void onClick(View v) {
        String toastMood = "";
        switch (v.getId()) {
            case R.id.sad:
                rId = R.drawable.sad;
                toastMood = "sad";
                break;

            case R.id.lol:
                rId = R.drawable.lol;
                toastMood = "lol";
                break;

            case R.id.bored:
                rId = R.drawable.bored;
                toastMood = "bored";
                break;

            default:
                break;
        }
        Toast toast = Toast.makeText(getApplicationContext(), toastMood, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void sendData(final Place newPlace){
        String url = "http://afnecors.altervista.org/android2016/api.php/markers";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //todo se ci dovesse essere stato un errore lato backend notificarlo all'utente da qua
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(NewMarkerActivity.this, "Errore nella richiesta!", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_device", ""+newPlace.getId_device());
                params.put("id_emo", ""+newPlace.getId_emo());
                params.put("latitude", ""+newPlace.getLatitude());
                params.put("longitude", ""+newPlace.getLongitude());
                params.put("message", ""+newPlace.getMessage());

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }
}