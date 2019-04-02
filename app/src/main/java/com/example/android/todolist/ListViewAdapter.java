package com.example.android.todolist;

/**
 * Created by Dell on 2/22/2016.
 */

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<FileName> implements Filterable
{
    // Declare Variables
    String akaron;
    String prevakaron="";
    boolean isReminderSet;
    Context context; ImageView ia,ib,ic;
    LayoutInflater inflater;
    public static List<FileName> filenames;
    private SparseBooleanArray mSelectedItemsIds;
    public static List<FileName> data;
    Animation animation = null;

    int ch;
    private NameFilter filter;
    public ListViewAdapter(Context context, int resourceId, List<FileName> filenames,int j) {
        super(context, resourceId, filenames);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
       data=new ArrayList<FileName>();
        this.filenames = filenames;
        this.data.addAll(filenames);
        ch=j;
        inflater = LayoutInflater.from(context);
        Log.e("TAG","Size of filenames is "+filenames.size());
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
        boolean shouldAnimate=true;

    }

    public View getView(int position, View view, ViewGroup parent)
    {

        ViewHolder holder = null;
        Animation animation = null;

        try {
            if (view == null)
            {

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_item, null);
                ia = (ImageView) view.findViewById(R.id.todolist);
                holder.remind = (ImageView) view.findViewById(R.id.remind);
                ib = (ImageView) view.findViewById(R.id.note);
                animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
                ic = (ImageView) view.findViewById(R.id.voicelog);
                SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                int len12 = filenames.get(position).getName().length();

                akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
                int color=sharedpreferences.getInt(filenames.get(position).getName()+"CoLoR", -1);
                if(color!=-1)
                    view.setBackgroundColor(color);
                Log.e("TAG", "AKARON654 IS " + filenames.get(position).getName());
                String short_desc = sharedpreferences.getString(filenames.get(position).getName() + "DATE*&^", "");
                Log.e("TAG", "The FILE RECEIVED ARE " + akaron);
                if (akaron.equals("two"))
                {
                    ib.setVisibility(View.GONE);
                    ic.setVisibility(View.GONE);
                    ia.setVisibility(View.VISIBLE);
                    Log.e("TAG","Akaron Two");

                }
                else if (akaron.equals("one"))
                {
                    ic.setVisibility(View.GONE);
                    ia.setVisibility(View.GONE);
                    ib.setVisibility(View.VISIBLE);
                    Log.e("TAG","Akaron One");
                }
                else if (akaron.equals("three"))
                {
                    ia.setVisibility(View.GONE);
                    ib.setVisibility(View.GONE);
                    ic.setVisibility(View.VISIBLE);
                    Log.e("TAG","Akaron Three");
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
                if(holder.shouldAnimate)
                {
                    animation.setDuration(400);
                    view.startAnimation(animation);
                }
                holder.shouldAnimate=false;

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
            akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
            String short_desc = sharedpreferences.getString(filenames.get(position).getName() + "DATE*&^", "");
            boolean isCrossed = sharedpreferences.getBoolean(filenames.get(position).getName() + "CHECKED#$@", false);
            if (akaron.equals("two"))
            {
                ib.setVisibility(View.GONE);
                ic.setVisibility(View.GONE);
                ia.setVisibility(View.VISIBLE);
                Log.e("TAG","Akaron Two");

            }
            else if (akaron.equals("one"))
            {
                ic.setVisibility(View.GONE);
                ia.setVisibility(View.GONE);
                ib.setVisibility(View.VISIBLE);
                Log.e("TAG","Akaron One");
            }
            else if (akaron.equals("three"))
            {
                ia.setVisibility(View.GONE);
                ib.setVisibility(View.GONE);
                ic.setVisibility(View.VISIBLE);
                Log.e("TAG","Akaron Three");
            }
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



    private View newView(ViewGroup parent, int position)
    {
        // Getting view somehow...
        Animation animation = null;
        animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_item, parent, false);
       ViewHolder holder = new ViewHolder();
    //   View view = inflater.inflate(R.layout.listview_item, null);
        ia = (ImageView) view.findViewById(R.id.todolist);
    //    animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
        holder.remind = (ImageView) view.findViewById(R.id.remind);
        ib = (ImageView) view.findViewById(R.id.note);
        ic = (ImageView) view.findViewById(R.id.voicelog);
        view.setTag(holder);
        animation.setDuration(400);
        view.startAnimation(animation);
        animation = null;
        return view;
    }



    private void bindView(final int position, View inflatedView) {


        final ViewHolder holder = (ViewHolder) inflatedView.getTag();
       // holder.setId(position);
        SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int len12 = filenames.get(position).getName().length();
        akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
        Log.e("TAG", "AKARON654 IS " + filenames.get(position).getName());
        String short_desc = sharedpreferences.getString(filenames.get(position).getName() + "DATE*&^", "");
        Log.e("TAG", "The FILE RECEIVED ARE " + akaron);
        if (akaron.equals("two"))
        {
            ib.setVisibility(View.GONE);
            ic.setVisibility(View.GONE);
            ia.setVisibility(View.VISIBLE);
            Log.e("TAG","Akaron Two");

        }
        else if (akaron.equals("one"))
        {
            ic.setVisibility(View.GONE);
            ia.setVisibility(View.GONE);
            ib.setVisibility(View.VISIBLE);
            Log.e("TAG","Akaron One");
        }
        else if (akaron.equals("three"))
        {
            ia.setVisibility(View.GONE);
            ib.setVisibility(View.GONE);
            ic.setVisibility(View.VISIBLE);
            Log.e("TAG","Akaron Three");
        }


        // Locate the TextViews in listview_item.xml
        holder.filename = (TextView) inflatedView.findViewById(R.id.item_title);
        holder.shorttext = (TextView) inflatedView.findViewById(R.id.item_desc);
        try {
            int len = filenames.get(position).getName().length();
            Log.e("sa","THE POS IS "+position+" and the size is "+filenames.size());
            holder.filename.setText(filenames.get(position).getName().substring(0, len - 4));
            int len2 = filenames.get(position).getShorttext().length();
            Log.e("TAG","THE AKARON FOR FILE - "+filenames.get(position).getName()+" IS "+akaron);


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

        inflatedView.setTag(holder);
        holder.setId(position);





    }



    @Override
    public void remove(FileName object)
    {
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

    public void animateTo(List<FileName> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<FileName> newModels) {
        for (int i = filenames.size() - 1; i >= 0; i--) {
            final FileName model = filenames.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FileName> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final FileName model = newModels.get(i);
            if (!filenames.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<FileName> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final FileName model = newModels.get(toPosition);
            final int fromPosition = filenames.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    public FileName removeItem(int position) {
        final FileName model = filenames.remove(position);
        notifyDataSetChanged();
        return model;
    }

    public void addItem(int position, FileName model)
    {
        filenames.add(position, model);
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FileName model = filenames.remove(fromPosition);
        filenames.add(toPosition, model);
        notifyDataSetChanged();
    }

    public void resetData() {
        filenames.clear();
        filenames.addAll(data);
        Main2.adapter.notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {



        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0)
            {
                results.values = data;
                results.count = data.size();
            }
            else {
                Log.e("TAG", "Constraint is " + constraint.toString());
                // We perform filtering operation
                List<FileName> nPlanetList = new ArrayList<FileName>();

                for (FileName p : data)
                {
                    if (p.getName().toUpperCase().contains(constraint.toString().toUpperCase()) || p.getShorttext().toUpperCase().contains(constraint.toString().toUpperCase()) ||p.getContent().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);

                }


                for(int z=0;z<data.size();z++)
                    Log.e("TAG","THE DATA AT INDEX "+z+" is "+data.get(z).getName());

                for (int g = 0; g < nPlanetList.size(); g++)
                {
                    Log.e("TAG","THE PLANETS WITH INDEX "+g+" are "+nPlanetList.get(g).getName());
                }
                Log.e("TAG","nPlanetList size is "+nPlanetList.size());


                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filenames.clear();
                Main2.adapter.notifyDataSetChanged();
            }
            else
            {
//if(((List<FileName>) results.values).size()<1)
    //filenames.clear();
              //  filenames = (List<FileName>) results.values;
                int v;
                for(int f=0;f<filenames.size();f++)
                {
                    Log.e("TAG","The filenamesbhf with the index "+f+" is "+filenames.get(f).getName());
                }
                filenames.clear();
                filenames.addAll(((List<FileName>) results.values));
             //   for( v=0;v<((List<FileName>) results.values).size();v++)
               // {
                  //  filenames.set(v,((List<FileName>) results.values).get(v));
                //}
                for(int f=0;f<filenames.size();f++)
                {
                    Log.e("TAG","The filenamesbhf2 with the index "+f+" is "+filenames.get(f).getName());
                }
            //    for(int m=v;m<filenames.size();m++)
              //      filenames.remove(m);
                for(int j=0;j<filenames.size();j++)
                    Log.e("TAG","THE FileNames WITH INDEX "+j+" are "+filenames.get(j).getName());

                Main2.adapter.notifyDataSetChanged();
            }

        }

    }
}

