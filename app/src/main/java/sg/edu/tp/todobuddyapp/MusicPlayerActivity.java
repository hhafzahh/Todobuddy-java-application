package sg.edu.tp.todobuddyapp;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;


public class MusicPlayerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //create 2 variables to for the image switcher and button
    ImageSwitcher imageSwitcher;
    Button nextImageButton;
    SeekBar seekbar;
    Handler handler;
    Runnable runnable;
    Button btn_play,btn_pause,btn_stop;
    //create 2 variables to for the button and image capture
    Button takePictureButton;
    private static final int Image_Capture_Code = 1;


    Integer[] images = {
            //creates an array of images to contain the 7 images that was just loaded in drawable folder.
            R.drawable.calm1,
            R.drawable.calm2,
            R.drawable.calm3,
            R.drawable.calm4,
            R.drawable.calm5,
            R.drawable.calm6,
            R.drawable.calm7
    };
    // to track which image will be choosen
    int switcherImage = images.length;
    int counter = 0;

    String url = "";
    MediaPlayer player = null;
    Spinner spinner;
    String[] songs = { "Smooth Piano Music", "Raindrops On Roses", "Study Music" };
    String[] links = { "https://p.scdn.co/mp3-preview/af13ccab7268295bff3fbda19ec140a37e64680b?cid=2afe87a64b0042dabf51f37318616965",
            "https://p.scdn.co/mp3-preview/acc1f20b951dea91e5cfa6d4113d38197a179ded?cid=2afe87a64b0042dabf51f37318616965",
            "https://p.scdn.co/mp3-preview/a9a7e0567a508e5ea94f37d7c3ffe6afa8cb1d77?cid=2afe87a64b0042dabf51f37318616965"};
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        seekbar = findViewById(R.id.seekBarTime);
        btn_play = findViewById(R.id.btnPlay);
        btn_pause = findViewById(R.id.btnPause);
        btn_stop = findViewById(R.id.btnStop);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        nextImageButton = (Button) findViewById(R.id.button10);
        position = 0;

        spinner = (Spinner) findViewById(R.id.spinner);
        //Creates a Spinner with an existing View and binds it to a new ArrayAdapter that reads an array of songs
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, songs);
        //// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //Below code is used to setup the Image properties for the first image.
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(getApplicationContext());
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setImageResource(R.drawable.calm1);
                return switcherImageView;
            }
        });
        //we add animation to switching image to help with transition of images
        Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        Animation animationIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        imageSwitcher.setOutAnimation(animationOut);
        imageSwitcher.setInAnimation(animationIn);

        // when play is button is clicked
        btn_play.setOnClickListener(View -> {
                   //url is what chosen in the spinner position and the links positon
                    url = links[position];
                    //if there is no player
                    if (player == null) {
                        //instantiate a new Mediaplayer
                        player = new MediaPlayer();
                        try {
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            player.setDataSource(url);
                            //collects metadata abt the file to be played
                            player.prepare();
                            //setting max value for seekbar
                            seekbar.setMax((int) player.getDuration());
                        } catch (Exception e) {}
                    }
                    //if player is not playing
                    if (!player.isPlaying()) {
                        player.start();
                        //setting tittle based on spinner -> songs position
                        setTitle(songs[position]);
                    }

                });
        //if user clicks on pause button
        btn_pause.setOnClickListener(View -> {
            //if player is playying
            if (player.isPlaying()) {
                //pauses the playback
                player.pause();
            }
        });
// if user clicks the stop button,
        btn_stop.setOnClickListener(View -> {
            // if player is not empty and it is playiing
            if (player!= null && player.isPlaying())
            {
                //stop player
                player.stop();
              //  player.release();
                // set player to be empty
                player = null;
                if (player == null) {
                    //if play is null , initate a new player
                    player = new MediaPlayer();
                    try {
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(url);
                        player.prepare();
                        seekbar.setMax((int) player.getDuration());
                    } catch (Exception e) {
                    }
                }
            }
        });

       seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //progress is the current progress level
            //b is true if the progress change was intiated by the ue
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                // if true
                if (b) {
                    //if player is playing
                    if (player.isPlaying()) {
                        //pause player
                        player.pause();
                        //usinng the MediaPlayer's seekTo function to seek the file to a particular time which is the progress
                        player.seekTo(progress);
                        //start from there
                        player.start();

                    } else {
                        //else if it is not playing
                        player.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
       // we need a handler that constantly updates the seek bar
        handler = new Handler();
        // also with help of the Runnable to update the Ui
        runnable = new Runnable() {
            @Override
            public void run() {
                if(player == null){
                    //do nothing
                  //  player = new MediaPlayer();
                }

                else if (player.isPlaying()) {
                    //if it is
                    seekbar.setProgress((int) player.getCurrentPosition());
                }
                // the handler updates the seek after every 1000ms.
                handler.postDelayed(runnable, 1000);
            }


        };

         handler.post(runnable);
    }

    //the counter variable initalised earlier, here the codes are  to change to the next image in the gallery.
    public void nextImageButton(View view) {
        counter++;
        if (counter == switcherImage)
            counter = 0;
        imageSwitcher.setImageResource(images[counter]);
    }

    @Override
    // for spinner activity
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        // An item was selected. You can retrieve the selected item using position
        this.position = position;
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }






}