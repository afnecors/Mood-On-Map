package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(new ListSettingAdapter(this, prgmNameList, prgmImages));


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

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("range", progressChanged);
                intent.putExtra("position",pos);//pos 0: bored pos 1: happy pos 2: sad
                intent.putExtra("activity_id", "setting");
                Toast.makeText(context, "You Selected "+pos, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });


     }
}
