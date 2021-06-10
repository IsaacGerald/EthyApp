package com.example.ethysell.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ethysell.R
import com.example.ethysell.databinding.MainListItemBinding
import com.example.ethysell.databinding.ProductsListItemBinding

import com.example.ethysell.model.Data

class ProductAdapter(private val clickListener: OnProductClickListener) : ListAdapter<Data, ProductAdapter.ProductsViewHolder>(ProductsDiffUtill) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item, clickListener)
    }


    class ProductsViewHolder(private val binding: ProductsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Data, clickListener: OnProductClickListener) {
            binding.data = item
            binding.clickListener = clickListener
            binding.invalidateAll()
        }


        companion object {
            fun from(parent: ViewGroup): ProductsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = ProductsListItemBinding.inflate(inflater, parent, false)

                return ProductsViewHolder(view)
            }
        }
    }


    object ProductsDiffUtill : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

    }

}

class OnProductClickListener(val clickListener: (product: Data) -> Unit){
    fun onClick(product: Data) = clickListener(product)
}