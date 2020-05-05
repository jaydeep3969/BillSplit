package com.demo.nikunj.splitwise;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.abs;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tab_activity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tab_activity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab_activity extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tab_activity() {
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
    public static tab_activity newInstance(String param1, String param2) {
        tab_activity fragment = new tab_activity();
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
    private FirebaseUser loginuser;
    private ListView activitylist;
    private List<Transaction> transactionslist=new ArrayList<>();
    private Map<String,Transaction> tlist=new HashMap<>();
    private Map<String,String> groupnames=new HashMap<>();
    MyAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tab_activity, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        loginuser=mAuth.getCurrentUser();
        activitylist=(ListView)view.findViewById(R.id.lv_activity);
        if(loginuser!=null){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    createActivityList(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            activitylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Map.Entry<String,Transaction> t=adapter.getItem(i);
                    Intent intent=new Intent(getContext(),ActivityDetails.class);
                    intent.putExtra("Transaction",t.getValue());
                    intent.putExtra("Id",t.getKey());
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    private void createActivityList(DataSnapshot dataSnapshot) {
        List<String> grpidlist=new ArrayList<>();
        if(dataSnapshot.child("groupuilist").hasChild(loginuser.getUid())) {
            for (DataSnapshot ds : dataSnapshot.child("groupuilist").child(loginuser.getUid()).getChildren()) {
                grpidlist.add(ds.getKey());
            }
            for(String grpkey:grpidlist){
                groupnames.put(grpkey,dataSnapshot.child("groupuilist").child(loginuser.getUid()).child(grpkey).getValue(String.class));
                if(dataSnapshot.child("GroupTransaction").hasChild(grpkey)){
                    for(DataSnapshot ds:dataSnapshot.child("GroupTransaction").child(grpkey).getChildren()){
                        //transactionslist.add(ds.getValue(Transaction.class));
                        Transaction t=ds.getValue(Transaction.class);
                        transactionslist.add(t);
                        tlist.put(ds.getKey(),ds.getValue(Transaction.class));
                    }
                }
            }
        }
        List<String> transactionidlist=new ArrayList<>();
        if(dataSnapshot.child("UserTransactions").hasChild(loginuser.getUid())){
            for(DataSnapshot ds:dataSnapshot.child("UserTransactions").child(loginuser.getUid()).getChildren()){
                transactionidlist.add(ds.getValue(String.class));
            }
            for(String tid:transactionidlist){
                transactionslist.add(dataSnapshot.child("Transaction").child(tid).getValue(Transaction.class));
                tlist.put(tid,dataSnapshot.child("Transaction").child(tid).getValue(Transaction.class));
            }
        }
        //new CustomToast().Show_Toast(getContext(),getView(),String.valueOf(tlist.size()));
        if(getActivity()!=null) {
            adapter= new MyAdapter(tlist, getContext());
            activitylist.setAdapter(adapter);
        }

    }

    public class MyAdapter extends BaseAdapter {
        public final ArrayList mData;
        private Context context;

        public MyAdapter(Map<String, Transaction> map,Context context) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
            this.context=context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, Transaction> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO implement you own logic with ID
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = layoutInflater.inflate(R.layout.activity_feed, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<String, Transaction> item = getItem(position);
            TextView activitytitle=(TextView) result.findViewById(R.id.tv_activity);
            TextView yourshare=result.findViewById(R.id.tv_owe);
            final TextView groupname=result.findViewById(R.id.tv_group);
            TextView date=result.findViewById(R.id.tv_date);
            TextView chara=result.findViewById(R.id.firstchar);
            ImageView iv_activity_pic=result.findViewById(R.id.iv_activity_pic);
            final Transaction t=item.getValue();
            if(t!=null) {
                chara.setText(t.getDescription().substring(0, 1).toUpperCase());
                Map<String, UserUID> userlist = new HashMap<String, UserUID>();
                userlist = t.getUserlist();
                //rushab
                //iv_activity_pic.setColorFilter(getRandomMaterialColor("400"));
                iv_activity_pic.setBackgroundColor(getRandomMaterialColor("400"));


                if (!t.getGrpname().equals("")) {
                    groupname.setVisibility(View.VISIBLE);
                    groupname.setText(" in " + t.getGrpname());
                }
                if (t.getId().equals(mAuth.getCurrentUser().getUid())) {
                    activitytitle.setText(Html.fromHtml("<b>You</b> Added " + "<b>\"" + t.getDescription() + "\"</b>"));
                    float amount = 0;
                    for (Map.Entry<String, UserUID> u : userlist.entrySet()) {
                        if (!u.getValue().getId().equals(mAuth.getCurrentUser().getUid())) {
                            amount += u.getValue().getAmount();
                        }
                    }
                    if (amount > 0) {
                        yourshare.setText("You Get Back ₹" + amount);
                        yourshare.setTextColor(Color.parseColor("#32CD32"));
                    } else {
                        yourshare.setText("You Owe ₹" + abs(amount));
                        yourshare.setTextColor(Color.RED);
                    }
                } else {
                    String doerid = t.getId();
                    activitytitle.setText(Html.fromHtml("<b>" + userlist.get(doerid).getName() + "</b>" + " Added " + "<b>\"" + t.getDescription() + "\"</b>"));
                    if (userlist.containsKey(mAuth.getCurrentUser().getUid())) {
                        float amount = userlist.get(mAuth.getCurrentUser().getUid()).getAmount();
                        if (amount < 0) {
                            yourshare.setText("You Get Back ₹" + abs(amount));
                            yourshare.setTextColor(Color.parseColor("#32CD32"));
                        } else {
                            yourshare.setText("You Owe ₹" + amount);
                            yourshare.setTextColor(Color.RED);
                        }
                    }
                }
                date.setText(t.getDate());
            }
                return result;
        }
        private int getRandomMaterialColor(String typeColor) {
            int returnColor = Color.GRAY;
            int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

            if (arrayId != 0) {
                TypedArray colors = getResources().obtainTypedArray(arrayId);
                int index = (int) (Math.random() * colors.length());
                returnColor = colors.getColor(index, Color.GRAY);
                colors.recycle();
            }
            return returnColor;
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
