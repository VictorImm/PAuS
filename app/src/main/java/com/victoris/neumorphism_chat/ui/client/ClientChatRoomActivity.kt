package com.victoris.neumorphism_chat.ui.client

// TODO: Separate business operation to viewModel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.listChat
import com.victoris.neumorphism_chat.MainActivity.Companion.user
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.ui.client.adapter.ClientChatAdapter
import com.victoris.neumorphism_chat.data.Chat
import com.victoris.neumorphism_chat.data.DateTime
import com.victoris.neumorphism_chat.databinding.ClientActivityChatRoomBinding
import com.victoris.neumorphism_chat.ui.host.adapter.HostChatAdapter
import soup.neumorphism.NeumorphFloatingActionButton
import java.time.LocalTime
import kotlin.reflect.jvm.internal.impl.types.AbstractTypeCheckerContext.SupertypesPolicy.None

class ClientChatRoomActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ClientActivityChatRoomBinding

    // widgets
    private lateinit var btnBack: ImageView
    private lateinit var tvRoom: TextView
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
            // clientChatAdapter.updateChatList()
            showRecyclerList(listChat)
            scrollToBottom(rvChat)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {
            // TODO: set listener ke chat, jika host left, maka end activity
            // beri dialog

            listChat = arrayListOf()
            finish()
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }
    private val roomListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (uname in snapshot.children) {
                tvRoom.text = "${uname.getValue(String::class.java)}'s Room - $SERVER_KEY"

                removeRoomListener()
                break
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }
    private val userListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (uname in snapshot.children) {
                if (uname.getValue(String::class.java) == user) {
                    // TODO: add confirmation dialogue

                    uname.ref.removeValue()

                    listChat = arrayListOf()

                    // remove all listener
                    removeChatListener()
                    removeUserListener()

                    break
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}

    }

    // data
    private lateinit var clientChatAdapter: ClientChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ClientActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // cache database
        listenForMessage()

        supportActionBar?.hide()

        // widget init
        tvRoom = binding.tvRoomOwner
        btnBack = binding.btnBack
        rvChat = binding.rvChat
        inputMsg = binding.inputMsg
        btnMsg = binding.btnMsg

        // widget properties
        setRoomName()
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

        // TODO: jika end activity/left, maka delete user dalam database
    }

    private fun showRecyclerList(chats: List<Chat>) {
        rvChat.layoutManager = LinearLayoutManager(applicationContext)

        // show recycle view from list of chat
        clientChatAdapter = ClientChatAdapter(user, chats)
        rvChat.adapter = clientChatAdapter
    }

    private fun scrollToBottom(recyclerView: RecyclerView) {
        recyclerView.smoothScrollToPosition(clientChatAdapter.itemCount - 1)
    }

    private fun getTimeNow(): LocalTime {
        return LocalTime.now()
    }

    private fun sendChatToServer() {
        val refMsg = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/${SERVER_KEY}/messages")
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
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/messages")

        ref.addChildEventListener(chatListener)
    }

    private fun setRoomName() {
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/list-user")

        ref.addValueEventListener(roomListener)
    }

    private fun removeChatListener() {
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/messages")

        ref.removeEventListener(chatListener)
    }

    private fun removeRoomListener() {
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/list-user")

        ref.removeEventListener(roomListener)
    }

    private fun removeUserListener() {
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/list-user")

        ref.removeEventListener(userListener)
    }


    override fun onDestroy() {
        super.onDestroy()

        // remove user in the list
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/$SERVER_KEY/list-user")
        ref.addValueEventListener(userListener)
    }
}