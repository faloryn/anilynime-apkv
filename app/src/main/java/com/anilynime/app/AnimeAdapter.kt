package com.anilynime.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anilynime.app.databinding.ItemAnimeBinding
import com.bumptech.glide.Glide

class AnimeAdapter(
    private val items: MutableList<AnimeItem> = mutableListOf(),
    private val onItemClick: (AnimeItem) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.ViewHolder>() {

    fun submitList(newItems: List<AnimeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AnimeItem) {
            binding.tvTitle.text = item.title
            binding.tvMeta.text = listOfNotNull(item.year, item.latestTime).joinToString(" • ")

            if (!item.episode.isNullOrEmpty()) {
                binding.tvEpBadge.visibility = View.VISIBLE
                binding.tvEpBadge.text = "Ep ${item.episode}"
            } else {
                binding.tvEpBadge.visibility = View.GONE
            }

            if (!item.type.isNullOrEmpty()) {
                binding.tvTypeBadge.visibility = View.VISIBLE
                binding.tvTypeBadge.text = item.type
            } else {
                binding.tvTypeBadge.visibility = View.GONE
            }

            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .centerCrop()
                .into(binding.ivPoster)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
