package com.demo.nikunj.splitwise;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OM on 3/13/2018.
 */

public class lv_split_adapter extends ArrayAdapter<UserUID>{
   // SparseBooleanArray mCheckStates;

    /**
     * Created by Belal on 9/14/2017.
     */

//we need to extend the ArrayAdapter class as we are building an adapter


        //the list values in the List of type hero
        List<UserUID> group_member_List;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public lv_split_adapter(Context context, int resource, ArrayList<UserUID> group_member_List) {
            super(context, resource, group_member_List);
            this.context = context;
            this.resource = resource;
            this.group_member_List  = group_member_List;
            //mCheckStates = new SparseBooleanArray(group_member_List.size());
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            //we need to get the view of the xml for our list item
            //And for this we need a layoutinflater
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            //getting the view
            View view = layoutInflater.inflate(resource, null, false);

            //getting the view elements of the list from the view
            ImageView profile_pic = view.findViewById(R.id.iv_frnd_profile_pic);
            TextView tv_name = view.findViewById(R.id.tv_frnd_name);
            if(view.findViewById(R.id.checkBox)!=null) {
                CheckBox check = view.findViewById(R.id.checkBox);
            }else if(view.findViewById(R.id.et_percent)!=null){
                EditText percentage=view.findViewById(R.id.et_percent);
            }
            //check.setTag(position);




            //getting the hero of the specified position
            //String group = group_member_List.get(position);
            UserUID temp=group_member_List.get(position);
            Glide.with(getContext()).load(Uri.parse(temp.getImageUri())).into(profile_pic);

            //adding values to the list item
           // imageView.profi);
           // imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
            tv_name.setText(temp.getName());
           /* check.setTag(position);
            check.setChecked(mCheckStates.get(position,false));
            check.setOnCheckedChangeListener(this);*/
            //finally returning the view
            return view;
        }
      /*  public boolean isChecked(int position) {
         return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);

        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));

        }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCheckStates.put((Integer)compoundButton.getTag(),b);
    }*/
}
