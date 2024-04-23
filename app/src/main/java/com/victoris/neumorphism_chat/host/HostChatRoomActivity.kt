package com.victoris.neumorphism_chat.host

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.databaseUrl
import com.victoris.neumorphism_chat.MainActivity.Companion.listChat
import com.victoris.neumorphism_chat.MainActivity.Companion.user
import com.victoris.neumorphism_chat.host.adapter.HostChatAdapter
import com.victoris.neumorphism_chat.data.Chat
import com.victoris.neumorphism_chat.data.DateTime
import com.victoris.neumorphism_chat.databinding.HostActivityChatRoomBinding
import soup.neumorphism.NeumorphFloatingActionButton
import java.time.LocalTime

class HostChatRoomActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: HostActivityChatRoomBinding

    // widgets
    private lateinit var btnBack: NeumorphFloatingActionButton
    private lateinit var rvChat: RecyclerView
    private lateinit var inputMsg: EditText
    private lateinit var btnMsg: NeumorphFloatingActionButton

    // listener
    private val chatListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // add msg to main cache
            val chat = snapshot.getValue(Chat::class.java)

            if (chat != null) {
                listChat.add(chat)
            }

            // renew the recyclerview
            // hostChatAdapter.updateChatList()
            showRecyclerList(listChat)
            scrollToBottom(rvChat)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    // data
    private lateinit var hostChatAdapter: HostChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: create list of user, pakai side menu

        binding = HostActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // cache database
        listenForMessage()

        supportActionBar?.hide()

        // widget init
        btnBack = binding.btnBack
        rvChat = binding.rvChat
        inputMsg = binding.inputMsg
        btnMsg = binding.btnMsg

        // widget properties
        btnBack.setOnClickListener {
            finish()
        }
        rvChat.setHasFixedSize(true)
        showRecyclerList(listChat)

        btnMsg.setOnClickListener {
            if (inputMsg.text.toString() != "") {
                // send msg to server AHAYYY
                sendChatToServer()

                // listen msg
                // listenForMessage()

                // reset textinput
                inputMsg.setText("")
            }
        }
    }

    private fun showRecyclerList(chats: List<Chat>) {
        rvChat.layoutManager = LinearLayoutManager(applicationContext)

        // show recycle view from list of chat
        hostChatAdapter = HostChatAdapter(user, chats)
        rvChat.adapter = hostChatAdapter
    }

    private fun scrollToBottom(recyclerView: RecyclerView) {
        recyclerView.smoothScrollToPosition(hostChatAdapter.itemCount - 1)
    }

    private fun getTimeNow(): LocalTime {
        return LocalTime.now()
    }

    private fun sendChatToServer() {
        val refMsg = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/messages")
            .push()

        val timeNow = getTimeNow()
        val chat = Chat(
            id = refMsg.key!!,
            msg = inputMsg.text.toString(),
            sender = user,
            time = DateTime(
                hour = timeNow.hour,
                min = timeNow.minute
            )
        )
        refMsg.setValue(chat)
    }

    private fun listenForMessage() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/messages")

        ref.addChildEventListener(chatListener)
    }

    private fun removeChatListener() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/messages")

        ref.removeEventListener(chatListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: Kasih dialog terlebih dahulu, apakah ingin hapus chat atau meninggalkan saja
//        var ref = FirebaseDatabase
//            .getInstance(databaseUrl)
//            .getReference("/$SERVER_KEY")
//
//        ref.removeValue()

        removeChatListener()

        listChat = arrayListOf()
        user = ""
    }

}