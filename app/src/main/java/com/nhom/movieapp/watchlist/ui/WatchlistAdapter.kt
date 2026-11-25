package com.nhom.movieapp.watchlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhom.movieapp.data.local.WatchlistEntity
import com.nhom.movieapp.databinding.ItemWatchlistBinding

class WatchlistAdapter(
    private val onWatchedClick: (WatchlistEntity) -> Unit,
    private val onRemoveClick: (WatchlistEntity) -> Unit
) : ListAdapter<WatchlistEntity, WatchlistAdapter.WatchlistViewHolder>(
    DiffCallback()
) {

    inner class WatchlistViewHolder(
        private val binding: ItemWatchlistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: WatchlistEntity) {
            binding.textTitle.text = movie.title
            binding.textRating.text = "Đánh giá: ${movie.voteAverage}"
            binding.textWatched.text =
                if (movie.isWatched) "Đã xem" else "Chưa xem"

            binding.btnWatched.text =
                if (movie.isWatched) "Đánh dấu chưa xem"
                else "Đánh dấu đã xem"

            val posterUrl = movie.posterPath?.let {
                "https://image.tmdb.org/t/p/w500$it"
            }

            Glide.with(binding.root.context)
                .load(posterUrl)
                .into(binding.imagePoster)

            binding.btnWatched.setOnClickListener {
                onWatchedClick(movie)
            }

            binding.btnRemove.setOnClickListener {
                onRemoveClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WatchlistViewHolder {
        val binding = ItemWatchlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WatchlistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WatchlistViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WatchlistEntity>() {
        override fun areItemsTheSame(
            oldItem: WatchlistEntity,
            newItem: WatchlistEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WatchlistEntity,
            newItem: WatchlistEntity
        ): Boolean = oldItem == newItem
    }
}
