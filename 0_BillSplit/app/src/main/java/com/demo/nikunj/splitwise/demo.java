package com.demo.nikunj.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class demo extends AppCompatActivity {
    private Button signout,users;
    private ListView userlist;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        signout = (Button)findViewById(R.id.signout);
        users=(Button)findViewById(R.id.users);
        userlist=(ListView)findViewById(R.id.list);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent i = new Intent(demo.this,MainActivity.class);
            startActivity(i);
        }else{

        }
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(demo.this,MainActivity.class);
                startActivity(i);
            }
        });
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(demo.this,SearchUser.class);
                startActivity(i);
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        ArrayList<String> list=new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            User user=new User();
            user.setFullname(ds.getValue(User.class).getFullname());
            user.setEmail(ds.getValue(User.class).getEmail());
            user.setNumber(ds.getValue(User.class).getNumber());

            list.add(user.getEmail());
            list.add(user.getFullname());
            list.add(user.getNumber());


        }
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        userlist.setAdapter(adapter);
    }

}
