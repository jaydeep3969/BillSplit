package com.demo.nikunj.splitwise;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * {@link tab_equally.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_equally#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_equally extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_equally() {
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
    public static tab_equally newInstance(String param1, String param2) {
        tab_equally fragment = new tab_equally();
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
    private Button btn_save;
    private ArrayList<UserUID> group_member_List ;
    private ListView lv_group_member;
    private TextView amount_per_person;
    private String grpkey="";
    private String grpname="";
    private String description;
    private String date;
    private List<String> useridlist=new ArrayList<>();
    private Map<String,UserUI> useruilist=new HashMap<>();
    private lv_split_adapter adapter;
    private float total=0;
    private UserUI myUI;
    private String myID;
    int numberofperson;
    private Transaction transaction;
    View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_tab_equally, container, false);
        lv_group_member = (ListView) view.findViewById(R.id.lv_group_member_equally);
        amount_per_person=(TextView)view.findViewById(R.id.tv_amount);
       // btn_save=(Button)getActivity().findViewById(R.id.btn_save);
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

        lv_group_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox cb=(CheckBox)view.findViewById(R.id.checkBox);
                cb.setChecked(!cb.isChecked());
                if(cb.isChecked()){
                    //cb.getTag();
                    numberofperson=numberofperson+1;
                }else{
                    numberofperson=numberofperson-1;
                }
                if(numberofperson!=0) {
                    amount_per_person.setText(String.valueOf(total / numberofperson));
                }else {
                    amount_per_person.setText(String.valueOf(0));
                }
            }
        });
       /* btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String myID=mAuth.getCurrentUser().getUid();
                            }
        });*/
        return view;
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public  void  setListView(DataSnapshot dataSnapshot)
    {
        group_member_List = new ArrayList<UserUID>();
        for(DataSnapshot ds:dataSnapshot.child("groups").child(grpkey).child("users").getChildren()){
            if(!useridlist.contains(ds.getKey())) {
                useridlist.add(ds.getKey());
            }
        }
        amount_per_person.setText(String.valueOf(total/useridlist.size()));
        numberofperson=useridlist.size();
        amount_per_person.setText(String.valueOf(total/numberofperson));
        DataSnapshot useruidata=dataSnapshot.child("useruilist").child(mAuth.getCurrentUser().getUid());
        for(String key:useridlist){
            //3/20/2018
            if(useruidata.hasChild(key)) {
                UserUID userUID = new UserUID(key);
                UserUI temp = useruidata.child(key).getValue(UserUI.class);
                userUID.setAmount(temp.getAmount());
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
            adapter = new lv_split_adapter(getContext(), R.layout.custom_lv_equally, group_member_List);
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
                //new CustomToast().Show_Toast(getContext(),view,"EQUALLY");
                addTransaction();
                return true;

            default:
                break;
        }

        return false;
    }

    private void addTransaction() {
        if (Float.valueOf(amount_per_person.getText().toString()) != 0) {
            for (int i = 0; i < adapter.getCount(); i++) {
                View itemview = lv_group_member.getChildAt(i);
                CheckBox cb = itemview.findViewById(R.id.checkBox);
                final float splitamount = Float.valueOf(amount_per_person.getText().toString());
                final UserUID temp = adapter.getItem(i);
                if (cb.isChecked()) {
                    temp.setAmount(splitamount);
                    if (!temp.getId().equals(myID)) {
                        mDatabase.child("useruilist").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                float frndamount = dataSnapshot.child(myID).child(temp.getId()).child("amount").getValue(float.class);
                                frndamount = frndamount + splitamount;
                                mDatabase.child("useruilist").child(myID).child(temp.getId()).child("amount").setValue(frndamount);
                                if (dataSnapshot.hasChild(temp.getId())) {
                                    if (dataSnapshot.child(temp.getId()).hasChild(myID)) {
                                        Float amount = dataSnapshot.child(temp.getId()).child(myID).child("amount").getValue(float.class);
                                        amount = amount - splitamount;
                                        mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(amount);
                                    } else {
                                        mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                        mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-splitamount);
                                    }
                                } else {
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).setValue(myUI);
                                    mDatabase.child("useruilist").child(temp.getId()).child(myID).child("amount").setValue(-splitamount);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    temp.setAmount(0);
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
            //mDatabase.child("Transactions").push().setValue(transaction);
            mDatabase.child("GroupTransaction").child(grpkey).push().setValue(transaction);
            /*String key=mDatabase.push().getKey();
            mDatabase.child("Transaction").child(key).setValue(transaction);
            mDatabase.child("UserTransactions").child(mAuth.getCurrentUser().getUid()).push().setValue(key);
            mDatabase.child("UserTransactions").child("lFybvNwsUvaYlQuo3e6kH69XVOS2").push().setValue(key);*/
            new CustomToast_true().Show_Toast(getContext(),view,"Transaction Added Successfully");
            Intent intent=new Intent(getContext(),HomeActivity.class);
            startActivity(intent);
        }else {
            new CustomToast().Show_Toast(getContext(),view,"You Must Select at least one person!! ");
        }
    }
}
