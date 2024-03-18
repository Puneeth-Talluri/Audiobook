package com.puneeth.soundsaga;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.ChapterEntryBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<ChapterViewHolder> {

    private static final String TAG = "AudioAdapter";

    AudioBookActivity audioBookActivity;
    ArrayList<Chapter> chapterlist=new ArrayList<>();

    Book book;

    public AudioAdapter(AudioBookActivity audioBookActivity, Book book) {
        this.audioBookActivity = audioBookActivity;
        this.book=book;
        this.chapterlist = book.chaptersList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ChapterEntryBinding binding=ChapterEntryBinding.inflate(LayoutInflater.from(parent.getContext()));
        ChapterEntryBinding binding=ChapterEntryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ChapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter ch=chapterlist.get(position);

        holder.binding.chapterTitle.setText(ch.title);
        holder.binding.chapterTitle.setSelected(true);
        Picasso.get().load(book.imageUrl)
                .into(holder.binding.chapterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: " );
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return chapterlist.size();
    }
}
