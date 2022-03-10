package sg.edu.tp.todobuddyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private View SettingsFragmentView;
    private Button button1;
    SwitchCompat switchTurn; // naming the widget SwitchCompat with switchTurn
    SharedPreferences sharedPreferences = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SettingsFragmentView =  inflater.inflate(R.layout.fragment_settings,container,false);
        button1 = (Button)SettingsFragmentView.findViewById(R.id.button9);
        switchTurn = SettingsFragmentView.findViewById(R.id.themeChanger);
        // mode = 0  - private mode --> the default mode, where the created file can only be accessed by the calling application
        //whenever getSharedPreferences(String name,int mode) function get called it validates the file that if it exists or it doesn’t then a new xml is created with passed name

        // using custom preference way
        //night is the name of the preference file
        //save the current mode in a SharedPreferences object
        sharedPreferences = getActivity().getSharedPreferences("night",0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode",true);
        if (booleanValue) {
            //to retrieve the current night mode type , we use the method AppCompatDelegate.setDefaultNightMode()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchTurn.setChecked(true);
        }

        switchTurn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // if user is check switch button then apply Night mode On
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchTurn.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }else {
                    // if user doesnt then apply Night MODE OFF
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchTurn.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();

                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPlay();
            }

        });
        return SettingsFragmentView;
    }

    private void clickPlay() {
        Intent myIntent = new Intent(getContext(),MusicPlayerActivity.class);
        startActivity(myIntent);
    }


}
