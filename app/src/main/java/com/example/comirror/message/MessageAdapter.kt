package com.example.comirror.message

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.comirror.data.MessageDTO
import com.example.comirror.databinding.MessageListBinding


class MessageAdapter(private val data: ArrayList<MessageDTO>, private val context: Context) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val binding: MessageListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messageData: MessageDTO) {
            binding.apply {
                senderNameTextView.text = messageData.senderName
                contentTextView.text = messageData.content
            }
        }
    }

}


