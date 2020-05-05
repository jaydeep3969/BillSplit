package com.demo.nikunj.splitwise;

import android.content.Context;
import android.content.Intent;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tab_adjustment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_adjustment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_adjustment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_adjustment() {
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
    public static tab_adjustment newInstance(String param1, String param2) {
        tab_adjustment fragment = new tab_adjustment();
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
    private float tmp_total;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<UserUID> group_member_List ;
    private ListView lv_group_member;
    private String grpkey="";
    private String grpname="";
    private float total=0;
    private String description;
    private String date;
    private List<String> useridlist=new ArrayList<>();
    private UserUI myUI;
    private Map<String,UserUI> useruilist=new HashMap<>();
    private adjustment_adapter adapter;
    private String myID;
    private Transaction transaction;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view =  inflater.inflate(R.layout.fragment_tab_adjustment, container, false);
        lv_group_member = (ListView) view.findViewById(R.id.lv_group_member_adjustment);
        //save=(Button)getActivity().findViewById(R.id.btn_save);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u=mAuth.getCurrentUser();

        grpkey=getActivity().getIntent().getStringExtra("grpkey");
        total=Float.valueOf(getActivity().getIntent().getStringExtra("total"));
        description=getActivity().getIntent().getStringExtra("dec");
        date=getActivity().getIntent().getStringExtra("date");

        if(u!=null){
            myID=u.getUid();
        }

        grpkey=getActivity().getIntent().getStringExtra("grpkey");
        total=Float.valueOf(getActivity().getIntent().getStringExtra("total"));

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
       /* save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomToast().Show_Toast(getContext(),view,"EQUALLY");
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
            adapter = new adjustment_adapter(group_member_List,getContext());
            //adapt=new lv_split_adapter(getContext(),R.layout.custom_lv_percentage,group_member_List);
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
    private class adjustment_adapter extends BaseAdapter{

        public ArrayList<UserUID> userlist;
        public ArrayList<Float> adjust,individual;
        private Context context;

        public adjustment_adapter(ArrayList<UserUID> userlist, Context context) {
            this.userlist = userlist;
            this.context = context;
            adjust = new ArrayList<Float>(Collections.nCopies(userlist.size(),0.0f));
            individual = new ArrayList<Float>(Collections.nCopies(userlist.size(),total/userlist.size()));
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
                row = layoutInflater.inflate(R.layout.custom_lv_adjustment, viewGroup, false);
                holder = new ViewHolder();
                holder.tv_name = row.findViewById(R.id.tv_frnd_name);
                holder.profile_pic = row.findViewById(R.id.iv_frnd_profile_pic);
                holder.adjustment = row.findViewById(R.id.et_adjustment);
                holder.adjustment.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                holder.equalamount=row.findViewById(R.id.tv_individual_share);
                row.setTag(holder);
            } else {
                row=view;
                holder=(ViewHolder)row.getTag();
            }

            //holder.equalamount.setText(String.valueOf(total/adapter.getCount()));
            UserUID temp = userlist.get(i);
            holder.tv_name.setText(temp.getName());
            Glide.with(getContext()).load(Uri.parse(temp.getImageUri())).into(holder.profile_pic);
            holder.adjustment.setText(String.valueOf(adjust.get(i)));
            holder.equalamount.setText(String.valueOf(individual.get(i)));
            //holder.percentage.setText(String.valueOf(0));
            holder.adjustment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!charSequence.toString().isEmpty()){
                        holder.equalamount.setText(String.valueOf((total/adapter.getCount())-Float.valueOf(charSequence.toString())));
                        tmp_total -= Float.valueOf(charSequence.toString());
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().isEmpty()){
                        //holder.equalamount.setText(String.valueOf((total/adapter.getCount())+Float.valueOf(editable.toString())));
                        tmp_total += Float.valueOf(editable.toString());
                        if(tmp_total>total) {
                            new CustomToast().Show_Toast(getContext(), row, "Adjustment amount is more than Total");
                        }
                    }
                    for(int j=0;j<adapter.getCount();j++) {
                        View itemview = lv_group_member.getChildAt(j);
                            EditText tmp_adjustment = (EditText) itemview.findViewById(R.id.et_adjustment);
                            TextView tmp_individual = (TextView) itemview.findViewById(R.id.tv_individual_share);
                            float tmp = 0;
                            if (!tmp_adjustment.getText().toString().isEmpty()) {
                                tmp = Float.valueOf(tmp_adjustment.getText().toString());
                            }

                            adjust.set(j, tmp);
                            individual.set(j, ((total - tmp_total) / (float) adapter.getCount()) + tmp);
                            tmp_individual.setText(String.valueOf(((total - tmp_total) / (float) adapter.getCount()) + tmp));
                            userlist.get(j).setAmount((total - tmp_total) / (float) adapter.getCount() + tmp);

                    }
                }
            });

            return row;
        }
    }
    class ViewHolder{
        ImageView profile_pic;
        TextView tv_name;
        EditText adjustment;
        TextView equalamount;
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
                //new CustomToast().Show_Toast(getContext(),view,"ADJUSTMENT");
                addTransaction();
                return true;

            default:
                break;
        }

        return false;
    }

    private void addTransaction() {
        if(tmp_total<=total){
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
            new CustomToast().Show_Toast(getContext(),view,"Adjustment amount is more than Total");
        }
    }
}
