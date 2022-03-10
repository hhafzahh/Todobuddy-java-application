package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText mobileNumber;
    EditText userName;
    EditText password;
    String userID;
    // initalise firebaseFirestore
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // get firestore instance.
        fStore = FirebaseFirestore.getInstance();
        firstName = (EditText) findViewById(R.id.editText1);
        lastName = (EditText) findViewById(R.id.editTextTextPersonName3);
        email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        mobileNumber = (EditText) findViewById(R.id.editTextNumber);
        userName = (EditText) findViewById(R.id.editTextTextPersonName5);
        password = (EditText) findViewById(R.id.editTextTextPassword2);
    }

    public void clickSignup1(View v) {
        //create 2 variables to hold email and password
        String userFirst = firstName.getText().toString();
        String userLast = lastName.getText().toString();
        String userEmail = email.getText().toString();
        String userMobile = mobileNumber.getText().toString();
        String username = userName.getText().toString();
        String userPassword = password.getText().toString();
        if(userFirst.length() != 0 && userLast.length() != 0 && userEmail.length() !=0 && userMobile.length() !=0 && username.length() != 0  && userPassword.length() !=0){
            if(userPassword.length() < 4){
                password.setError("Password must be > 4 chars");
                Toast.makeText(getApplicationContext(),"Password must be more than 4 characters",Toast.LENGTH_SHORT).show();

            }
            else{
                // create user with email and password in firebase
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success,  show toast
                                    Toast.makeText(getApplicationContext(), "SignUp Successful", Toast.LENGTH_LONG).show();
                                    // get id from currentUser
                                    userID = mAuth.getCurrentUser().getUid();
                                    //using document reference to locate in the firestore , in the collection 'users' , document(user's id)
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    //userDocIDs.add(documentReference.getId());

                                    Map<String, Object> user = new HashMap<>();
                                    // using hash map to use the key value pair
                                    // the one in "" must corresspond with firestore!
                                    user.put("firstName", userFirst);
                                    user.put("lastName", userLast);
                                    user.put("email", userEmail);
                                    user.put("mobileNumber", userMobile);
                                    user.put("username", username);
                                    user.put("password", userPassword);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // if sign in successful , show toast !
                                            Toast.makeText(getApplicationContext(), "User  created for" + userID, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // if not , show not successfull!
                                            Toast.makeText(getApplicationContext(), "Created failed" + e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent myIntent = new Intent(getApplicationContext(),LoginActivity.class);

                                    startActivity(myIntent);
                                }
                                else {
                                    // If sign in fails, display a failed message to the user.
                                    Toast.makeText(getApplicationContext(), "SignUp failed", Toast.LENGTH_LONG).show();
                                }

                            }

                        });
            }
        }
        else{
            // if the fields are not filled! show toast message!
            Toast.makeText(getApplicationContext(),"Fill in all fields of the form! Sign Up unsuccessful",Toast.LENGTH_SHORT).show();
        }

    }
}