package sg.edu.tp.todobuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.Calendar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String out;
    private String getKey;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    //create a drawerlayout variable
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /*Intent intent = getIntent();
        //out = intent.getExtras().getString("message_key");
        //getKey = intent.getExtras().getString("ID");*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        drawer = findViewById(R.id.drawer_layout);
       // linking bottom navigation from activity_mainAct2 to java file
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // To get the menu button in the top left corner of our activity
        // instead of doing menu hamburger drawable icon to be placed and giving onclick method , would not give the rotating animation.
        // but there is a class called "ActionBarDrawerToggle that does everything for you.
        //drawer and toolbar  are the two views that will sycronised
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //rotating and syncing
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        bottomNav.setOnNavigationItemSelectedListener(navListener);
        // as soon as the application opens the first
        // fragment should be shown to the user
        // in this case it is HomeFragment fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }
    public Bundle getMyData() {
        Bundle hm = new Bundle();
        hm.putString("val1",out);
        hm.putString("val2",getKey);
        return hm;
    }
     // i didnt use the bottonNav activity inbuilt in android studio as it does not switch between fragments but only changes text!
    //  to click items,we can do this in onCreate too but its messy
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            // By using switch we can easily get the selected fragment by using its id.

            switch (item.getItemId()){
                //linking to id in menu
                // if navigation_home is clicked
                case R.id.navigation_home:
                    // set selectedFragment to open to be HomeFragment
                selectedFragment = new HomeFragment();

                break;
                case R.id.navigation_timer:
                    //linking to id in menu
                    // if navigation_home is clicked
                    // set selectedFragment to open to be TimerFragment
                    selectedFragment = new TimerFragment();
                    break;
                case R.id.navigation_profile:
                    // set selectedFragment to open to be ProfileFragment
                    selectedFragment = new ProfileFragment();
                    break;


            }
            // It will help to replace from  one fragment to other into the fragment_container created in the activity_main2
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            // return true - item will be selected.
            return true;
        }

    };

    @Override
    //callback method for the implementation
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //if home is clicked,
            case R.id.nav_home:
                // open homefragment and display in the fragment_container
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;
            //if profile is clicked,
            case R.id.nav_profile:
                // open profileFragment and display in the fragment_container
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                break;
            //if settings is clicked,
            case R.id.nav_settings:
                // open settingFragment and display in the fragment_container
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
                break;
            case R.id.nav_leave:
                // if sign out is clicked,
                // sign out autenticated user
               mAuth.signOut();
                //if user is signed in by google, using this to signOut
                GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build()).signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // successfully signed out google user will go to main activity page
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Unsuccessfull , toast will be shown
                        Toast.makeText(MainActivity2.this,"LOGOUT FAILED",Toast.LENGTH_SHORT).show();
                    }
                });
                //show toast message and send user to loginActivity
                Toast.makeText(this,"Signed Out",Toast.LENGTH_SHORT).show();
                Intent intent  = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        //if drawer is open, gravity compat.start -> start as my drawer layout_gravity is left
        //Push object to x-axis position at the start of its container, not changing its size.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //close nav drawer
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //if drawer is not opened, then it will close the activity.
            super.onBackPressed();
        }
    }

}