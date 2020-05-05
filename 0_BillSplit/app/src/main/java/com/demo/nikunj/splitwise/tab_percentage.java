package com.demo.nikunj.splitwise;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tab_percentage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_percentage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_percentage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_percentage() {
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
    public static tab_percentage newInstance(String param1, String param2) {
        tab_percentage fragment = new tab_percentage();
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
    private Button btn_save;
    private String grpkey="";
    private String grpname="";
    private String description;
    private String date;
    private float total=0;
    private List<String> useridlist=new ArrayList<>();
    private UserUI myUI;
    private Map<String,UserUI> useruilist=new HashMap<>();
    private percent_adapter adapter;
    private lv_split_adapter adapt;
    private TextView left_percent;
    private float leftpercent=100;
    private TextView used_percent;
    private String myID;
    private Transaction transaction;
    private TextView txt_left;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_tab_percentage, container, false);

        lv_group_member = (ListView) view.findViewById(R.id.lv_group_member_percentage);
       // btn_save=(Button)getActivity().findViewById(R.id.btn_save);
        left_percent=(TextView)view.findViewById(R.id.tv_left_percent);
        used_percent=(TextView)view.findViewById(R.id.tv_in_percent);
        txt_left=(TextView)view.findViewById(R.id.txt_left);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u=mAuth.getCurrentUser();
        if(u!=null){
            myID=u.getUid();
        }

        grpkey=getActivity().getIntent().getStringExtra("grpkey");
        total=Float.valueOf(getActivity().getIntent().getStringExtra("total"));
        date=getActivity().getIntent().getStringExtra("date");
        description=getActivity().getIntent().getStringExtra("dec");

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
                            }
        });*/
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
        //amount_per_person.setText(String.valueOf(total/useridlist.size()));
        //numberofperson=useridlist.size();
        //amount_per_person.setText(String.valueOf(total/numberofperson));
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
            adapter = new percent_adapter(group_member_List,getContext());
            //adapt=new lv_split_adapter(getContext(),R.layout.custom_lv_percentage,group_member_List);
            lv_group_member.setAdapter(adapter);
        }
        /*group_member_List = new ArrayList<String>();
       /* for(DataSnapshot ds: dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildren()){
            groupList.add(ds.getValue(UserUI.class));
        }*/

        /*group_member_List.add("Group1");
        group_member_List.add("Group2");
        lv_split_adapter adapter = new lv_split_adapter(getContext(), R.layout.custom_lv_percentage, group_member_List);
        lv_group_member.setAdapter(adapter);
        */
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
    private class percent_adapter extends BaseAdapter{
        public ArrayList<UserUID> userlist;
        private Context context;

        public percent_adapter(ArrayList<UserUID> userlist, Context context) {
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
            final ViewHolder holder;
            final View row;
            view = null;
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.custom_lv_percentage, viewGroup, false);
                holder = new ViewHolder();
                holder.tv_name = row.findViewById(R.id.tv_frnd_name);
                holder.profile_pic = row.findViewById(R.id.iv_frnd_profile_pic);
                holder.percentage = row.findViewById(R.id.et_percent);
                holder.percentage.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                row.setTag(holder);
            } else {
                row=view;
                holder=(ViewHolder)row.getTag();
            }
            UserUID temp = userlist.get(i);
            holder.tv_name.setText(temp.getName());
            Glide.with(getContext()).load(Uri.parse(temp.getImageUri())).into(holder.profile_pic);
            holder.percentage.setText(String.valueOf(userlist.get(i).getAmount()));
            //holder.percentage.setText(String.valueOf(0));
            holder.percentage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!charSequence.toString().isEmpty()) {
                        leftpercent = leftpercent + Float.valueOf(charSequence.toString());
                        left_percent.setText(String.valueOf(leftpercent));
                        //used_percent.setText(String.valueOf(100-leftpercent));
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!String.valueOf(editable).isEmpty()) {
                        leftpercent = leftpercent - Float.valueOf(String.valueOf(editable));
                        left_percent.setText(String.valueOf(leftpercent));
                        used_percent.setText(String.valueOf(100-leftpercent));
                        userlist.get(i).setAmount(Float.valueOf(editable.toString()));
                        if(leftpercent < 0)
                        {
                            left_percent.setTextColor(Color.RED);
                            txt_left.setTextColor(Color.RED);
                        }
                        else
                        {
                            left_percent.setTextColor(Color.BLACK);
                            txt_left.setTextColor(Color.BLACK);
                        }
                    }
                }
            });

            return row;
        }
    }
    class ViewHolder{
        ImageView profile_pic;
        TextView tv_name;
        EditText percentage;
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
                //new CustomToast().Show_Toast(getContext(),view,"PERCENTAGE");
                addTransaction();
                return true;

            default:
                break;
        }

        return false;
    }

    private void addTransaction() {
        used_percent.setText(String.valueOf(0));
        left_percent.setText(String.valueOf(100));
        if(leftpercent==0){
            leftpercent=100;
            for(int i=0;i<adapter.getCount();i++){
                final UserUID temp=adapter.getItem(i);
                if(!temp.getId().equals(myID)){
                    Float temp_percent=temp.getAmount();
                    temp_percent=temp_percent*total/100;
                    temp.setAmount(temp_percent);
                    mDatabase.child("useruilist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            float frndamount=dataSnapshot.child(myID).child(temp.getId()).child("amount").getValue(float.class);
                            frndamount=frndamount+temp.getAmount();
                            mDatabase.child("useruilist").child(myID).child(temp.getId()).child("amount").setValue(frndamount);
                            if(dataSnapshot.hasChild(temp.getId())){
                                if(dataSnapshot.child(temp.getId()).hasChild(myID)){
                                    Float amount=dataSnapshot.child(temp.getId()).child(myID).child("amount").getValue(float.class);
                                    amount=amount-temp.getAmount();
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(amount);
                                }else{
                                    //myUI.setAmount(0);
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-temp.getAmount());
                                }
                            }else{
                                mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-temp.getAmount());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    float myshare=temp.getAmount();
                    temp.setAmount(myshare*total/100);
                }
            }
            transaction=new Transaction(description,total,date,myID);
            transaction.setGrpkey(grpkey);
            transaction.setGrpname(grpname);
            for(int j=0;j<adapter.getCount();j++){
                if(adapter.getItem(j).getAmount()!=0){
                    transaction.userlist.put(adapter.getItem(j).getId(),adapter.getItem(j));
                }
            }
            mDatabase.child("GroupTransaction").child(grpkey).push().setValue(transaction);
            new CustomToast_true().Show_Toast(getContext(),view,"Transaction Added Successfully");
            Intent intent=new Intent(getContext(),HomeActivity.class);
            startActivity(intent);
        }else{
            new CustomToast().Show_Toast(getContext(),view,"Total percentage should be 100");
        }
    }
}
