package sg.edu.tp.todobuddyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private View HomeFragmentView;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db;
    private String onlineUserID;
    // private TextView output;
    public String idUpdate = "";
    private String docId;
    private ProgressDialog loader;
    private String key = "";
    private String task;
    private String description;
    private String dateline;
    private TextView email;
    private TextView emailHead;
    private String UID;
    private String docId2;


    @Nullable
    @Override

    //The LayoutInflater class is used to instantiate the contents of layout XML files into their corresponding View objects.
    // In other words, it takes an XML file as input and builds the View objects from it.
    // using here as for fragment and the layout is in recyclerview -> easier for complex layouts


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        HomeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        email = HomeFragmentView.findViewById(R.id.textView16);

        // initialise firebase instance
        mAuth = FirebaseAuth.getInstance();
        Log.d("tag","onCreate:" + mAuth.getCurrentUser().getEmail());
        email.setText(mAuth.getCurrentUser().getEmail());
        recyclerView = HomeFragmentView.findViewById(R.id.recyclerView);
        // layoutManager Helps in positioning the items
        // works similar to ListView, using a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        loader = new ProgressDialog(getContext());

        reference = FirebaseDatabase.getInstance().getReference();

        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        db = FirebaseFirestore.getInstance();
        // reference.child("tasks").child(onlineUserID);
        // reference = db.collection("users").document(onlineUserID);

        floatingActionButton = HomeFragmentView.findViewById(R.id.fab);
        // when user clicks the button , a dialog is created ==> which opens the input_file
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }

        });
        return HomeFragmentView;
    }


    private void addTask() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        //links to the file

        View myView = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myView);
        // myDialog.show();


        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);
        final EditText dateline = myView.findViewById(R.id.dateline);
        //   final TextView email = myView.findViewById(R.id.textView16);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String mDateline = dateline.getText().toString().trim();
                String id = reference.push().getKey();
                String mEmail = email.getText().toString();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTask)) {
                    task.setError("Task Required!");
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {
                    description.setError("Description is required");
                    return;
                } else {

                    loader.setMessage("Adding your data!");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    //create an object from the modal class with the the strings created above
                    Modal modal = new Modal(mTask, mDescription, mDateline, id, date);
                    // Create a new user with a first and last name
                    UID = mAuth.getCurrentUser().getUid();
                    // inserting data into database
                    //get location using DocumentReference
                    DocumentReference documentReference = db.collection("users").document(UID);
                    //using Hashmap task to add values into firestore
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", id);
                    task.put("task", mTask);
                    task.put("description", mDescription);
                    task.put("dateline", mDateline);
                    task.put("email", mEmail);
                    task.put("date", date);


                    // Add a new document with a generated ID
                    documentReference.collection("tasks")
                            .add(task)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //if successful show successful
                                    Toast.makeText(getContext(), "Task has been inserted successfully", Toast.LENGTH_SHORT).show();
                                    loader.dismiss();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //else show failed toast message
                                    Toast.makeText(getContext(), "Create Task failed!!" + e, Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
                                }
                            });
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
       // creating FirestoreRecyclerAdapter -->t binds the results of a query to a recyclerview while responding to all the real-time events
        // e.g items added/removed/moved/changed.
        Query query = db.collection("users").document(onlineUserID).collection("tasks");
        //Bind the query to the recyclerview
        FirestoreRecyclerOptions<Modal> options = new FirestoreRecyclerOptions.Builder<Modal>()
                .setQuery(query, Modal.class)
                .build();
       /* FirebaseRecyclerOptions<Modal> options = new FirebaseRecyclerOptions.Builder<Modal>()
                .setQuery(reference, Modal.class)
                .build();

       */
         //Define the adapter object and attach it to your recycler view
        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Modal, MyViewHolder>(options) {
            @NonNull
            @Override
            //Usually involves inflating a layout from XML and returning the holder
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the custom layout
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                // Return a new holder instance
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position, Modal modal) {
                holder.setDate((modal.getDate()));
                holder.setTask(modal.getTask());
                holder.setDesc(modal.getDescription());
                holder.setDateline(modal.getDateline());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //so if uuser clicks on the task
                    public void onClick(View v) {

                        idUpdate = modal.getId();
                        task = modal.getTask();
                        description = modal.getDescription();
                        dateline = modal.getDateline();

                        updateTask();

                    }
                });
            }

        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //set adapter to recyclerview
        recyclerView.setAdapter(adapter);
        //add listening so that it will be populated when i run the app
        adapter.startListening();
    }


    private void updateTask() {
        //Create a dialog
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);
        myDialog.show();


        final AlertDialog dialog = myDialog.create();

        String id = reference.push().getKey();
        String date = DateFormat.getDateInstance().format(new Date());
        final EditText mTask = view.findViewById(R.id.mEditTextTask);
        final EditText mDescription = view.findViewById(R.id.mEditTextDescription);
        final EditText mDateline = view.findViewById(R.id.mEditTextDateline);
        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        mDateline.setText(dateline);
        mDateline.setSelection(dateline.length());

        //2 buttons to link from xml file to java file
        Button delButton = view.findViewById(R.id.btnDelete);
        Button updateButton = view.findViewById(R.id.btnUpdate);
        //if user clicks update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = mTask.getText().toString().trim();
                description = mDescription.getText().toString().trim();
                dateline = mDateline.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Modal modal = new Modal(task, description, dateline, id, date);
                // get the location from firestore to update data using DocumentReference
                //instead of using set(modal) since its 5 segments,
                // documentReference only allows even segnments, so i am using hashmap to update task
                //Getting Reference to "users" collection
                DocumentReference documentReference2 = db.collection("users").document(onlineUserID);

                Map<String, Object> task1 = new HashMap<>();
                task1.put("id", id);
                task1.put("task", task);
                task1.put("description", description);
                task1.put("dateline", dateline);
                //  task.put("email", mEmail);
                task1.put("date", date);

                // if id field in ffirestore for tasks is equal to id in modal(idUpdate) , thennn proceed to retrieve
                documentReference2.collection("tasks").whereEqualTo("id", idUpdate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //if successful ,
                            // for loop to get each document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docId = document.getId();
                                Log.i("The documeNT ID", docId + ": " + document.getData());
                               // update the task data based on document ID
                                documentReference2.collection("tasks").document(docId).update(task1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //if taskk is successful , show toast , and dismiss dialog
                                            Toast.makeText(getContext(), "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            // show the error
                                            String err = task.getException().toString();
                                            Toast.makeText(getContext(), "update failed " + err, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("My ERROR", "Error: " + task.getException());
                            Toast.makeText(getContext(), "Error retrieving my tasks",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        // if user clicks on the delete button
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting Reference to "users" collection
                DocumentReference documentReference3 = db.collection("users").document(onlineUserID);
                // if id field in ffirestore for tasks is equal to id in modal(idUpdate) , thennn proceed to retrieve
                documentReference3.collection("tasks").whereEqualTo("id", idUpdate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docId2 = document.getId();
                                Log.i("My Task", docId2 + ": " + document.getData());
                                //delete task
                                documentReference3.collection("tasks").document(docId2).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //if success then toast
                                        Toast.makeText(getContext(), "Data has been deleted successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                        else {
                                            //if not, show error
                                            String err = task.getException().toString();
                                            Toast.makeText(getContext(), "delete attempt or request failed " + err, Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();

                                    }

                                });

                            }

                        } else {
                            Log.i("My Error", "Error: " + task.getException());
                            Toast.makeText(getContext(), "Error retrieving my tasks",
                                    Toast.LENGTH_LONG).show();
                        }

                    }


                });
            }
        });
    }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            View mView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
            }
            //create getters and setters so that we can use the textviews in the retrieved layouts
            public void setTask(String task) {
                TextView taskTectView = mView.findViewById(R.id.taskTv);
                taskTectView.setText(task);
            }

            public void setDesc(String desc) {
                TextView descTectView = mView.findViewById(R.id.descriptionTv);
                descTectView.setText(desc);
            }

            public void setDateline(String dateline) {
                TextView datelineTectView = mView.findViewById(R.id.dateDueTv);
                datelineTectView.setText(dateline);
            }

            public void setDate(String date) {
                TextView dateTextView = mView.findViewById(R.id.dateTv);
                dateTextView.setText(date);
            }

        }
    }




