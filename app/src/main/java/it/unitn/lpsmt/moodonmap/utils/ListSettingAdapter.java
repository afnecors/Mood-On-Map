package it.unitn.lpsmt.moodonmap.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import it.unitn.lpsmt.moodonmap.R;
import it.unitn.lpsmt.moodonmap.SettingActivity;

/**
 * Created by jack on 21/05/2016.
 */
public class ListSettingAdapter extends BaseAdapter {

    String [] result;
    Context context;
    int [] imageId;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;

    private static LayoutInflater inflater=null;
    public ListSettingAdapter(SettingActivity Activity, String[] prgmNameList, int[] prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=Activity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
        RadioButton radioBtn;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.setting_list_view, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.radioBtn=(RadioButton) rowView.findViewById(R.id.radio);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        holder.radioBtn.setClickable(false);

//        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (position != mSelectedPosition && mSelectedRB != null) {
//                    mSelectedRB.setChecked(false);
//                }
//
//                mSelectedPosition = position;
//                mSelectedRB = (RadioButton) v;
//            }
//        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                }
                holder.radioBtn.setChecked(true);
                mSelectedPosition = position;
                mSelectedRB = holder.radioBtn;
            }
        });

        if(mSelectedPosition != position){
            holder.radioBtn.setChecked(false);
        }else{
            holder.radioBtn.setChecked(true);
            if(mSelectedRB != null && holder.radioBtn != mSelectedRB){
                mSelectedRB = holder.radioBtn;
            }
        }

        return rowView;
    }

    public int getCurrentListViewPosition() {
        return this.mSelectedPosition;
    }

}
