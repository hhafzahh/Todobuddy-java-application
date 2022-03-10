package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    //GOOGLE_SIGN_IN_CODE in is the request code you will assign for starting the new activity.
    // this can be any number. When the user is done with the subsequent activity and returns,
    // the system calls your activity's onActivityResult() method. and that method will be like :
    public static final int REQUEST_CODE = 10005;
    public static final int GOOGLE_SIGN_IN_CODE = REQUEST_CODE;
    SignInButton signIn;
    GoogleSignInClient signInClient;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // link the google button in activity_main to java file!
        signIn = findViewById(R.id.sign_in_button);
        //// Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        // Initialise FirebaseFirestore
        fStore = FirebaseFirestore.getInstance();
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // request id token to authenticate in firebase
                .requestIdToken("931542018563-74nceasobkdf4hccvp7uvkce790ht7d5.apps.googleusercontent.com")
                // i only want to requestEmail()
                .requestEmail()
                .build();
        //get client from googleSignIn
        signInClient = GoogleSignIn.getClient(this,gso);
         /* GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
          --> to check if user is logged in , if user is logged in , go current page
              but i dont want this -> so not putting it!
        if(signInAccount != null){
           // Toast.makeText(this,"User is Logged in already!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,AccountActivity.class));
         }*/

        // [START signin]
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //  Gets an Intent to start the Google Sign In flow by calling startActivityForResult(Intent, int).
                // prompts the user to select a Google account to sign in with.
                Intent sign = signInClient.getSignInIntent();
                //passing the request code
                startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
            }
        });
        // [END signin]
    }
    //Once user is signed in , GoogleSignInAccount object is created in the below onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from signIn.getSignInIntent();
        if(requestCode == GOOGLE_SIGN_IN_CODE){
            // user "comes back" to your app
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // sigInAcc object contains info about the signed-in user
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                // if no exception , it means user is connected!
                // Authentication credentials for an authentication provider. In this case, it is google.
                // to get data from the user , i need to pass the token. the below retrieves creditial
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);
                // this method signs the user into my Firebase Authentication system.
                fAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Once user is authenticately sign in successfully , a toast message and intent to next activity will be created.
                        // Before i send the user to the to do list page, i need to store this user into the firestore database!
                        Toast.makeText(getApplicationContext(),"Your Google account is connected to our app!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity2.class));
                        // get the current User id
                        userID = fAuth.getCurrentUser().getUid();
                        //A DocumentReference refers to a document location in a Cloud Firestore database and can be used to write, read, or listen to the location.
                        // so i want this data to be going into the users colllection and in its userId doc
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        // just to note : signInAcc is the GoogleSignInAccount Object variable.
                        // these information is stored into the firestore!
                        user.put("firstName", signInAcc.getGivenName());
                        user.put("lastName",  signInAcc.getFamilyName());
                        user.put("email",  fAuth.getCurrentUser().getEmail());
                        user.put("mobileNumber",  fAuth.getCurrentUser().getPhoneNumber());
                        user.put("username",  fAuth.getCurrentUser().getDisplayName());
                        user.put("password",  "password cant be revealed!");
                        //  Log.d("tag","onCreate:" + signInAcc.getGivenName());

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // if successful, show successful Toast!
                                Toast.makeText(getApplicationContext(), "User  created for" + userID, Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getApplicationContext(),MainActivity2.class);
                                startActivity(myIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Upon Failure, show unsuccessful toast!
                                Toast.makeText(getApplicationContext(), "Created failed" + e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
            });
            }
            catch(ApiException e ){
                e.printStackTrace();
            }
        }
    }

    // use Intent to let user to login page!
    public void clickLogin(View v) {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
    }
    // use Intent to let user to sign up page!
    public void clickSignup(View v) {
        Intent myIntent = new Intent(this, SignupActivity.class);
        startActivity(myIntent);
    }
}