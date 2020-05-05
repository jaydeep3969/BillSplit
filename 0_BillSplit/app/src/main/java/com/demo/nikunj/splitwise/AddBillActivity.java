package com.demo.nikunj.splitwise;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddBillActivity extends AppCompatActivity {

    private AutoCompleteTextView search;
    private EditText description,total;
    private ImageButton back;
    private Button split;
    private ArrayList<String> suggestions;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser loginuser;
    private String grpkey="";
    private TextView date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int year,month,day;
    private Calendar cal;
    private String []months={"January","February","March","April","May","June","July","August","September","Octomber","November","December"};
    Map<String,String> groupidlist=new HashMap<>();
    List<String> groupnamelist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        loginuser=mAuth.getCurrentUser();

        suggestions = new ArrayList<String>();

        split = (Button) findViewById(R.id.btn_split);
        back = (ImageButton) findViewById(R.id.btn_back);
        search = (AutoCompleteTextView)findViewById(R.id.actv_search);
        description=(EditText)findViewById(R.id.et_description);
        total = (EditText)findViewById(R.id.et_amount);
        date=(TextView) findViewById(R.id.date);


        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);
        month=cal.get(Calendar.MONTH);
        day=cal.get(Calendar.DAY_OF_MONTH);

        date.setText(new StringBuilder().append(day).append(" ").append(months[month])
        .append(" ").append(year));

        setSuggestions();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grpkey=groupidlist.get(search.getText().toString());
                //new CustomToast().Show_Toast(AddBillActivity.this,view,grpkey);
                if(!(description.getText().toString().isEmpty() && total.getText().toString().isEmpty())) {
                    Intent i = new Intent(AddBillActivity.this, SplitActivity.class);
                    i.putExtra("grpkey",grpkey);
                    i.putExtra("total",total.getText().toString());
                    i.putExtra("dec",description.getText().toString());
                    i.putExtra("date",date.getText().toString());
                    startActivity(i);
                }else{
                    new CustomToast().Show_Toast(AddBillActivity.this,view,"All field are Mandatory");
                }
            }
        });

        if(loginuser!=null){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    creategrouplist(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //new CustomToast().Show_Toast(AddBillActivity.this,view,grpkey);
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String str=search.getText().toString();
                    if(groupnamelist.contains(str)){
                        grpkey=groupidlist.get(str);
                        return;
                    }else {
                        new CustomToast().Show_Toast(AddBillActivity.this, view, "Invalid Group Name");
                        search.setText("");
                        grpkey=null;
                    }
                }
            }
        });
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
    }

    private void creategrouplist(DataSnapshot dataSnapshot) {
        if(dataSnapshot.child("groupuilist").hasChild(loginuser.getUid())) {
            for (DataSnapshot ds : dataSnapshot.child("groupuilist").child(loginuser.getUid()).getChildren()) {
                groupidlist.put(ds.getValue(String.class),ds.getKey());
                groupnamelist.add(ds.getValue(String.class));
            }
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddBillActivity.this,android.R.layout.select_dialog_item,groupnamelist);
        search.setAdapter(adapter);
        search.setThreshold(1);
    }

    public  void setSuggestions()
    {
        //suggestions.add("Group1");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,suggestions);

        search.setAdapter(adapter);
    }
    private void ShowDateDialog() {
        DatePickerDialog dialog=new DatePickerDialog(this,android.R.style.Theme_Material_Dialog,mDateSetListener,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        dialog.show();
    }
}
