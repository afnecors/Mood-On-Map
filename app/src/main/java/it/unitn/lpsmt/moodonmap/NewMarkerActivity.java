package it.unitn.lpsmt.moodonmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Mattia on 27/04/2016.
 */
public class NewMarkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_marker_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);
    }
}