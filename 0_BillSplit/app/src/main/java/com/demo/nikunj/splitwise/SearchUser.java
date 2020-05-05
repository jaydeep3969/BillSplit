package com.demo.nikunj.splitwise;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchUser extends AppCompatActivity {
    MaterialSearchView searchView;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    ListView lstview;
    ArrayList<String> list;
    Map<String,String> userlist=new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mDatabase=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        lstview=(ListView)findViewById(R.id.lstview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search User");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView=(MaterialSearchView)findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ArrayAdapter adapter=new ArrayAdapter(SearchUser.this,android.R.layout.simple_list_item_1,list);
                lstview.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    List<String> found=new ArrayList<String>();
                    for(String i:list){
                        if(i.contains(newText)){
                            found.add(i);
                        }
                        ArrayAdapter adapter=new ArrayAdapter(SearchUser.this,android.R.layout.simple_list_item_1,found);
                        lstview.setAdapter(adapter);
                    }
                }
                else{
                    ArrayAdapter adapter=new ArrayAdapter(SearchUser.this,android.R.layout.simple_list_item_1,list);
                    lstview.setAdapter(adapter);
                }
                return true;
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

        lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                final String email=lstview.getItemAtPosition(i).toString();
                final String ukey;
                //new CustomToast().Show_Toast(SearchUser.this,view,email);
                AlertDialog.Builder a_builder=new AlertDialog.Builder(SearchUser.this);
                if (userlist.containsKey(email)) {
                    ukey=userlist.get(email);
                }else{
                    ukey="default";
                }
                if(getIntent().hasExtra("grpid")) {
                    a_builder.setMessage("You Want To add : " + email).setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Bundle grpid = getIntent().getExtras();
                                    if (grpid != null) {
                                        String grpkey = grpid.getString("grpid");
                                        mDatabase.child("groups").child(grpkey).child("users").child(userlist.get(email)).setValue(email);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialossterface, int i) {

                                }
                            });
                    AlertDialog alert = a_builder.create();
                    alert.setTitle("GroupName");
                    alert.show();
                }else{
                    a_builder.setMessage("You Want To add: "+email).setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(!ukey.equals("default")){
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (!dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid()).hasChild(ukey)) {
                                                    UserUI newuser=new UserUI();
                                                    User data=dataSnapshot.child("users").child(ukey).getValue(User.class);
                                                    newuser.setAmount(0);
                                                    newuser.setImageUri(data.getImageUri());
                                                    newuser.setName(data.getFullname());
                                                    mDatabase.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(ukey).setValue(newuser);
                                                    if(!dataSnapshot.child("useruilist").child(ukey).hasChild(mAuth.getCurrentUser().getUid())){
                                                        UserUI myUI=dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid()).getValue(UserUI.class);
                                                        myUI.setAmount(0);
                                                        mDatabase.child("useruilist").child(ukey).child(mAuth.getCurrentUser().getUid()).setValue(myUI);
                                                    }
                                                } else {
                                                    new CustomToast().Show_Toast(SearchUser.this, view, "User Already Exists");
                                                    Intent i = new Intent(SearchUser.this, HomeActivity.class);
                                                    startActivity(i);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog alert = a_builder.create();
                    alert.setTitle("GroupName");
                    alert.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.action_seach);
        searchView.setMenuItem(item);
        return true;
    }
    private void showData(DataSnapshot dataSnapshot) {
        list=new ArrayList<>();
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            User user=new User();
            user.setFullname(ds.getValue(User.class).getFullname());
            user.setEmail(ds.getValue(User.class).getEmail());
            user.setNumber(ds.getValue(User.class).getNumber());
            list.add(user.getEmail());
            String key=ds.getKey();
            userlist.put(user.getEmail(),key);
        }
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        lstview.setAdapter(adapter);
    }
}
