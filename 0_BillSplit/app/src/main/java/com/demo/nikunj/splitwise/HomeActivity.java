package com.demo.nikunj.splitwise;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,tab_friends.OnFragmentInteractionListener,tab_groups.OnFragmentInteractionListener,tab_activity.OnFragmentInteractionListener
{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser loginuser;
    private TextView loginemail,username;
    private String authusername,authemail;
   // Map<String,UserUI> useruilist=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        final ImageView userprofile = (ImageView) header.findViewById(R.id.userprofile);
        final TextView name = (TextView) header.findViewById(R.id.name);
        final TextView email = (TextView) header.findViewById(R.id.email);;


        //loginemail=(TextView)findViewById(R.id.umail);
        //username=(TextView) findViewById(R.id.uname);
        //useridlist=new ArrayList<String>();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();
        loginuser=mAuth.getCurrentUser();
        //loginemail.setText(loginuser.getEmail());
        if(loginuser!=null){





            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User u = dataSnapshot.child("users").child(loginuser.getUid()).getValue(User.class);
                    name.setText(u.getFullname());
                    email.setText(u.getEmail());
                    Glide.with(getBaseContext()).load(Uri.parse(u.getImageUri())).into(userprofile);

                    CreteUserList(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
           /* mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    updateUserUI(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }else{
            new MainActivity();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this,AddBillActivity.class);
                startActivity(i);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Friends");
        tabLayout.getTabAt(1).setText("Groups");
        tabLayout.getTabAt(2).setText("Activity");


    }

    private void updateUserUI(DataSnapshot dataSnapshot) {
        //Toast.makeText(HomeActivity.this,"Yes Success",Toast.LENGTH_LONG);
       // mDatabase.child("nikunj").child("hello").setValue("World");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);
        }else{

        }
    }

    private void CreteUserList(DataSnapshot dataSnapshot) {
        List<String> useridlist=new ArrayList<>();

        ;
        //mDatabase.child("nikunj").setValue("Dhola");
        if(dataSnapshot.child("useruilist").hasChild(loginuser.getUid())){
            for(DataSnapshot ds:dataSnapshot.child("useruilist").child(loginuser.getUid()).getChildren()){
                if(!useridlist.contains(ds.getKey())){
                    useridlist.add(ds.getKey());
                }
            }
        }else{
            //remove after all user login completes
            User mydata=dataSnapshot.child("users").child(loginuser.getUid()).getValue(User.class);
            UserUI myUI=new UserUI();
            myUI.setName(mydata.getFullname());
            myUI.setAmount(0);
            myUI.setImageUri(mydata.getImageUri());
            mDatabase.child("useruilist").child(loginuser.getUid()).child(loginuser.getUid()).setValue(myUI);
        }
        for(DataSnapshot ds:dataSnapshot.child("groups").getChildren()){

            if(ds.child("users").hasChild(loginuser.getUid())){
                mDatabase.child("groupuilist").child(loginuser.getUid()).child(ds.getKey()).setValue(ds.child("grpname").getValue());
                for(DataSnapshot key:ds.child("users").getChildren()){
                    if(!useridlist.contains(key.getKey())){
                        useridlist.add(key.getKey());
                    }
                }
            }
        }
        DataSnapshot useruilist=dataSnapshot.child("useruilist");
        DataSnapshot userdata=dataSnapshot.child("users");
        for(String key:useridlist){
            if(userdata.hasChild(key)){
                if(!useruilist.child(mAuth.getCurrentUser().getUid()).hasChild(key)) {
                    UserUI temp = new UserUI();
                    temp.setName(userdata.child(key).child("fullname").getValue(String.class));
                    temp.setAmount(0);
                    temp.setImageUri(userdata.child(key).child("imageUri").getValue(String.class));
                    //mDatabase.child("rushab").setValue("sheta");
                    if (key.equals(loginuser.getUid())) {
                        //username.setText(temp.getName());
                    }
                    //new changes
                    if (!(useruilist.child(loginuser.getUid()).hasChild(key))) {
                        mDatabase.child("useruilist").child(loginuser.getUid()).child(key).setValue(temp);
                    } else {
                        temp.setAmount(useruilist.child(loginuser.getUid()).child(key).child("amount").getValue(float.class));
                        mDatabase.child("useruilist").child(loginuser.getUid()).child(key).setValue(temp);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.create_grp) {
            final EditText edittext = new EditText(HomeActivity.this);
            alert.setMessage("Enter Group Name");
            alert.setTitle("Groups");

            alert.setView(edittext);

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    //Editable YouEditTextValue = edittext.getText();
                    //OR
                    String group_name = edittext.getText().toString();
                    if(group_name!=null && !group_name.isEmpty()){
                        Group grp=new Group(group_name,mAuth.getCurrentUser().getUid());
                        String gkey=mDatabase.child("groups").push().getKey();
                        mDatabase.child("groups").child(gkey).setValue(grp);
                        mDatabase.child("groups").child(gkey).child("users").child(grp.getUid()).setValue(mAuth.getCurrentUser().getEmail());


                        Intent i = new Intent(HomeActivity.this,SearchUser.class);
                        i.putExtra("grpid",gkey);
                        startActivity(i);
                    }
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_home) {
            // Handle the home action
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(HomeActivity.this,ProfileActivity .class);
            startActivity(i);

        }/* else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_contact) {

        } */else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
