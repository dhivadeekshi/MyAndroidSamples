/*package com.dhivakar.mysamples.googleplaygames;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GPGSLogin extends BaseAppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpgslogin);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId())
        {
            default:
                break;
            case R.id.buttonLoginGpgs: Login(); break;
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // User not found login user
            LogUtils.i(this, "UserLoggedIn : " + currentUser.getDisplayName());
        }
    }

    private void Login()
    {
        TextView email = (TextView) findViewById(R.id.textEmailAddress);
        TextView password = (TextView) findViewById(R.id.textPassword);

        LogUtils.i(this, "Login using email:"+email.getText().toString()+" password:"+password.getText().toString());

        Login(email.getText().toString(), password.getText().toString());
    }

    private void Login(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LogUtils.d(GPGSLogin.this, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            LogUtils.w(GPGSLogin.this.getClass().getSimpleName(), "createUserWithEmail:failure", task.getException());
                            Toast.makeText(GPGSLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}*/
