package com.puneeth.soundsaga;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puneeth.soundsaga.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private boolean keepOn = true;
    private static final long minSplashTime = 2000;
    private long startTime;
    private ArrayList<Book> downloadedBooks=new ArrayList<>();

    private MainAdapter adapter;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());

        startTime=System.currentTimeMillis();

        //async process
        downloadVolley.downloadBooks(this);


        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(
                        new SplashScreen.KeepOnScreenCondition() {
                            @Override
                            public boolean shouldKeepOnScreen() {
                                Log.d(TAG, "shouldKeepOnScreen: " + (System.currentTimeMillis() - startTime));
                                return keepOn || (System.currentTimeMillis() - startTime <= minSplashTime);
                            }
                        }
                );

        adapter=new MainAdapter(this,downloadedBooks);
        binding.recycler.setAdapter(adapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Code for portrait orientation
            binding.recycler.setLayoutManager(new GridLayoutManager(this,2));
        } else {
            // Code for other orientations, likely landscape
            binding.recycler.setLayoutManager(new GridLayoutManager(this,4));
        }
//        binding.recycler.setLayoutManager(new GridLayoutManager(this,2));
        setContentView(binding.getRoot());


    }

    protected void acceptDownload(ArrayList<Book> allbooks){
        downloadedBooks.addAll(allbooks);
        adapter.notifyDataSetChanged();
        Chapter temp=downloadedBooks.get(0).chaptersList.get(0);
        Log.d(TAG, "acceptDownload: "+temp);
        keepOn=false;
    }


    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick: was clicked");
        intent=new Intent(this,AudioBookActivity.class);
        int pos=binding.recycler.getChildLayoutPosition(v);
        Book b=downloadedBooks.get(pos);
        intent.putExtra("Book",b);
        startActivity(intent);
    }

//    @Override
//    public boolean onLongClick(View v) {
//        Log.d(TAG, "onLongClick: was clicked");
//        return false;
//    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.recycler.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        }

    }

    public void showBookDetailsDialog(Book book) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        // Set dialog window size or minimum size here, e.g.,
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView imageView = dialog.findViewById(R.id.dialog_image);
        TextView titleTextView = dialog.findViewById(R.id.removeText);
        TextView chapterTextView = dialog.findViewById(R.id.dialogChapters);
        TextView durationTextView = dialog.findViewById(R.id.dialogDuration);
        TextView LanguageTextView = dialog.findViewById(R.id.dialogLanguage);
        // Initialize other views similarly

        // Load the book image, e.g., using Picasso
        Picasso.get().load(book.getImageUrl()).into(imageView);

        String titleText = book.getTitle() + " (" + book.date + ") <br /><i>" + book.author + "</i>";
        titleTextView.setText(HtmlCompat.fromHtml(titleText, HtmlCompat.FROM_HTML_MODE_LEGACY));

//        titleTextView.setText(book.getTitle()+" ("+book.date+") \n "+book.author);
        chapterTextView.setText(book.chaptersList.size()+" Chapters.");
        durationTextView.setText("Duration: "+book.duration);
        LanguageTextView.setText("Language: "+book.language);
        // Set text for other TextViews similarly

        TextView okButton = dialog.findViewById(R.id.okText);
        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void startMyBooks(View v){

        if(initMyBooksFromSharedPreferences())
        {
            Intent intent=new Intent(this,MyBooksActivity.class);
            startActivity(intent);
        }
    }

    private boolean initMyBooksFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
        String jsonBooks = prefs.getString("myBooksKey", null);
        Log.d(TAG, "Retrieved books JSON from SharedPreferences: " + jsonBooks);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Book>>() {
        }.getType();
        ArrayList<Book> storedBooks = gson.fromJson(jsonBooks, type);

        if (storedBooks.isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("My Books Shelf is empty");
            alertDialogBuilder.setMessage(
                    "You currently have no audiobooks in progress.");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Just dismiss the dialog
                            dialog.dismiss();
                        }
                    });

            alertDialogBuilder.setIcon(R.drawable.logo);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

            return false;

        }

        return true;
    }


}