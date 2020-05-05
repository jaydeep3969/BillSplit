package com.demo.nikunj.splitwise;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tab_friends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_friends extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tab_friends.
     */
    // TODO: Rename and change types and number of parameters
    public static tab_friends newInstance(String param1, String param2) {
        tab_friends fragment = new tab_friends();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private ListView lv_friends;
    private TextView username,balance;
    private ImageView userprofile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<UserUI> friendsList ;
    private ArrayList<String> useridlist;
    private Button addfriends;
    lv_friends_adapter adapter;
    String uname="default";
    private UserUI myUI=new UserUI();
    int year,month,day;
    Calendar cal;
    String []months={"January","February","March","April","May","June","July","August","September","Octomber","November","December"};
    DatePickerDialog.OnDateSetListener mDateSetListener;
    float YouEditTextValue;
    Transaction transaction;
    Map<String,UserUID> userlist=new HashMap<>();

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState)
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_friends, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        lv_friends = (ListView) view.findViewById(R.id.lv_friends);
        lv_friends.setLongClickable(true);
        username=(TextView)view.findViewById(R.id.username);
        balance=(TextView)view.findViewById(R.id.totalbalance);
        userprofile=(ImageView)view.findViewById(R.id.userprofile);
        addfriends=(Button)view.findViewById(R.id.add_friends);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u=mAuth.getCurrentUser();
        if(u!=null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(getActivity()!=null) {
                        setListView(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            lv_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                float amount;
                UserUI friendUI;
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String user2=useridlist.get(i);
                    friendUI=friendsList.get(i);

                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.custom_add_bill, null);
                    final EditText description = (EditText) alertLayout.findViewById(R.id.et_description);
                    final EditText amount = (EditText)alertLayout.findViewById(R.id.et_amount);
                    final TextView date = (TextView) alertLayout.findViewById(R.id.date);

                    cal= Calendar.getInstance();
                    year=cal.get(Calendar.YEAR);
                    month=cal.get(Calendar.MONTH);
                    day=cal.get(Calendar.DAY_OF_MONTH);


                    mDateSetListener=new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            date.setText(new StringBuilder().append(day).append(" ").append(months[month])
                                    .append(" ").append(year));
                        }
                    };
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShowDateDialog();
                        }
                    });




                    date.setText(new StringBuilder().append(day).append(" ").append(months[month])
                            .append(" ").append(year));

                    //final EditText edittext = new EditText(getContext());
                    //alert.setMessage("Enter Amount");
                    alert.setTitle("Split Equally");

                    alert.setView(alertLayout);

                    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //What ever you want to do with the value
                            //Editable YouEditTextValue = edittext.getText();
                            //OR
                            YouEditTextValue = Float.parseFloat(amount.getText().toString());
                            transaction = new Transaction(description.getText().toString(),YouEditTextValue,date.getText().toString(),mAuth.getCurrentUser().getUid());


                            mDatabase.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(user2).child("amount").setValue(friendUI.getAmount()+YouEditTextValue/2);
                            mDatabase.child("useruilist").addListenerForSingleValueEvent(new ValueEventListener() {
                                float myamount;
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    UserUID temp_uid  = new UserUID();
                                    temp_uid.setAmount(YouEditTextValue/2);

                                    //for current user
                                    String uid=mAuth.getCurrentUser().getUid();
                                    temp_uid.setId(uid);
                                    temp_uid.setName(dataSnapshot.child(uid).child(uid).child("name").getValue(String.class));
                                    temp_uid.setImageUri(dataSnapshot.child(uid).child(uid).child("imageUri").getValue(String.class));
                                    userlist.put(uid,temp_uid);

                                    if(dataSnapshot.hasChild(user2)) {
                                        myamount=dataSnapshot.child(user2).child(mAuth.getCurrentUser().getUid()).child("amount").getValue(float.class);
                                        myUI.setAmount(myamount-YouEditTextValue/2);
                                        mDatabase.child("useruilist").child(user2).child(mAuth.getCurrentUser().getUid()).setValue(myUI);
                                    }else{
                                        myamount=-YouEditTextValue/2;
                                        myUI.setAmount(myamount);
                                        mDatabase.child("useruilist").child(user2).child(mAuth.getCurrentUser().getUid()).setValue(myUI);
                                    }

                                    //for user2
                                    UserUID temp_uid2  = new UserUID();
                                    temp_uid2.setAmount(YouEditTextValue/2);
                                    temp_uid2.setId(user2);
                                    temp_uid2.setName(dataSnapshot.child(uid).child(user2).child("name").getValue(String.class));
                                    temp_uid2.setImageUri(dataSnapshot.child(uid).child(user2).child("imageUri").getValue(String.class));
                                    userlist.put(user2,temp_uid2);
                                    transaction.setUserlist(userlist);
                                    String key = mDatabase.child("Transaction").push().getKey();
                                    mDatabase.child("Transaction").child(key).setValue(transaction);
                                    mDatabase.child("UserTransactions").child(mAuth.getCurrentUser().getUid()).child(key).setValue(key);
                                    mDatabase.child("UserTransactions").child(user2).child(key).setValue(key);
                                    new CustomToast_true().Show_Toast(getContext(),getView(),"Transaction Added Successfully");


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }
                    });



                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert.show();
                }
            });
            addfriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(getContext(),SearchUser.class);
                    startActivity(i);
                }
            });
            lv_friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String userid=useridlist.get(i);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Settle-Up");
                    UserUI friend=friendsList.get(i);
                    alert.setMessage("Amount : "+friend.getAmount());
                    alert.setPositiveButton("Settle-UP", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mDatabase.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(userid).child("amount").setValue(0);
                            mDatabase.child("useruilist").child(userid).child(mAuth.getCurrentUser().getUid()).child("amount").setValue(0);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert.show();
                    return true;
                }
            });
        }

        return  view;
    }

    private void ShowDateDialog() {
        DatePickerDialog dialog=new DatePickerDialog(getContext(),android.R.style.Theme_Material_Dialog,mDateSetListener,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        dialog.show();
    }

    public  void  setListView(DataSnapshot dataSnapshot)
    {
        //mDatabase.child("nikunj2").setValue("dhola2");
        float amount=0;
        friendsList = new ArrayList<UserUI>();
        useridlist=new ArrayList<String>();
        //balance.setText(dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid()).child("amount").getValue(String.class).toString());
        for(DataSnapshot ds: dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid()).getChildren()){
            if(!ds.getKey().equals(mAuth.getCurrentUser().getUid())) {
                friendsList.add(ds.getValue(UserUI.class));
                amount+=ds.getValue(UserUI.class).getAmount();
                useridlist.add(ds.getKey());
            }else{
                myUI=ds.getValue(UserUI.class);
                Glide.with(this).load(Uri.parse(myUI.getImageUri())).into(userprofile);
            }
        }
        username.setText(dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("fullname").getValue(String.class));
        //uname=dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("fullname").getValue(String.class);
        balance.setText(String.valueOf(amount));
        if(getActivity()!=null) {
            adapter = new lv_friends_adapter(getContext(), R.layout.custom_lv_friends, friendsList);
            lv_friends.setAdapter(adapter);
            return;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}


