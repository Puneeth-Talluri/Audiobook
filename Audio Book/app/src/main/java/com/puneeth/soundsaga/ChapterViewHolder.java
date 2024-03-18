package com.puneeth.soundsaga;

import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.ChapterEntryBinding;

public class ChapterViewHolder extends RecyclerView.ViewHolder {

    ChapterEntryBinding binding;

    public ChapterViewHolder(ChapterEntryBinding binding){
        super(binding.getRoot());
        this.binding=binding;
    }
}
