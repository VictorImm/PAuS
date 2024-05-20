package com.victoris.neumorphism_chat.ui.client.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.user
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.data.RoomChat

class ClientRoomChatAdapter(
    private val listRoomChat: List<RoomChat>,
    private val context: Context
): RecyclerView.Adapter<ClientRoomChatAdapter.ListViewHolder>() {

    class ListViewHolder(chatRoomView: View): RecyclerView.ViewHolder(chatRoomView) {
        val id: TextView = itemView.findViewById(R.id.tv_id)
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val capacity: TextView = itemView.findViewById(R.id.tv_cap)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.row_chat_room,
                    parent,
                    false
                )
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listRoomChat.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val roomChat = listRoomChat[position]

        holder.id.text = roomChat.id.toString()
        holder.name.text = "${roomChat.owner}'s room"
        // TODO: use live data for current capacity from database
        holder.capacity.text = "2/${roomChat.capacity}"

        holder.itemView.setOnClickListener {
            // TODO: Add dialog to input username and room pw

//            user = inputUname.text.toString()
//            SERVER_KEY = inputKey.text.toString().toInt()

            // create room in Firebase database
            // serverKeyCorrect()
        }
    }

}