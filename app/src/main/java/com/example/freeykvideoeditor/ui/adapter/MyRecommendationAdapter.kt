package com.example.freeykvideoeditor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeykvideoeditor.databinding.ItemMyrecommendationBinding

class MyRecommendationAdapter(private val list: List<String>) :
    RecyclerView.Adapter<MyRecommendationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMyrecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyrecommendationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}