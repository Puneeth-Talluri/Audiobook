package com.puneeth.soundsaga;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puneeth.soundsaga.databinding.ActivityAudioBookBinding;
import com.puneeth.soundsaga.databinding.ActivityMyBooksBinding;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyBooksActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MyBooksActivity";
    ActivityMyBooksBinding binding;
    ArrayList<Book> mybooks = new ArrayList<>();

    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appText.setText("SoundSaga");
        binding.activityText.setText("My Books");
//        clearSharedPreferences();
        initMyBooksFromSharedPreferences();
        sortBooksByLastAccessedDateTime();
        Log.d(TAG, "mybooks:size= " + mybooks.size() + "" + mybooks);
        adapter=new BookAdapter(this,mybooks);
        binding.recycler.setAdapter(adapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Code for portrait orientation
            binding.recycler.setLayoutManager(new GridLayoutManager(this,1));
        } else {
            // Code for other orientations, likely landscape
            binding.recycler.setLayoutManager(new GridLayoutManager(this,2));
        }
//        binding.recycler.setLayoutManager(new GridLayoutManager(this,1));

    }

    private void initMyBooksFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
        String jsonBooks = prefs.getString("myBooksKey", null);
        Log.d(TAG, "Retrieved books JSON from SharedPreferences: " + jsonBooks);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Book>>() {
        }.getType();
        ArrayList<Book> storedBooks = gson.fromJson(jsonBooks, type);

        if (storedBooks != null) {
            mybooks = storedBooks;
        }


    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recycler.setLayoutManager(new GridLayoutManager(this, 1));
        }

    }

    private void sortBooksByLastAccessedDateTime() {
        Collections.sort(mybooks, new Comparator<Book>() {
            @Override
            public int compare(Book book1, Book book2) {
                // This will sort the books in descending order of lastAccessedDateTime
                return book2.getLastAccessed().compareTo(book1.getLastAccessed());
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: clicked");
    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }

//    public void showdeleteDialog(Book book,int pos)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(Html.fromHtml("Remove your book history for <b>" + book.getTitle() + "</b>?"))
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        mybooks.remove(book);
//                        adapter.notifyDataSetChanged();
//                        updateSharedPreferences();
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    private void updateSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String jsonBooks = gson.toJson(mybooks);
        editor.putString("myBooksKey", jsonBooks);
        editor.apply();
    }

    public void showdeleteDialog(Book book, int pos)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog2);

        // Set dialog window size or minimum size here, e.g.,
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView imageView = dialog.findViewById(R.id.dialog_image);
        TextView removeText = dialog.findViewById(R.id.removeText);
        TextView okText = dialog.findViewById(R.id.okText);
        TextView cancelText = dialog.findViewById(R.id.cancelText);
        // Initialize other views similarly

        // Load the book image, e.g., using Picasso
        Picasso.get().load(book.getImageUrl()).into(imageView);
        String customTitle="Remove your book history for <b><i>"+book.title+"<i><b>?";
        removeText.setText(HtmlCompat.fromHtml(customTitle, HtmlCompat.FROM_HTML_MODE_LEGACY));
        //removeText.setText("Remove your book history for");
       // bookTitle.setText(book.title+"?");

        // Set text for other TextViews similarly

        okText.setOnClickListener(v -> {
            mybooks.remove(book);
            adapter.notifyDataSetChanged();
            updateSharedPreferences();
            dialog.dismiss();
        });
        cancelText.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void startAudioBook(Book b)
    {
        Intent intent=new Intent(this,AudioBookActivity.class);
        intent.putExtra("Book",b);
        startActivity(intent);
    }



//        private void clearSharedPreferences() {
//        // Get SharedPreferences
//        SharedPreferences prefs = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
//        // Get the Editor
//        SharedPreferences.Editor editor = prefs.edit();
//        // Clear all data
//        editor.clear();
//        // Apply changes
//        editor.apply();
//    }

}