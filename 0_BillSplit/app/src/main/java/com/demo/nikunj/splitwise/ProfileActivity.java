package com.demo.nikunj.splitwise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.http.Url;
/**
 * Created by OM on 3/11/2018.
 */

public class ProfileActivity extends AppCompatActivity {

    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    private Button upload;
    private ImageButton back;
    private EditText et_name,et_mail,et_mobile;
    private TextView tv_name ;
    private AlertDialog dialog;
    private EditText old_pswd;
    private EditText new_pswd;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView profile_pic;
    private Uri selectedImageUri;
    private Uri photourl;

    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

       mDatabase= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        back = (ImageButton) findViewById(R.id.btn_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_name = (EditText) findViewById(R.id.et_full_name);
        et_mail = (EditText) findViewById(R.id.et_mail);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        profile_pic = (ImageView) findViewById(R.id.iv_profile_pic);
        upload=(Button)findViewById(R.id.uploadimage);



        profile_pic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editProfilePic();
               // Toast.makeText(ProfileActivity.this, "You clicked on ImageView", Toast.LENGTH_LONG).show();

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImageUri != null)
                {
                    Bitmap bmp=((RoundedBitmapDrawable) profile_pic.getDrawable()).getBitmap();

                    if(bmp != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        byte[] data = baos.toByteArray();
                        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();

                        StorageReference ref = storageReference.child("images/" + mAuth.getCurrentUser().getUid().toString());
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        photourl = taskSnapshot.getDownloadUrl();
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        savephoto(photourl);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                .getTotalByteCount());
                                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                    }
                                });
                    }
                    else
                    {
                        new CustomToast().Show_Toast(ProfileActivity.this,view,"filepath not found");
                    }
                }else{
                    new CustomToast().Show_Toast(ProfileActivity.this,view,"filepath not found");
                }

            }
        });

       mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ds = (DataSnapshot) dataSnapshot.child(uid);
                    tv_name.setText(ds.getValue(User.class).getFullname());
                    et_name.setText(ds.getValue(User.class).getFullname());
                    et_mail.setText(ds.getValue(User.class).getEmail());
                    et_mobile.setText(ds.getValue(User.class).getNumber());

               // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),);
               // profile_pic.setImageDrawable(createRoundedBitmapDrawableWithBorder(bitmap));

                Glide.with(getBaseContext()).load(Uri.parse(ds.getValue(User.class).getImageUri())).into(profile_pic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void savephoto(Uri photourl) {
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("imageUri").setValue(photourl.toString());
        mDatabase.child("useruilist").child(mAuth.getCurrentUser().getUid()).child(mAuth.getCurrentUser().getUid()).child("imageUri").setValue(photourl.toString());
    }

    public void editProfilePic()
    {
      //  final CharSequence[] items={"Camera","Gallery", "Cancel"};

        final CharSequence[] items={"Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Set New Profile Picture");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               /* if (items[i].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else*/ if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bmp);
                roundedBitmapDrawable.setCircular(true);
                profile_pic.setImageDrawable(roundedBitmapDrawable);
            }else if(requestCode==SELECT_FILE){

                selectedImageUri = data.getData();
                //crop image
                try{
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(selectedImageUri,"image/*");
                    cropIntent.putExtra("crop","true");
                    cropIntent.putExtra("outputX",180);
                    cropIntent.putExtra("outputY",180);
                    cropIntent.putExtra("aspectX",5);
                    cropIntent.putExtra("aspectY",5);
                    cropIntent.putExtra("scaleUpIfNedded",false);
                    cropIntent.putExtra("return-data",true);

                    startActivityForResult(cropIntent,1);
                }
                catch (ActivityNotFoundException ex)
                {

                }

                Glide.with(getBaseContext()).load(selectedImageUri).into(profile_pic);
            }
        }
    }

}
