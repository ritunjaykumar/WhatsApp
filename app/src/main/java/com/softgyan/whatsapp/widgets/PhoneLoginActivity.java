package com.softgyan.whatsapp.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityPhoneLoginBinding;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private static final String TAG = "my_tag";
//    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
//    private boolean mVerificationInProgress = false;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ActivityPhoneLoginBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private FirebaseFirestore mFirestore;
    private FirebaseUser authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        authUser = mAuth.getCurrentUser();

        if (authUser != null) {
            startActivity(new Intent(getApplicationContext(), SetUserInfoActivity.class));
            finish();
        }

        binding.btnNext.setOnClickListener(v -> {
            String phoneNumber = "+91" + binding.etPhoneNumber.getText().toString();

            if (verifyPhoneNumber()) {
                if (binding.btnNext.getText().equals(getString(R.string.next))) {
                    disableViews(binding.btnNext, binding.etPhoneNumber);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    startPhoneNumberVerification(phoneNumber);

                } else if (binding.btnNext.getText().equals(getString(R.string.verify))) {

                    String otp = binding.etOtp.getText().toString();
                    if (verifyOTP()) {
                        disableViews(binding.btnNext, binding.etOtp);
                        binding.progressBar.setVisibility(View.VISIBLE);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(PhoneLoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(PhoneLoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                binding.btnNext.setText(getString(R.string.next));
            }
        });

        binding.tvResend.setOnClickListener(v -> {
            String phoneNumber = "+91" + binding.etPhoneNumber.getText().toString();
            binding.progressBar.setVisibility(View.VISIBLE);
            startPhoneNumberVerification(phoneNumber);
            binding.tvResend.setVisibility(View.GONE);
            resendVerificationCode(phoneNumber, mResendToken);
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //self mobile verification
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Log.d(TAG, "onVerificationCompleted: complete");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "Something error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
                enableViews(binding.btnNext, binding.etPhoneNumber);
                binding.btnNext.setText(getString(R.string.next));
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;
                binding.btnNext.setText(getString(R.string.verify));
                binding.etOtp.setVisibility(View.VISIBLE);
                Log.d(TAG, "onCodeSent: " + s);
                binding.progressBar.setVisibility(View.GONE);
                enableViews(binding.btnNext);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                binding.tvResend.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "signWithCredential : success");
                        Intent intent = new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PhoneLoginActivity.this, "Something error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "signInWithPhoneAuthCredential: " + task.getException().getMessage());
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private boolean verifyPhoneNumber() {
        String phoneNumber = binding.etPhoneNumber.getText().toString();
        return !TextUtils.isEmpty(phoneNumber);
    }

    private boolean verifyOTP() {
        String otp = binding.etOtp.getText().toString();
        return !TextUtils.isEmpty(otp);
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }
}




//                            if (authUser != null) {
//                                String userId = authUser.getUid();
//                                Users users = new Users(userId, "", authUser.getPhoneNumber(),
//                                        "", "", "", "",
//                                        "", "", "");
//                                mFirestore.collection("Users")
//                                        .document("UserInfo")
//                                        .collection(userId)
//                                        .add(users)
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                            @Override
//                                            public void onSuccess(DocumentReference documentReference) {
//                                                Intent intent = new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            } else {
//                                Toast.makeText(PhoneLoginActivity.this, "something error", Toast.LENGTH_SHORT).show();
//                            }