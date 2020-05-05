package com.demo.nikunj.splitwise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tab_exact.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_exact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_exact extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_exact() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tab_activity.
     */
    // TODO: Rename and change types and number of parameters
    public static tab_exact newInstance(String param1, String param2) {
        tab_exact fragment = new tab_exact();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
    private ArrayList<UserUID> group_member_List ;
    private ListView lv_group_member;
    private TextView tv_taken_amount;
    private TextView tv_total_amount;
    private TextView tv_left_amount;
    private TextView txt_left,txt_rupees;
    private String grpkey="";
    private String grpname="";
    private String description;
    private String date;
    private Transaction transaction;
    List<String> useridlist=new ArrayList<>();
    Map<String,UserUI> useruilist=new HashMap<>();
    //lv_split_adapter adapter;
    exact_adapter adapter;
    private UserUI myUI;
    private String myID;
    float total=0;
    float taken=0;
    float tmp = 0 ;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_tab_exact, container, false);

        lv_group_member = (ListView) view.findViewById(R.id.lv_group_member_exact);
        // btn_save=(Button)getActivity().findViewById(R.id.btn_save);
        tv_taken_amount = (TextView) view.findViewById(R.id.tv_in_amount);
        tv_left_amount = (TextView) view.findViewById(R.id.tv_left_amount);
        tv_total_amount = (TextView) view.findViewById(R.id.tv_total_amount);
        txt_left = (TextView) view.findViewById(R.id.txt_left);
        txt_rupees = (TextView) view.findViewById(R.id.txt_rupees);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u=mAuth.getCurrentUser();
        grpkey=getActivity().getIntent().getStringExtra("grpkey");
        total=Float.valueOf(getActivity().getIntent().getStringExtra("total"));
        description=getActivity().getIntent().getStringExtra("dec");
        date=getActivity().getIntent().getStringExtra("date");

        tv_total_amount.setText(String.valueOf(total));
        tv_left_amount.setText(String.valueOf(total));

        if(u!=null) {
            myID=u.getUid();
        }

        if(u!=null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    grpname=dataSnapshot.child("groupuilist").child(myID).child(grpkey).getValue(String.class);
                    setListView(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



       /* btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomToast().Show_Toast(getContext(),view,"Exact");
            }
        });*/
       /* btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"save_clicked",Toast.LENGTH_LONG);
              /*  if((total - taken)!= 0)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("The math for this expense doesn't add up.Please double-check that you've entered the correct amounts for each person!");
                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }*/

                /*final String myID=mAuth.getCurrentUser().getUid();
                for(int i=0;i<adapter.getCount();i++){
                    View itemview=lv_group_member.getChildAt(i);
                    CheckBox cb=itemview.findViewById(R.id.checkBox);
                    final float splitamount=Float.valueOf(amount_per_person.getText().toString());
                    if(cb.isChecked()){
                        final UserUID temp=adapter.getItem(i);
                        if(!temp.getId().equals(myID)){
                            mDatabase.child("useruilist").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //for Myui
                                    float frndamount=dataSnapshot.child(myID).child(temp.getId()).child("amount").getValue(float.class);
                                    frndamount=frndamount+splitamount;
                                    mDatabase.child("useruilist").child(myID).child(temp.getId()).child("amount").setValue(frndamount);
                                    if(dataSnapshot.hasChild(temp.getId())){
                                        if(dataSnapshot.child(temp.getId()).hasChild(myID)){
                                            Float amount=dataSnapshot.child(temp.getId()).child(myID).child("amount").getValue(float.class);
                                            amount=amount-splitamount;
                                            mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(amount);
                                        }else{
                                            mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                            mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-splitamount);
                                        }
                                    }else{
                                        mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                        mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-splitamount);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            // Intent intent=new Intent(getContext(),HomeActivity.class);
                            //startActivity(intent);
                        }
                    }
                }*/
        //  }
        //});

        return view;
    }


    public  void  setListView(DataSnapshot dataSnapshot)
    {
        group_member_List = new ArrayList<UserUID>();
        for(DataSnapshot ds:dataSnapshot.child("groups").child(grpkey).child("users").getChildren()){
            if(!useridlist.contains(ds.getKey())) {
                useridlist.add(ds.getKey());
            }
        }

        DataSnapshot useruidata=dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid());
        for(String key:useridlist){
            //3/20/2018
            if(useruidata.hasChild(key)) {
                UserUID userUID = new UserUID(key);
                UserUI temp = useruidata.child(key).getValue(UserUI.class);
                userUID.setAmount(0);
                userUID.setImageUri(temp.getImageUri());
                userUID.setName(temp.getName());
                if (key.equals(mAuth.getCurrentUser().getUid())) {
                    myUI = temp;
                }
                useruilist.put(useruidata.child(key).getKey(), useruidata.child(key).getValue(UserUI.class));
                group_member_List.add(userUID);
            }
        }
        if(getActivity()!=null) {
            adapter = new exact_adapter(group_member_List,getContext());
            lv_group_member.setAdapter(adapter);
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


    public class exact_adapter extends BaseAdapter {
        public ArrayList<UserUID> userlist;
        private Context context;

        public exact_adapter(ArrayList<UserUID> userlist, Context context) {
            this.userlist = userlist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return userlist.size();
        }

        @Override
        public UserUID getItem(int i) {
            return userlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final tab_exact.ViewHolder holder;
            final View row;
            view = null;
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.custom_lv_exact, viewGroup, false);
                holder = new tab_exact.ViewHolder();
                holder.tv_name = row.findViewById(R.id.tv_frnd_name);
                holder.profile_pic = row.findViewById(R.id.iv_frnd_profile_pic);
                holder.et_amount = row.findViewById(R.id.et_amount);
                holder.et_amount.setHint(String.valueOf(0.00));
                row.setTag(holder);
            } else {
                row=view;
                holder=(tab_exact.ViewHolder)row.getTag();
            }
            UserUID temp = userlist.get(i);
            holder.tv_name.setText(temp.getName());
            Glide.with(getContext()).load(Uri.parse(temp.getImageUri())).into(holder.profile_pic);
            holder.et_amount.setText(String.valueOf(userlist.get(i).getAmount()));

            holder.et_amount.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    if(!s.toString().isEmpty()){
                        tmp =  Float.valueOf(holder.et_amount.getText().toString());
                        taken = taken - tmp;}
                    {}
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(!s.toString().isEmpty()){
                        taken += Float.valueOf(s.toString());
                        userlist.get(i).setAmount(Float.valueOf(s.toString()));
                    }
                    tv_taken_amount.setText(String.valueOf(taken));
                    tv_left_amount.setText(String.valueOf(total-taken));
                    if(total-taken < 0)
                    {
                        tv_left_amount.setTextColor(Color.RED);
                        txt_left.setTextColor(Color.RED);
                        txt_rupees.setTextColor(Color.RED);
                    }
                    else
                    {
                        tv_left_amount.setTextColor(Color.BLACK);
                        txt_left.setTextColor(Color.BLACK);
                        txt_rupees.setTextColor(Color.BLACK);
                    }
                }
            });

            return row;
        }
    }
    class ViewHolder{
        ImageView profile_pic;
        TextView tv_name;
        EditText et_amount;
    }

  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        menu.clear();
        inflater.inflate(R.menu.menu_split, menu);

        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                //new CustomToast().Show_Toast(getContext(),view,"EXACT");
                addTransaction();
                return true;

            default:
                break;
        }

        return false;
    }

    private void addTransaction() {
        if(Float.parseFloat(tv_left_amount.getText().toString())==0){
            for(int i=0;i<adapter.getCount();i++){
                final UserUID temp=adapter.getItem(i);
                if(!temp.getId().equals(myID)){
                    final float frndshare=temp.getAmount();
                    //frndshare=frndshare*total/total_share;
                    //temp.setAmount(frndshare);
                    mDatabase.child("useruilist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            float frndamount=dataSnapshot.child(myID).child(temp.getId()).child("amount").getValue(float.class);
                            frndamount=frndamount+frndshare;
                            mDatabase.child("useruilist").child(myID).child(temp.getId()).child("amount").setValue(frndamount);
                            if(dataSnapshot.hasChild(temp.getId())){
                                if(dataSnapshot.child(temp.getId()).hasChild(myID)){
                                    Float amount=dataSnapshot.child(temp.getId()).child(myID).child("amount").getValue(float.class);
                                    amount=amount-frndshare;
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(amount);
                                }else{
                                    //myUI.setAmount(0);
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-frndshare);
                                }
                            }else{
                                mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-frndshare);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            transaction=new Transaction(description,total,date,myID);
            transaction.setGrpkey(grpkey);
            transaction.setGrpname(grpname);
            for(int i=0;i<adapter.getCount();i++){
                if(adapter.getItem(i).getAmount()!=0){
                    transaction.userlist.put(adapter.getItem(i).getId(),adapter.getItem(i));
                }
            }
            mDatabase.child("GroupTransaction").child(grpkey).push().setValue(transaction);
            new CustomToast_true().Show_Toast(getContext(),view,"Transaction Added Successfully");
            Intent i=new Intent(getContext(),HomeActivity.class);
            startActivity(i);
        }else{
            new CustomToast().Show_Toast(getContext(),view,"Share should not be 0 :)");
        }

    }
}
