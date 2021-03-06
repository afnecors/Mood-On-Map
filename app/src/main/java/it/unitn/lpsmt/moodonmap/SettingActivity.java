package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    Button buttonSave=null;
    int pos;
    int progressChanged;

    ArrayList prgmName;

    public static int [] prgmImages = {
            R.drawable.bored,
            R.drawable.lol,
            R.drawable.sad,
            R.drawable.cry,
            R.drawable.vomit,
            R.drawable.love,
            R.drawable.cool
    };
    public static String [] prgmNameList = {"bored", "happy", "sad", "cry", "vomit", "love", "cool"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);     // Toolbar nella schermata principale
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context=this;

        lv=(ListView) findViewById(R.id.listView);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ListSettingAdapter myAdapter = new ListSettingAdapter(this, prgmNameList, prgmImages);
        lv.setAdapter(myAdapter);

        setListViewHeightBasedOnChildren(lv);

        current= (TextView) findViewById(R.id.curentValue);
        range = (SeekBar) findViewById(R.id.seekbar);
        current.setText(String.valueOf(0));

        buttonSave=(Button) findViewById(R.id.ButtonSave);

        progressChanged=0;
        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = myAdapter.getCurrentListViewPosition();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("range", progressChanged);
                intent.putExtra("position",pos);//pos 0: bored pos 1: lol pos 2: sad
                intent.putExtra("activity_id", "setting");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


     }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListSettingAdapter listAdapter =(ListSettingAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        //480x800 da 2
        //540x960 da 3
        //1440x2560 da 4
        int k=2;
        //Determine screen size

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        if(height>880){
            k=3;
            Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG).show();
        }else if(height>1760){
            k=4;
            Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show();
        }

        int totalHeight = 0;
        for (int i = 0; i < k; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
