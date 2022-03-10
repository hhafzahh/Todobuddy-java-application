package sg.edu.tp.todobuddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    TextView firstName, lastName, userName, email, mobileNumber, password;

    private DatabaseReference reference;
    private FloatingActionButton floatingActionButton2;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private View ProfileFragmentView;
    private String UID;
    private FirebaseFirestore db;
    private String onlineUserID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ProfileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        MainActivity2 activity = (MainActivity2) getActivity();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        // initialise firebase instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        mUser = mAuth.getCurrentUser();
        //get Uid
        onlineUserID = mUser.getUid();
        //intialise firestore instance
        db = FirebaseFirestore.getInstance();

        floatingActionButton2 = ProfileFragmentView.findViewById(R.id.editorBtn);
        // when user clicks the button , a dialog is created ==> which opens the input_file
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user clicks the edit button , it will go to to updateprofile activity page
                Intent myIntent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(myIntent);
            }

        });
        return ProfileFragmentView;
    }


@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        firstName = getActivity().findViewById(R.id.TextFirstNamae);
        lastName = getActivity().findViewById(R.id.TextLastName);
        mobileNumber = getActivity().findViewById(R.id.TextMobile);
        email = getActivity().findViewById(R.id.TextEmail);
        password = getActivity().findViewById(R.id.TextPassword);
        userName = getActivity().findViewById(R.id.TextUsername);


}
    public void onStart() {
        super.onStart();
        DocumentReference reference;
        // getting data from firestore collectio path "users" with an document id of onlineUserId
        reference = db.collection("users").document(onlineUserID);
        //using documentSnapshot to retrieve
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){

                    String firstResult = task.getResult().getString("firstName");
                    String lastResult = task.getResult().getString("lastName");
                    String emailResult = task.getResult().getString("email");
                    String mobileResult = task.getResult().getString("mobileNumber");
                    String passwordResult = task.getResult().getString("password");
                    String userNameResult = task.getResult().getString("username");
                    // setting the textView XXXXX to firestore user data firstname
                    firstName.setText(firstResult);
                    lastName.setText(lastResult);
                    email.setText(emailResult);
                    mobileNumber.setText(mobileResult);
                    password.setText(passwordResult);
                    userName.setText(userNameResult);

                }

                  //  Intent intent = new Intent(getActivity(),SignupActivity.class);
                   // startActivity(intent);

            }
        });



    }

}

