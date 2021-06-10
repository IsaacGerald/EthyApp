package com.example.ethysell.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.ethysell.databinding.ReviewListItemBinding
import com.example.ethysell.model.Comment


class ReviewsAdapter(private val clickListener: OnCommentClickListener) :
    ListAdapter<Comment, ReviewsAdapter.CommentViewHolder>(
        CommentDiffUtil
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }


    class CommentViewHolder(private val binding: ReviewListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment, clickListener: OnCommentClickListener) {
            binding.comment = item
            binding.clickListener = clickListener
            binding.invalidateAll()

        }

        companion object {
            fun from(parent: ViewGroup): CommentViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = ReviewListItemBinding.inflate(inflater, parent, false)
                return CommentViewHolder(view)
            }
        }


    }


    companion object CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

    }



}

class OnCommentClickListener(val clickListener: (comment: Comment) -> Unit){
    fun onClick(comment: Comment) = clickListener(comment)
}
