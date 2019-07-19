package com.example.chatterbox;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    public EditText phoneNo;
    public EditText otp;
    public TextView sendotp;
    public TextView submitotp;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String mVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        phoneNo = findViewById(R.id.enter_phoneNo);
        otp = findViewById(R.id.enter_otp);
        sendotp = findViewById(R.id.send_otp);
        submitotp = findViewById(R.id.submit_otp);

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP(phoneNo.getText().toString());
            }
        });
    }


    public void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d(TAG, "onVerificationCompleted:" + credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {


                        Log.w(TAG, "onVerificationFailed", e);  // Incorrect phone no !!!!

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(MainActivity.this, "Incorrect format of phone no.", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                        }

                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        Log.d(TAG, "onCodeSent:" + verificationId);

                        sendotp.setVisibility(View.INVISIBLE);
                        phoneNo.setVisibility(View.INVISIBLE);
                        submitotp.setVisibility(View.VISIBLE);
                        otp.setVisibility(View.VISIBLE);

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        getcode();


                    }
                });

    }

    private void verifycode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    String usertypedcode;

    private void getcode() {
        submitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usertypedcode = otp.getText().toString();
                Toast.makeText(MainActivity.this, "OTP Submitted", Toast.LENGTH_SHORT).show();
                verifycode(usertypedcode);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Boolean checkNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            if (checkNewUser == true) {
                                // TODO: ..........
                                CreateFirebaseDatabase(user);
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.putExtra("PhoneNo", user.getPhoneNumber());
                                intent.putExtra("PhoneNo", user.getPhoneNumber());
                                intent.putExtra("NewUser", "true");
                                intent.putExtra("Editable", "true");
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, HomePage.class);
                                startActivity(intent);
                            }

                            Toast.makeText(MainActivity.this, "Signed in using" + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    private void CreateFirebaseDatabase(FirebaseUser user) {

        FirebaseDatabase mfirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");
        mdatabaseReference.child(user.getPhoneNumber()).child("UID").setValue(user.getUid());

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart : ");

        // Check if user is signed in (non-null)

        if(mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Toast.makeText(MainActivity.this, currentUser.getDisplayName() + " Already logged in", Toast.LENGTH_LONG).show();
            Log.d(TAG,currentUser.toString());
            Intent intent = new Intent(MainActivity.this,HomePage.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, "please sign in...", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}




