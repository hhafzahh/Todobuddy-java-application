package sg.edu.tp.todobuddyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment {
    private View TimerFragmentView;
    private static TextView countdownTimerText;
    private static EditText minutes;
    private static Button startTimer, resetTimer;
    private static CountDownTimer countDownTimer;
    private EditText mEditTextInput; // input by user
    private TextView mTextViewCountDown; // the Countdown Timer that is shown
    private Button mButtonSet; // button to set the input into the the countdown timer
    private Button mButtonStartPause; //startPause button
    private Button mButtonReset; // Reset Timer button
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning; // this variable will tell if timer is running on or not.

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        TimerFragmentView = inflater.inflate(R.layout.fragment_timer, container, false);
        mEditTextInput = TimerFragmentView.findViewById(R.id.edit_text_input);
        mTextViewCountDown = TimerFragmentView.findViewById(R.id.text_view_countdown);
        mButtonSet = TimerFragmentView.findViewById(R.id.button_set);
        mButtonStartPause = TimerFragmentView.findViewById(R.id.button_start_pause);
        mButtonReset = TimerFragmentView.findViewById(R.id.button_reset);
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(getActivity(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //converts to minutes
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) { //if it is 0, then toast
                    Toast.makeText(getActivity(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return; //return so that it does not run after this
                }
                //set the time
                setTime(millisInput);
                // set the input empty string
                mEditTextInput.setText("");
            }
        });
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If Timer is running
                if (mTimerRunning) {
                    //call pausetimer()
                    pauseTimer();
                } else {

                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        return TimerFragmentView;
    }
    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
       // closeKeyboard();
    }
    private void startTimer() {

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        //set to new CountDownTimer(mTimeLeftInMillis,INTERVAL)
        //this interval is the the interval along the way to receive onTick(long) callbacks.Every 1000s
        // so basically every 1000milliseconds , onTick Function will be called
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // millisUntilFinished is number of millis until the countdowntimer is finished
                mTimeLeftInMillis = millisUntilFinished;
                //update the textview in the xml file
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                //Timer running to be false since it is not running
                mTimerRunning = false;
                //Since it is not running , set the text button to start
                //set the set button visible
                updateWatchInterface();
            }
        }.start();
        //set it True
        mTimerRunning = true;
        updateWatchInterface();
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        //timer running as false
        mTimerRunning = false;
        //Inside the updateWatchInterface() method,
        // since the timer is not running, set buttonSet and EditTextInput Visible
       //also set button text to start
        updateWatchInterface();
    }
    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        //reset button to disappear -> make the view invisible
        updateWatchInterface();
    }
    private void updateCountDownText() {
        //this is the calculation for the textView
        // 1 hr has 3600s
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        // after the above is done, whatever is left is divided by 1000 , then modulus with 3600 then divide by 60
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        // after the above is done , divide and modulus to find the number of seconds
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;
        // if hours is more than 0
        if (hours > 0) {
            // format the string
            //+ for example 75, then the counter will be formatted to show 1:15:00 i.e. in HH:MM:SS format. instead of 75:00
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            // if the hours is 0
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeftFormatted); //updates the text shown in the xml file( countdown text)
    }
    private void updateWatchInterface() {
        if (mTimerRunning) {
            //if timer is running,
            //set the other EditTexts to be invisible
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            //change it to pause
            mButtonStartPause.setText("Pause");
        } else {
            //if it is not running
            //then set it visible
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            //set text to start
            mButtonStartPause.setText("Start");
            if (mTimeLeftInMillis < 2000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // this is so that the timer is still running
        SharedPreferences prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //we can save data into the editor
        SharedPreferences.Editor editor = prefs.edit();
        //putting the data into editor
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        //apply() same as save the data
        editor.apply();
        if (mCountDownTimer != null) {
            // if it is not null , cancel
            mCountDownTimer.cancel();
        }
    }
    @Override
    public void onStart() {
        //onStart() is the first thing that will be called after onCreate method!
        super.onStart();
        //timer will still run if u leave the app. Instead of using a service , we can compare the system time the next time we open it and the ttime we leave the app
        SharedPreferences prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //default value that is started will be 750000 or basically any number hardcoded
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 750000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        if (mTimerRunning) {
            //if it is running/ if it is true
            // end time default value will be 0
            mEndTime = prefs.getLong("endTime", 0);
            //System.currentTimeMillis() returns the current time in Milliseconds.
            // so time left = end time - current time and if it is negative, then it is no use/doesnt make sense,
            // so set back to 0 & set timer running to false.
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                //if it is negative , then reset to 0
                mTimeLeftInMillis = 0;
                // make timer running false
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                //else if timeleft is more than 0 , then startTimer()
                startTimer();
            }
        }
    }
}

