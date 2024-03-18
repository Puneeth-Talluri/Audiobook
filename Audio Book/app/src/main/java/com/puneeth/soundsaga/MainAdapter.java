package com.puneeth.soundsaga;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.GridEntryBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<GridViewHolder> {

    private static final String TAG="MainAdapter";
    private final MainActivity mainActivity;

    private final ArrayList<Book> displayedBooks;

    public MainAdapter(MainActivity mainActivity, ArrayList<Book> displayedBooks)
    {
        this.displayedBooks=displayedBooks;
        this.mainActivity=mainActivity;
    }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridEntryBinding bind= GridEntryBinding.inflate(LayoutInflater.from(parent.getContext()));
        bind.getRoot().setOnClickListener(mainActivity);
//        bind.getRoot().setOnLongClickListener(mainActivity);
        return new GridViewHolder(bind);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Book b=displayedBooks.get(position);
        holder.binding.author.setText(b.author);
        holder.binding.title.setText(b.title);
        Picasso.get().load(b.imageUrl)
                .into(holder.binding.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: " );
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
        holder.binding.title.setSelected(true);
        holder.binding.author.setSelected(true);

        holder.itemView.setOnLongClickListener(v -> {
            mainActivity.showBookDetailsDialog(b);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return displayedBooks.size();
    }

}
