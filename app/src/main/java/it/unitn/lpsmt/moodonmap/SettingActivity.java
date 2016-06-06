package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.unitn.lpsmt.moodonmap.utils.ListSettingAdapter;

/**
 * Created by jack on 12/05/2016.
 */
public class SettingActivity extends AppCompatActivity {

    ListView lv;
    Context context;
    private SeekBar range = null;
    public TextView current=null;

    ArrayList prgmName;
    public static int [] prgmImages={R.drawable.bored,R.drawable.lol,R.drawable.sad};
    public static String [] prgmNameList={"bored","happy","sad"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);

        context=this;

        lv=(ListView) findViewById(R.id.listView);
        lv.setAdapter(new ListSettingAdapter(this, prgmNameList, prgmImages));


        current= (TextView) findViewById(R.id.curentValue);
        range = (SeekBar) findViewById(R.id.seekbar);
        current.setText(String.valueOf(0));

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged=0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    current.setText(String.valueOf(progressChanged));
            }
        });

        int min=0;
        int max=200;

        TextView minValue = (TextView) findViewById(R.id.minValue);
        TextView maxValue = (TextView) findViewById(R.id.maxValue);

        minValue.setText(String.valueOf(min));
        maxValue.setText(String.valueOf(max));


     }
}
