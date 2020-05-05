package com.demo.nikunj.splitwise;


import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by OM on 3/13/2018.
 */

public class lv_groups_adapter extends BaseAdapter {
        public final ArrayList mData;
        private Context context;
        public lv_groups_adapter(Context context, Map<String,String> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
            this.context=context;
        }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String,String> getItem(int i) {
        return (Map.Entry)mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final View result;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = layoutInflater.inflate(R.layout.custom_lv_friends, parent, false);
            } else {
                result = convertView;
            }
            //getting the view elements of the list from the view
            ImageView profile_pic = result.findViewById(R.id.iv_frnd_profile_pic);
            TextView tv_name = result.findViewById(R.id.tv_frnd_name);
            TextView amount=result.findViewById(R.id.tv_money);
            TextView textViewTeam = result.findViewById(R.id.tv_owe_type_type);

            Map.Entry<String,String> groups=getItem(position);
            //getting the hero of the specified position
            String group = groups.getValue();
            String uri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2Fboard-services-sm.png?alt=media&token=ad6c0a3d-39a3-45ea-8211-4e1d8127fe97";
            Glide.with(result).load(Uri.parse(uri)).into(profile_pic);
            profile_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            //adding values to the list item
           // imageView.profi);
           // imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
            tv_name.setText(group);
            amount.setText("");
            textViewTeam.setText("");
            //finally returning the view
            return result;
        }
}
