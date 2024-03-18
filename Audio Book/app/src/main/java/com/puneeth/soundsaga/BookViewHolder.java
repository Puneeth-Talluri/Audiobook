package com.puneeth.soundsaga;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.BookEntryBinding;

public class BookViewHolder extends RecyclerView.ViewHolder {

    BookEntryBinding binding;
    public BookViewHolder(BookEntryBinding binding) {
        super(binding.getRoot());
        this.binding=binding;
    }
}
