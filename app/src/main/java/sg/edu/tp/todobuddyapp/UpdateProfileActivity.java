package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText EditfName,EditlName,EditUName, EditEmaill,EditMobNo,EditPass;
    Button button;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FloatingActionButton floatingActionButton3;
    private String currentuid;
    private String onlineUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        documentReference = fStore.collection("user").document(onlineUserID);
        // linking xml to java file
        EditfName = findViewById(R.id.editTextTextPersonName7);
        EditlName = findViewById(R.id.editTextTextPersonName8);
        EditMobNo = findViewById(R.id.editTextTextPersonName9);
        //EditEmaill = findViewById(R.id.editEmail); --> not doing email because email cannot be changeable!!
        EditPass = findViewById(R.id.editTextTextPassword3);
        EditUName = findViewById(R.id.editTextTextPersonName6);
        floatingActionButton3 = findViewById(R.id.imageButton3);

        // if user touches the floatingbutton(save) then the updateProfile method is called!
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();

            }
        });

    }
    // if user clicks the "update password" button,by onClick: clickUpdate, this is method is done.
    public void clickUpdate(View v) {
        //get input
        String userPassword = EditPass.getText().toString();
        //if it is not empty
        if (userPassword.length() != 0) {
            //update password in firebase
            mUser.updatePassword(userPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Password change success
                                Toast.makeText(getApplicationContext(), "Password changed successfully.",
                                        Toast.LENGTH_SHORT).show();
                                //documentReference is the firesotore collectiion 'users' with document of user id
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            //if successful , set Text to userPassword
                                            EditPass.setText(userPassword);
                                        } else {
                                            // else if it is not successful , toast message to be displayed
                                            Toast.makeText(UpdateProfileActivity.this, "Password cannot be updated ", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                    }
                                });

                                 // enter the read operation first before the write operation!
                                //this is where we want to read the value from.
                                final DocumentReference sDoc = fStore.collection("users").document(onlineUserID);
                                fStore.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        // enter the read operation first before the write operation!
                                        //this is where we want to read the value from.
                                      //  final DocumentReference sDoc = fStore.collection("users").document(onlineUserID);
                                      //  DocumentSnapshot snapshot = transaction.get(sDoc);
                                        transaction.update(sDoc, "password", userPassword);
                                        return null;
                                    }


                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        Toast.makeText(UpdateProfileActivity.this, "updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Password change fails
                                Toast.makeText(getApplicationContext(), "Error changing password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "ERROR: Password cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onStart(){
        super.onStart();
        //setting of values into the edit Profile does not work ? - is it because of the EditText
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String firstResult = task.getResult().getString("firstName");
                    String lastResult = task.getResult().getString("lastName");
               //     String emailResult = task.getResult().getString("email");
                    String mobileResult = task.getResult().getString("mobileNumber");
                //    String passwordResult = task.getResult().getString("password");
                    String userNameResult = task.getResult().getString("username");

                    EditfName.setText(firstResult);
                    EditlName.setText(lastResult);
                  //  EditEmaill.setText(emailResult);
                    EditMobNo.setText(mobileResult);
                   // EditPass.setText(passwordResult);
                    EditUName.setText(userNameResult);
                }
                else{
                  //  Toast.makeText(UpdateProfileActivity.this,"No profile exists ",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateProfile()
    // for update profile() method , other than email and password , the rest will be updated
    {
        String firstName = EditfName.getText().toString().trim();
        String lastName = EditlName.getText().toString().trim();
     //   String email = EditEmaill.getText().toString().trim();
        String mobileNumber = EditMobNo.getText().toString().trim();
      //  String password = EditPass.getText().toString().trim();
        String username = EditUName.getText().toString().trim();






        //read operation = read the values before the write operation
        final DocumentReference sDoc = fStore.collection("users").document(onlineUserID);
        fStore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                // update values , by keys and the value inputted in the EditText
                transaction.update(sDoc, "firstName",firstName );
                transaction.update(sDoc, "lastName",lastName );
             //   transaction.update(sDoc, "email",email );
                transaction.update(sDoc, "mobileNumber",mobileNumber );
            //   transaction.update(sDoc, "password",password );
                transaction.update(sDoc, "username",username );
                //Success
                return null;
            }


        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(UpdateProfileActivity.this,"updated",Toast.LENGTH_SHORT).show();
                Toast.makeText(UpdateProfileActivity.this,"Password is not updated , to update , press the other button",Toast.LENGTH_LONG).show();

            }
        });

    }

}