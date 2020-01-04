package com.example.eduguide;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;


public class HomeActivity extends AppCompatActivity implements DialogInterface.OnDismissListener,OTPViewInterface{
    Toolbar toolbar;
    ActionBar actionBar;
    Button submit;
    Integer usertype;
    CoordinatorLayout cv;
    String verificationID, code;
    // User Id to be put in the shared prefrences
    String userId;
    ProgressBar pb;
    Boolean verificationstate;

    public OTPView dialog;
    public FragmentTransaction ft;


    final private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;
            pb.setVisibility(View.GONE);

            dialog = new OTPView();
            ft = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("verificationID",s);
            bundle.putInt("task",1);
            bundle.putInt("usertype",usertype);
            bundle.putString("userId",userId);
            dialog.setArguments(bundle);
            dialog.show(ft, "Enter the OTP");





        }
    };


    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SharedPreferences sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId",userId);
                            editor.putInt("usertype",usertype);
                            editor.apply();

                            if(usertype==1){
                                Intent i = new Intent(HomeActivity.this, AdminHome.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Intent i = new Intent(HomeActivity.this, StudentHome.class);
                                startActivity(i);
                                finish();
                            }
                            // Sign in success, update UI with the signed-in user's information


                            // ...
                        }
                    }
                });
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking Previous Users

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            SharedPreferences sharedPreferences = getSharedPreferences("MyRef",Context.MODE_PRIVATE);
            int ut = sharedPreferences.getInt("usertype",0);
            Intent i ;
            if(ut==1){
                i = new Intent(HomeActivity.this,AdminHome.class);
                startActivity(i);
                finish();
            }
            else if (ut==2){
                i = new Intent(HomeActivity.this,StudentHome.class);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
            }

        }


        //Setting up action bar

        submit = findViewById(R.id.login_submit);
        toolbar = findViewById(R.id.home_toolbar);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        //OTP Dialog

        TextInputEditText username, password;


        username = findViewById(R.id.login_phone);
        password = findViewById(R.id.login_password);

        pb = findViewById(R.id.home_pb);
        pb.setVisibility(View.GONE);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);






                String username_string, pass_string;
                username_string = username.getText().toString();
                pass_string = password.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Phone

                //Searching in admin


                //Searching students


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("phonenumbers").child(username_string).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            usertype = dataSnapshot.getValue(Integer.class);
                            checkPasswordAndLogin(pass_string ,username_string);

                        }
                        else{
                            Toast.makeText(HomeActivity.this, "User Doesnot Exists.", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });


        // Forgot Password

        TextView forgot = findViewById(R.id.login_forgot_pass);
        forgot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ForgotPassword forgotPassword = new ForgotPassword();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                forgotPassword.show(ft,"Forgot Password");
                return false;
            }
        });


        // Password seeing

        ImageButton see = findViewById(R.id.login_password_see);
        see.setVisibility(View.GONE);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals("")){
                    see.setVisibility(View.VISIBLE);
                }
                else{
                    see.setVisibility(View.GONE);
                }

            }
        });

        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password.getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT))
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    see.setImageResource(R.drawable.visibility_off_icon);
                }
                else{
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    see.setImageResource(R.drawable.visibility_on_icon);
                }

            }
        });



        //SignUp

        Button signup = findViewById(R.id.home_signup_student);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SignupStudents.class);
                startActivity(i);
            }
        });

        Button signup_admin = findViewById(R.id.home_signup_admin);
        signup_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,SignupAdmin.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
    }


    @Override
    public void phoneVerificationState(Boolean state) {
        verificationstate  = state;
        if(state){
            SharedPreferences sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userId",userId);
            editor.putInt("usertype",usertype);
            editor.putInt("usertype",usertype);
            editor.apply();

            if(usertype==1){
                Intent i = new Intent(HomeActivity.this, AdminHome.class);
                startActivity(i);
                finish();
            }
            else{
                Intent i = new Intent(HomeActivity.this, StudentHome.class);
                startActivity(i);
                finish();
            }
        }

    }

    private void checkPasswordAndLogin(String password,String phone){

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        if(usertype.equals(1)){
            Query q = db.collection("admins").whereEqualTo("phone",phone);
            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().getDocuments().get(0).get("password").equals(password)){
                        String phonenum = "+91" + phone;
                        userId = task.getResult().getDocuments().get(0).getId();
                        Toast.makeText(getApplicationContext(),"Sending OTP",Toast.LENGTH_SHORT).show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum, 60, TimeUnit.SECONDS, HomeActivity.this, mCallBacks);
                    }
                    else{
                        Toast.makeText(HomeActivity.this, "Password Entered is Wrong", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                }
            });
        }
        else{
            Query q = db.collection("students").whereEqualTo("phone",phone);
            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().getDocuments().get(0).get("password").equals(password)){
                        String phonenum = "+91" + phone;
                        userId = task.getResult().getDocuments().get(0).getId();
                        Toast.makeText(getApplicationContext(),"Sending OTP",Toast.LENGTH_SHORT).show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum, 60, TimeUnit.SECONDS, HomeActivity.this, mCallBacks);
                    }
                    else{
                        Toast.makeText(HomeActivity.this, "Password Entered is Wrong", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                }
            });

        }

    }
}