package com.udb.travelgo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udb.travelgo.databinding.ItemDestinationBinding
import com.udb.travelgo.model.Destination

class DestinationAdapter(
    private val onEdit: (Destination) -> Unit,
    private val onDelete: (Destination) -> Unit
) : ListAdapter<Destination, DestinationAdapter.DestinationViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemDestinationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DestinationViewHolder(private val binding: ItemDestinationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: Destination) {
            binding.tvName.text = destination.name
            binding.tvCountry.text = destination.country
            binding.tvDescription.text = destination.description
            binding.tvPrice.text = binding.root.context.getString(
                com.udb.travelgo.R.string.price_format, destination.price
            )
            Glide.with(binding.imgDestination.context)
                .load(destination.imageUrl)
                .placeholder(com.udb.travelgo.R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(binding.imgDestination)

            binding.btnEdit.setOnClickListener { onEdit(destination) }
            binding.btnDelete.setOnClickListener { onDelete(destination) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Destination>() {
            override fun areItemsTheSame(oldItem: Destination, newItem: Destination) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Destination, newItem: Destination) =
                oldItem == newItem
        }
    }
}
