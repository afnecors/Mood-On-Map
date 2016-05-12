package it.unitn.lpsmt.moodonmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by jack on 12/05/2016.
 */
public class MyMarkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);
    }
}
