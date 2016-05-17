package it.unitn.lpsmt.moodonmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import it.unitn.lpsmt.moodonmap.api.MySingleton;
import it.unitn.lpsmt.moodonmap.api.VolleyResponseListener;

/**
 * Created by jack on 12/05/2016.
 */
public class MyMarkerActivity extends AppCompatActivity {
    TextView mTxtDisplay;
    String data;
    VolleyResponseListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        listener = new VolleyResponseListener() {
            @Override
            public void onResponse(JSONArray response) {
                data = response.toString();

                mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);
                mTxtDisplay.setText(response.toString());
            }

            @Override
            public void onError(String message) {

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
