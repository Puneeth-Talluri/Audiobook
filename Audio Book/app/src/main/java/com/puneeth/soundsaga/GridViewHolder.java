package com.puneeth.soundsaga;

import androidx.recyclerview.widget.RecyclerView;

import com.puneeth.soundsaga.databinding.GridEntryBinding;

public class GridViewHolder extends RecyclerView.ViewHolder {

GridEntryBinding binding;

    GridViewHolder(GridEntryBinding binding)
    {
        super(binding.getRoot());
        this.binding=binding;
    }

}
