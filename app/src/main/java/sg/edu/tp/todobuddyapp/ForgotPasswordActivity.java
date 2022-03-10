package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// For user to provide email to reset password
public class ForgotPasswordActivity extends AppCompatActivity {
    //getting the shared instance of the FirebaseAuth Object and initalising it!
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = (EditText) findViewById(R.id.editTextTextPersonName2);
    }

    public void clickCancel(View v) {
        // go to previous page
        onBackPressed();
    }

    public void clickSend(View v) {
        // this method sends the password reset link to the email inputted by user!
        String userEmail = email.getText().toString();
        if (userEmail.length() != 0) {
            mAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Error sending to email", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


        } else {
            Toast.makeText(this, "ERROR: Email  cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}