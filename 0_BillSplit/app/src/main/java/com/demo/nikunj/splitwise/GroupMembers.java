package com.demo.nikunj.splitwise;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembers extends AppCompatActivity {
    private ListView userlist;
    private String grpkey="";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser loginuser;
    private Map<String,User> userdetailslist=new HashMap<>();
    private UserAdapter adapter;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        loginuser=mAuth.getCurrentUser();
        userlist=(ListView)findViewById(R.id.userlist);
        grpkey=getIntent().getStringExtra("grpkey");
        back=(ImageButton)findViewById(R.id.btn_back);
        if(loginuser!=null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showUserlist(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showUserlist(DataSnapshot dataSnapshot) {
        List<String> useridlist=new ArrayList<>();
        for(DataSnapshot ds:dataSnapshot.child("groups").child(grpkey).child("users").getChildren()){
            useridlist.add(ds.getKey());
        }
        for(String key:useridlist){
            User user=dataSnapshot.child("users").child(key).getValue(User.class);
            userdetailslist.put(key,user);
        }
        adapter=new UserAdapter(userdetailslist,GroupMembers.this);
        userlist.setAdapter(adapter);
    }
    public class UserAdapter extends BaseAdapter{
        public final ArrayList mData;
        private Context context;

        public UserAdapter(Map<String, User> map,Context context) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
            this.context=context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String,User> getItem(int i) {
            return (Map.Entry)mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = layoutInflater.inflate(R.layout.userdetails, parent, false);
            } else {
                result = convertView;
            }
            TextView username=result.findViewById(R.id.username);
            TextView email=result.findViewById(R.id.email);
            TextView mobilenumber=result.findViewById(R.id.mobileNumber);
            ImageView profilepic=result.findViewById(R.id.userprofile);
            Map.Entry<String,User> user=getItem(i);
            username.setText(user.getValue().getFullname());
            email.setText(user.getValue().getEmail());
            mobilenumber.setText(user.getValue().getNumber());
            Glide.with(GroupMembers.this).load(Uri.parse(user.getValue().getImageUri())).into(profilepic);

            return result;
        }
    }
}
