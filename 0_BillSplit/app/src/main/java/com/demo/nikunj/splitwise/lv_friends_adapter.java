package com.demo.nikunj.splitwise;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OM on 3/13/2018.
 */

public class lv_friends_adapter extends ArrayAdapter<UserUI> {

    /**
     * Created by Belal on 9/14/2017.
     */

//we need to extend the ArrayAdapter class as we are building an adapter


    //the list values in the List of type hero
    List<UserUI> friendsList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public lv_friends_adapter(Context context, int resource, ArrayList<UserUI> friendsList) {
        super(context, resource, friendsList);
        this.context = context;
        this.resource = resource;
        this.friendsList  = friendsList;
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
        TextView amount=view.findViewById(R.id.tv_money);
        TextView textViewTeam = view.findViewById(R.id.tv_owe_type_type);
        //String tempuri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2FIOexxOQmGpZxc2NV6wG2AfpLoKm1?alt=media&token=67aa9498-0496-4bd1-b197-880d9b56415a";
        //getting the hero of the specified position
        UserUI friend = friendsList.get(position);
        String tempuri=friend.getImageUri();
        Glide.with(getContext()).load(Uri.parse(tempuri)).into(profile_pic);


        //adding values to the list item
        // imageView.profi);
        // imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
        tv_name.setText(friend.getName());
        float amo=friend.getAmount();
        amount.setText(String.valueOf(amo));
        if(friend.getAmount()<0){
            textViewTeam.setText("You Owes");
            textViewTeam.setTextColor(Color.parseColor("#FF0000"));
            amount.setTextColor(Color.parseColor("#FF0000"));
        }else{
            textViewTeam.setText("You Are Owed");
            textViewTeam.setTextColor(Color.parseColor("#20b2aa"));
            amount.setTextColor(Color.parseColor("#20b2aa"));
        }
        //finally returning the view
        return view;
    }
}
