package com.puneeth.soundsaga;

import androidx.activity.BackEventCompat;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puneeth.soundsaga.databinding.ActivityAudioBookBinding;
import com.puneeth.soundsaga.databinding.ActivityMainBinding;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioBookActivity extends AppCompatActivity {

    private static final String TAG = "AudioBookActivity";
    ActivityAudioBookBinding binding;

    AudioAdapter adapter;

    Book book;
    Intent intent;

    int pos;

    ArrayList<Chapter> chapters=new ArrayList<>();
    ArrayList<Book> mybooks=new ArrayList<>();

    //mediaplayer
    public MediaPlayer player;
    private String url;
    private int startTime;
    private float speed;
    private Timer timer;
    private int mediaCounter = 1;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAudioBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent=getIntent();
        book=(Book)intent.getSerializableExtra("Book");
        Log.d(TAG, "onCreate: of audiobookactivity initiated");

        chapters.addAll(book.chaptersList);
        binding.titleText.setText(book.title);
        //setting the adapter
        adapter=new AudioAdapter(this,book);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.e("Selected_Page", String.valueOf(position));
                if(pos!=position){
                    pos=position;
                    url=chapters.get(pos).audioUrl;
                    playIt();
                }

            }

        });



//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                // Update the book object
//                updateBookState();
//
//                // Check if the current book is already in mybooks and update it
//                int existingIndex = mybooks.indexOf(book);
//                if(existingIndex != -1) {
//                    mybooks.set(existingIndex, book);
//                } else {
//                    mybooks.add(book);
//                }
//
//                // Load existing books from SharedPreferences
//                SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
//                String jsonBooks = prefs.getString("myBooksKey", null);
//                Gson gson = new Gson();
//                Type type = new TypeToken<ArrayList<Book>>() {}.getType();
//                ArrayList<Book> existingBooks = gson.fromJson(jsonBooks, type);
//
//                if(existingBooks == null) {
//                    existingBooks = new ArrayList<>();
//                }
//
//                // Update existingBooks with mybooks
//                for(Book newBook : mybooks) {
//                    existingIndex = existingBooks.indexOf(newBook);
//                    if(existingIndex != -1) {
//                        existingBooks.set(existingIndex, newBook);
//                    } else {
//                        existingBooks.add(newBook);
//                    }
//                }
//
//                // Save the updated list back to SharedPreferences
//                SharedPreferences.Editor editor = prefs.edit();
//                jsonBooks = gson.toJson(existingBooks);
//                Log.d(TAG, "Saving books to SharedPreferences. Books: " + gson.toJson(existingBooks));
//                editor.putString("myBooksKey", jsonBooks);
//                editor.apply();
//
//                // Finish activity
//                AudioBookActivity.this.finish();
//            }
//        };


        player = new MediaPlayer();
        player.setOnCompletionListener(mediaPlayer -> {
            timer.cancel();


            if(pos<chapters.size()) {
                pos++;
            }
            else{
                    pos=0;
            }
            binding.viewPager.setCurrentItem(pos, true); // true for smooth scroll

            // Since you're updating the adapter data, notify the change
            adapter.notifyDataSetChanged();

            // Update the url to the next chapter's audio URL and play it
            url = chapters.get(pos).audioUrl;
            playIt();
        });

        setupSeekBar();
        setupSpeedMenu();

        presetVars();

        if (savedInstanceState != null) {
            updateFromBundle(savedInstanceState);
        }

        playIt();



    }

    private void updateFromBundle(Bundle bundle) {

        url = bundle.getString("URL");
        startTime = bundle.getInt("TIME");
        speed = bundle.getFloat("SPEED");
        //int savedPosition = bundle.getInt("pos", 0); // Default to 0 if not found
        pos=bundle.getInt("pos");
        binding.viewPager.setCurrentItem(pos, true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("URL", url);
        outState.putInt("TIME", player.getCurrentPosition());
        outState.putFloat("SPEED", speed);
        outState.putInt("POSITION", binding.viewPager.getCurrentItem());
        outState.putInt("pos",pos);
        super.onSaveInstanceState(outState);
    }

    private void presetVars() {

        if(book.isRead){
            url = chapters.get(book.lastPos).audioUrl;
            startTime = book.lastPlayerTime;
            Log.d(TAG, "presetVars: lastPos"+book.lastPos+"lastPlayerTime="+book.lastPlayerTime);
            speed = 1.0f;
            binding.speedText.setText(""+speed+"x");
            binding.speedText.setPaintFlags(binding.speedText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            pos= book.lastPos;
            binding.viewPager.setCurrentItem(pos);
        }
        else{
            url = chapters.get(pos).audioUrl;
            startTime = 0;
            speed = 1.0f;
            binding.speedText.setText(""+speed+"x");
            binding.speedText.setPaintFlags(binding.speedText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        }
    }


    public void playIt() {
        try {
            player.reset();
            player.setDataSource(url);
            player.prepare(); // Consider using prepareAsync() for network streams
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Set the seek bar's maximum to the new audio's duration (in milliseconds)

                    if(book.isRead)
                    {
                        binding.seekBar.setProgress(startTime);
                    }
                    else{
                        binding.seekBar.setProgress(0);
                    }
                    binding.seekBar.setMax(player.getDuration());

                    // Reset the seek bar's progress

                    player.seekTo(startTime);
                    // Start playback
                    player.start();

                    player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));

                    // Reset and start the timer for updating the seek bar
                    timerCounter();

                    if(pos==0){
                        binding.backArrow.setVisibility(View.GONE);
                    }
                    else if(pos>0 && pos<chapters.size()-1)
                    {
                        binding.backArrow.setVisibility(View.VISIBLE);
                        binding.frontArrow.setVisibility(View.VISIBLE);
                    }
                    else{
                        binding.frontArrow.setVisibility(View.GONE);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error playing chapter audio", e);
        }
    }




    private void timerCounter() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (player != null && player.isPlaying()) {
                        binding.seekBar.setProgress(player.getCurrentPosition());
                        binding.currentTime.setText(getTimeStamp(player.getCurrentPosition()));
                        binding.totalDuration.setText(getTimeStamp(player.getDuration()));

                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private String getTimeStamp(int ms) {
        int t = ms;
        int h = ms / 3600000;
        t -= (h * 3600000);
        int m = t / 60000;
        t -= (m * 60000);
        int s = t / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }

    private void setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        // Don't need
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Don't need
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        player.seekTo(progress);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        player.release();
        player = null;
        super.onDestroy();
    }

    public void goBack(View v) {
        if (player != null && player.isPlaying()) {
            int pos = player.getCurrentPosition();
            pos -= 15000;
            if (pos < 0)
                pos = 0;
            player.seekTo(pos);
        }
    }

    public void goForward(View v) {
        if (player != null && player.isPlaying()) {
            int pos = player.getCurrentPosition();
            pos += 15000;
            if (pos > player.getDuration())
                pos = player.getDuration();
            player.seekTo(pos);
        }
    }

    public void speedClick(View v) {
        popupMenu.show();
    }

    private void setupSpeedMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
//        popupMenu = new PopupMenu(this, binding.speedText);
        popupMenu = new PopupMenu(new ContextThemeWrapper(this, R.style.CustomPopupMenu), binding.speedText);
        popupMenu.getMenuInflater().inflate(R.menu.speed_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_075) {
                speed = 0.75f;
            } else if (menuItem.getItemId() == R.id.menu_1) {
                speed = 1f;
            } else if (menuItem.getItemId() == R.id.menu_11) {
                speed = 1.1f;
            } else if (menuItem.getItemId() == R.id.menu_125) {
                speed = 1.25f;
            } else if (menuItem.getItemId() == R.id.menu_15) {
                speed = 1.5f;
            } else if (menuItem.getItemId() == R.id.menu_175) {
                speed = 1.75f;
            } else if (menuItem.getItemId() == R.id.menu_2) {
                speed = 2f;
            }

            binding.speedText.setText(menuItem.getTitle());
//            binding.speedText.setPaintFlags(binding.speedText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
            return true;
        });
    }

    public void doPlayPause(View v) {
        if (player.isPlaying()) {
            player.pause();
            binding.playPause.setImageResource(R.drawable.play);
        } else {
            player.start();
            binding.playPause.setImageResource(R.drawable.pause);

        }
    }
    public void gotoNext(View v){
        pos++;
        binding.viewPager.setCurrentItem(pos, true); // true for smooth scroll
        // Since you're updating the adapter data, notify the change
        adapter.notifyDataSetChanged();
        // Update the url to the next chapter's audio URL and play it
        url = chapters.get(pos).audioUrl;
        playIt();
    }

    public void gotoPrev(View v){
        pos--;
        binding.viewPager.setCurrentItem(pos, true); // true for smooth scroll
        // Since you're updating the adapter data, notify the change
        adapter.notifyDataSetChanged();
        // Update the url to the next chapter's audio URL and play it
        url = chapters.get(pos).audioUrl;
        playIt();
    }

    private void updateBookState() {
        book.isRead = true;
        book.lastReadChapter = chapters.get(pos).number;
        if(player != null) {
            book.lastChapterTime = getTimeStamp(player.getCurrentPosition());
        } else {
            book.lastChapterTime = getTimeStamp(0);
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());
        book.lastAccessed=newFormat.format(new Date());
        book.lastChapterDuration=getTimeStamp(player.getDuration());
        book.lastChapterTitle=chapters.get(pos).title;
        book.lastPos=pos;
        book.lastPlayerTime=player.getCurrentPosition();
    }

    @Override
    public void onBackPressed() {
        // Update the book object
        updateBookState();

        // Check if the current book is already in mybooks and update it
        int existingIndex = mybooks.indexOf(book);
        if(existingIndex != -1) {
            mybooks.set(existingIndex, book);
        } else {
            mybooks.add(book);
        }

        // Load existing books from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
        String jsonBooks = prefs.getString("myBooksKey", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Book>>() {}.getType();
        ArrayList<Book> existingBooks = gson.fromJson(jsonBooks, type);

        if(existingBooks == null) {
            existingBooks = new ArrayList<>();
        }

        // Update existingBooks with mybooks
        for(Book newBook : mybooks) {
            existingIndex = existingBooks.indexOf(newBook);
            if(existingIndex != -1) {
                existingBooks.set(existingIndex, newBook);
            } else {
                existingBooks.add(newBook);
            }
        }

        // Save the updated list back to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        jsonBooks = gson.toJson(existingBooks);
        Log.d(TAG, "Saving books to SharedPreferences. Books: " + gson.toJson(existingBooks));
        editor.putString("myBooksKey", jsonBooks);
        editor.apply();

        // Finish activity
        AudioBookActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle changes for landscape mode
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            // Handle changes for portrait mode
        }
    }




}