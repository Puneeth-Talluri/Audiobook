package com.puneeth.soundsaga;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.BookEntryBinding;
import com.puneeth.soundsaga.databinding.GridEntryBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private static final String TAG="BookAdapters";
    MyBooksActivity myBooksActivity;
    ArrayList<Book> booksList=new ArrayList<>();

    public BookAdapter(MyBooksActivity myBooksActivity,ArrayList<Book> booksList){
        this.myBooksActivity=myBooksActivity;
        this.booksList=booksList;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookEntryBinding binding= BookEntryBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book=booksList.get(position);
        holder.binding.title.setText(book.title);
        holder.binding.author.setText(book.author);
        holder.binding.chapter.setText(book.lastChapterTitle);
        holder.binding.lastRead.setText(book.lastAccessed);
        holder.binding.progress.setText(book.lastChapterTime+" of "+book.getLastChapterDuration());
        Picasso.get().load(book.imageUrl)
                .into(holder.binding.icon, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: " );
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
        holder.itemView.setOnLongClickListener(v -> {
            myBooksActivity.showdeleteDialog(book,position);
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            myBooksActivity.startAudioBook(book);
        });
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }
}
