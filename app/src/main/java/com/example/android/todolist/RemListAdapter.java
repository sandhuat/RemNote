package com.example.android.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dell on 4/21/2016.
 */
public class RemListAdapter extends ArrayAdapter<FileName>
{
    // Declare Variables
    String akaron;
    boolean isReminderSet;
    Context context;
    LayoutInflater inflater;
    public static List<FileName> filenames;
    private SparseBooleanArray mSelectedItemsIds;
    int ch;
    public RemListAdapter(Context context, int resourceId, List<FileName> filenames,int j) {
        super(context, resourceId, filenames);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.filenames = filenames;
        ch=j;
        inflater = LayoutInflater.from(context);
    }



    private class ViewHolder {
        TextView filename;
        int position;
        public void setId(int i)
        {
            position=i;
        }
        public int getId()
        {
            return position;
        }
        TextView shorttext;
        ImageView remind;

    }

    public View getView(int position, View view, ViewGroup parent)
    {
        Animation animation = null;

        ViewHolder holder = null;

        try {
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_item, null);
                ImageView ia = (ImageView) view.findViewById(R.id.todolist);
                animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
                holder.remind = (ImageView) view.findViewById(R.id.remind);
                ImageView ib = (ImageView) view.findViewById(R.id.note);
                ImageView ic = (ImageView) view.findViewById(R.id.voicelog);
                SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
                Log.e("TAG", "AKARON IS " + position);
                String short_desc = sharedpreferences.getString(filenames.get(position).getName() + "DATE*&^", "");
                Log.e("TAG", "The FILE RECEIVED ARE " + akaron);
                if (akaron.equals("two"))
                {
                    ib.setVisibility(View.GONE);
                    ic.setVisibility(View.GONE);
                    ia.setVisibility(View.VISIBLE);
                }
                else if (akaron.equals("one")) {
                    ic.setVisibility(View.GONE);
                    ia.setVisibility(View.GONE);
                    ib.setVisibility(View.VISIBLE);
                } else if (akaron.equals("three")) {
                    ia.setVisibility(View.GONE);
                    ib.setVisibility(View.GONE);
                    ic.setVisibility(View.VISIBLE);
                }
                isReminderSet = sharedpreferences.getBoolean(filenames.get(position).getName() + "REMINDER#$@", false);
                Log.e("asd", "FILEN IN ADAPTER " + isReminderSet);

                if (isReminderSet)
                {
                    holder.remind.setVisibility(View.VISIBLE);
                    Log.e("sdf", "REMIMAGE IS VISIBLE");
                    Main2.adapter.notifyDataSetChanged();
                }
                else {
                    holder.remind.setVisibility(View.INVISIBLE);
                    Log.e("sdf", "REMIMAGE IS NOT VISIBLE");

                    Main2.adapter.notifyDataSetChanged();
                }

                // Locate the TextViews in listview_item.xml
                holder.filename = (TextView) view.findViewById(R.id.item_title);
                holder.shorttext = (TextView) view.findViewById(R.id.item_desc);
                view.setTag(holder);
                holder.setId(position);
                animation.setDuration(500);
                view.startAnimation(animation);
                animation = null;
            }
            else
            {

                holder = (ViewHolder) view.getTag();
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            Log.e("sad","Exception handled in ListViewAdapter");
        }
        // Capture position and set to the TextViews
        try {
            int len = filenames.get(position).getName().length();
            Log.e("sa","THE POS IS "+position+" and the size is "+filenames.size());
            holder.filename.setText(filenames.get(position).getName().substring(0, len - 4));
            int len2 = filenames.get(position).getShorttext().length();
            SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            String short_desc = sharedpreferences.getString(filenames.get(position).getName() + "DATE*&^", "")+" "+sharedpreferences.getString(filenames.get(position).getName() + "TIME*&^", "");
            boolean isCrossed = sharedpreferences.getBoolean(filenames.get(position).getName() + "CHECKED#$@", false);
            if (ch == 2)
            {
                holder.shorttext.setText(short_desc);
            }
            else
            {
                if(akaron.equals("three"))
                {

                    holder.shorttext.setText("Sound Recording");
                }
                else if (len2 > 30)
                    holder.shorttext.setText(filenames.get(position).getShorttext().substring(0, 30));
                else if (len2 > 0)
                    holder.shorttext.setText(filenames.get(position).getShorttext());
                else if (len2 == 0)
                    holder.shorttext.setText("Empty file ....");


                if (akaron.equals("two") && isCrossed) {
                    holder.shorttext.setPaintFlags(holder.shorttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            isReminderSet = sharedpreferences.getBoolean(filenames.get(position).getName() + "REMINDER#$@", false);
            if (isReminderSet)
            {
                holder.remind.setVisibility(View.VISIBLE);
                Log.e("sdf", "REMIMAGE IS VISIBLE");
                Main2.adapter.notifyDataSetChanged();
            }
            else {
                holder.remind.setVisibility(View.INVISIBLE);
                Log.e("sdf", "REMIMAGE IS NOT VISIBLE");

                Main2.adapter.notifyDataSetChanged();
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            Log.e("TAG","Exception handled2 in listViewAdapter");
        }
        return view;
    }

    @Override
    public void remove(FileName object) {
        filenames.remove(object);
        notifyDataSetChanged();
    }

    public List<FileName> getFilenames() {
        return filenames;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection()
    {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }



    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}


