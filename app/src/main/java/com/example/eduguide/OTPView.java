package com.example.eduguide;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.eduguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;





public class OTPView extends DialogFragment {




    private void signInWithCredentials(PhoneAuthCredential credential){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){


                    mCallbacks.phoneVerificationState(true);
                    getDialog().dismiss();





                }
                else{
                    error.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    TextView error;
    ProgressBar pb;
    Integer t;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootview = inflater.inflate(R.layout.otp_view, container, false);
        Toolbar toolbar = rootview.findViewById(R.id.otp_view_toolbar);
        toolbar.setNavigationIcon(R.drawable.close_icon);
        toolbar.setTitle("Login SignupStudents");

        SignupStudents signupStudents = new SignupStudents();

        //Giving Refrence
        pb = rootview.findViewById(R.id.otp_view_pb);
        pb.setVisibility(View.GONE);
        error = rootview.findViewById(R.id.otp_view_error);



        //Error
        TextView error = rootview.findViewById(R.id.otp_view_error);
        error.setVisibility(View.GONE);

        OtpView otpView = rootview.findViewById(R.id.otp_view);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                pb.setVisibility(View.VISIBLE);

                Bundle bundle = getArguments();
                String verificationID = bundle.getString("verificationID");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,otp);
                signInWithCredentials(credential);
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootview;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private  OTPViewInterface mCallbacks;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallbacks = (OTPViewInterface) context;
        }
        catch (ClassCastException e){

        }
    }
}
