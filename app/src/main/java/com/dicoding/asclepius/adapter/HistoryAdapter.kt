package com.dicoding.asclepius.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.dicoding.asclepius.database.History
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ItemHistoryBinding

class HistoryAdapter(private val context: Context, private var histories: List<History> = listOf()) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, context)
    }

    override fun getItemCount(): Int = histories.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = histories[position]
        holder.bind(history)
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.resultText.text = history.resultLabel
            val imageUri = Uri.parse(history.image)
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.resultImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.resultImage.setImageResource(R.drawable.ic_place_holder)
                Toast.makeText(binding.root.context, "Could not load image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newFavorites: List<History>) {
        this.histories = newFavorites
    }
}