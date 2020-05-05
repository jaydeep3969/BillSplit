package com.demo.nikunj.splitwise;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.abs;

public class ActivityDetails extends AppCompatActivity {

    private TextView activity_title,total,tv_date,tv_owe,payerdetails,firstchar;
    private ListView lv_participant;
    private Button delete;
    private ImageView payerpic,activity_pic;
    private String myID="";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String payerid;
    private ImageButton back;

    Map<String,UserUID> userlist;
    UserAdapter adapter;
    Transaction t;
    String t_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mAuth=FirebaseAuth.getInstance();
        myID=mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        activity_title=(TextView)findViewById(R.id.activity_title);
        total=(TextView)findViewById(R.id.total);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_owe=(TextView)findViewById(R.id.tv_owe);
        payerdetails=(TextView)findViewById(R.id.tv_payer_details);
        lv_participant=(ListView)findViewById(R.id.lv_participant);
        delete=(Button) findViewById(R.id.btn_delete);
        payerpic=(ImageView)findViewById(R.id.iv_payer_pic);
        back = (ImageButton) findViewById(R.id.btn_back);
        firstchar=(TextView)findViewById(R.id.firstchar);
        activity_pic=(ImageView)findViewById(R.id.iv_activity_pic);

        t=(Transaction) getIntent().getSerializableExtra("Transaction");
        t_key = (String) getIntent().getSerializableExtra("Id");
        firstchar.setText(t.getDescription().substring(0,1).toUpperCase());
        activity_pic.setBackgroundColor(getRandomMaterialColor("400"));
        if(t!=null && !myID.isEmpty()){
            payerid=t.getId();
            userlist=t.getUserlist();
            activity_title.setText(t.getDescription());
            total.setText(String.valueOf(t.getTotal()));
            if(payerid.equals(myID)){
                tv_date.setText("Added By You on "+t.getDate());
                payerdetails.setText("You Paid ₹"+t.getTotal());
                float amount=0;
                for(Map.Entry<String,UserUID> u:userlist.entrySet()){
                    if(!u.getValue().getId().equals(mAuth.getCurrentUser().getUid())) {
                        amount += u.getValue().getAmount();
                    }
                }
                if(amount>0){
                    tv_owe.setText("You Get Back ₹"+amount);
                    tv_owe.setTextColor(Color.parseColor("#32CD32"));
                }else{
                    tv_owe.setText("You Owe ₹"+abs(amount));
                    tv_owe.setTextColor(Color.RED);
                }
            }else{
                tv_date.setText("Added By "+userlist.get(payerid).getName()+" on "+t.getDate());
                payerdetails.setText(userlist.get(payerid).getName()+" Paid ₹"+t.getTotal());
                if(userlist.containsKey(myID)){
                    float amount=userlist.get(myID).getAmount();
                    if(amount<0){
                        tv_owe.setText("You Get Back ₹"+abs(amount));
                        tv_owe.setTextColor(Color.parseColor("#32CD32"));
                    }else{
                        tv_owe.setText("You Owe ₹"+amount);
                        tv_owe.setTextColor(Color.RED);
                    }
                }
            }
            Glide.with(this).load(Uri.parse(userlist.get(payerid).getImageUri())).into(payerpic);
            adapter=new UserAdapter(userlist,ActivityDetails.this);
            lv_participant.setAdapter(adapter);
        }


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_transaction();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
    public class UserAdapter extends BaseAdapter{
        public final ArrayList mData;
        private Context context;

        public UserAdapter(Map<String,UserUID> map, Context context) {
            this.mData = new ArrayList();
            mData.addAll(map.entrySet());
            this.context = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, UserUID> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO implement you own logic with ID
            return 0;
        }
        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = layoutInflater.inflate(R.layout.userdetailsactivity, parent, false);
            } else {
                result = convertView;
            }
            ImageView profile_pic=(ImageView)result.findViewById(R.id.iv_frnd_profile_pic);
            TextView tv_detail=(TextView)result.findViewById(R.id.tv_detail);

            Map.Entry<String,UserUID> u=getItem(i);
            Glide.with(ActivityDetails.this).load(Uri.parse(u.getValue().getImageUri())).into(profile_pic);
            if(!u.getKey().equals(payerid)){
                if(u.getKey().equals(myID)){
                    tv_detail.setText("You owe ₹"+u.getValue().getAmount());
                }else{
                    tv_detail.setText(u.getValue().getName()+" owes ₹"+u.getValue().getAmount());
                }
            }else {
                if(u.getKey().equals(myID)){
                    tv_detail.setText("You owe ₹"+u.getValue().getAmount());
                }else{
                    tv_detail.setText(u.getValue().getName()+" owes ₹"+u.getValue().getAmount());
                }
            }
            return result;
        }
    }

    protected void delete_transaction()
    {
        final String payer  = t.getId();

        for (Map.Entry<String,UserUID> user:
             t.getUserlist().entrySet()) {
           final UserUID tmp = user.getValue();

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!tmp.getId().equals(payer))
                    {
                        float tmp_amount  = dataSnapshot.child("useruilist").child(payer).child(tmp.getId()).child("amount").getValue(Float.class);
                        tmp_amount -= tmp.getAmount();

                        mDatabase.child("useruilist").child(payer).child(tmp.getId()).child("amount").setValue(tmp_amount);

                        float tmp_amount1  = dataSnapshot.child("useruilist").child(tmp.getId()).child(payer).child("amount").getValue(Float.class);
                        tmp_amount1 += tmp.getAmount();

                        mDatabase.child("useruilist").child(tmp.getId()).child(payer).child("amount").setValue(tmp_amount1);

                        // for participant
                        if(t.getGrpkey().isEmpty())
                        {
                            mDatabase.child("UserTransactions").child(tmp.getId()).child(t_key).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        if(t.getGrpkey().isEmpty()) {
            mDatabase.child("UserTransactions").child(payer).child(t_key).removeValue(); //for payer
            mDatabase.child("Transaction").child(t_key).removeValue();
        }
        else {
            mDatabase.child("GroupTransaction").child(t.getGrpkey()).child(t_key).removeValue();
        }

        //new CustomToast().Show_Toast(ActivityDetails.this,,"Successfully Deleted");

        Intent i = new Intent(ActivityDetails.this,HomeActivity.class);
        startActivity(i);


    }
}
