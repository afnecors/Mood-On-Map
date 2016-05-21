package it.unitn.lpsmt.moodonmap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import it.unitn.lpsmt.moodonmap.utils.OwnIconRendered;
import it.unitn.lpsmt.moodonmap.utils.PermissionUtils;
import it.unitn.lpsmt.moodonmap.utils.Place;
import it.unitn.lpsmt.moodonmap.utils.ListAdapter;

/**
 * Created by jack on 12/05/2016.
 */
public class SettingActivity extends AppCompatActivity {

    ListView lv;
    Context context;

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
        lv.setAdapter(new ListAdapter(this, prgmNameList, prgmImages));
    }
}
