package com.victoris.neumorphism_chat.client.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.data.Chat
import com.victoris.neumorphism_chat.data.DateTime
import com.victoris.neumorphism_chat.host.adapter.HostChatAdapter

class ClientChatAdapter(
    private val user: String,
    private val listChat: List<Chat>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class YourViewHolder(chatView: View): RecyclerView.ViewHolder(chatView) {
        var tvTime: TextView = itemView.findViewById(R.id.tv_time)
        var tvMsg: TextView = itemView.findViewById(R.id.tv_msg)
    }

    class OtherViewHolder(chatView: View): RecyclerView.ViewHolder(chatView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvTime: TextView = itemView.findViewById(R.id.tv_time)
        var tvMsg: TextView = itemView.findViewById(R.id.tv_msg)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemViewType(position: Int): Int {
        val chat = listChat[position]

        // if sender is user, align right
        return if (chat.sender == user) {
            0
        }
        // if sender is other person, align left
        else {
            1
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            // if sender is user
            0 -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.row_your_chat,
                            parent,
                            false
                        )
                YourViewHolder(view)
            }
            // if sender is other
            1 -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.row_other_chat,
                            parent,
                            false
                        )
                OtherViewHolder(view)
            }

            else -> {
                val view: View = View(parent.context)
                EmptyViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = listChat.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = listChat[position]

        when (holder.itemViewType) {
            // if sender is user
            0 -> {
                val yourViewHolder = holder as ClientChatAdapter.YourViewHolder

                yourViewHolder.tvTime.text = setTime(chat.time)
                yourViewHolder.tvMsg.text = chat.msg
            }
            // if sender is other
            1 -> {
                val otherViewHolder = holder as ClientChatAdapter.OtherViewHolder

                otherViewHolder.tvName.text = chat.sender
                otherViewHolder.tvTime.text = setTime(chat.time)
                otherViewHolder.tvMsg.text = chat.msg
            }
        }
    }

    private fun setTime(time: DateTime): String {
        return "${if (time.hour < 10) "0${time.hour}" else time.hour}:${if (time.min < 10) "0${time.min}" else time.min}"
    }
}