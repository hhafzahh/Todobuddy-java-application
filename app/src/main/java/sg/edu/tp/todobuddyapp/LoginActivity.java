package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //create and initalise firebase firebaseAuth and firebaseUser
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    EditText username;
    EditText password;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editTextTextPassword);

    }
    public void clickForgot(View v){
        // if user clicks forgot password link , user directs to forgot password page
        Intent forgotActivity = new Intent(this,ForgotPasswordActivity.class);
        startActivity(forgotActivity);
    }
    public void clickLogin1(View v) {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        // if both fields are *NOT* empty , then authenticate email and password with firebase authentication
        if (userEmail.length() != 0 && userPassword.length()!= 0) {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //to check iff task is successful or not -> use if conditional.
                            if (task.isSuccessful()) {
                                // Login success
                                Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                             //   String str = userEmail.toString();
                                // Bring user to success activvity by using Intent
                                Intent homeActivity = new Intent(getApplicationContext(), MainActivity2.class);
                                //homeActivity.putExtra("message_key",str);
                               //homeActivity.putExtra("ID", currentUser);
                                startActivity(homeActivity);

                            } else {
                                // If login in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            // if both field are EMPTY , then display the toast!
            Toast.makeText(this, "ERROR: Username and Password cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}