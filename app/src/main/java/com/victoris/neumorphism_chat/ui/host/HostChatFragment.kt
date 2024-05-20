package com.victoris.neumorphism_chat.ui.host

// TODO: Separate business operation to viewModel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.data.Chat
import com.victoris.neumorphism_chat.data.DateTime
import com.victoris.neumorphism_chat.databinding.HostFragmentChatBinding
import com.victoris.neumorphism_chat.ui.host.adapter.HostChatAdapter
import soup.neumorphism.NeumorphFloatingActionButton
import java.time.LocalTime

class HostChatFragment : Fragment() {

    // binding
    private lateinit var binding: HostFragmentChatBinding

    // widgets
    private lateinit var btnBack: ImageView
    private lateinit var btnUser: ImageView
    private lateinit var rvChat: RecyclerView
    private lateinit var inputMsg: EditText
    private lateinit var btnMsg: NeumorphFloatingActionButton

    // listener
    private val chatListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // add msg to main cache
            val chat = snapshot.getValue(Chat::class.java)

            if (chat != null) {
                MainActivity.listChat.add(chat)
            }

            // renew the recyclerview
            // hostChatAdapter.updateChatList()
            showRecyclerList(MainActivity.listChat)
            scrollToBottom(rvChat)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    // data
    private lateinit var hostChatAdapter: HostChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = HostFragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // cache database
        listenForMessage()

        // widget init
        btnBack = binding.btnBack
        rvChat = binding.rvChat
        inputMsg = binding.inputMsg
        btnMsg = binding.btnMsg

        // widget properties
        btnBack.setOnClickListener {
            requireActivity().finish()
        }
        rvChat.setHasFixedSize(true)
        showRecyclerList(MainActivity.listChat)

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
        rvChat.layoutManager = LinearLayoutManager(requireContext())

        // show recycle view from list of chat
        hostChatAdapter = HostChatAdapter(MainActivity.user, chats)
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
            .getInstance(MainActivity.databaseUrl)
            .getReference("/${MainActivity.SERVER_KEY}/messages")
            .push()

        val timeNow = getTimeNow()
        val chat = Chat(
            id = refMsg.key!!,
            msg = inputMsg.text.toString(),
            sender = MainActivity.user,
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
            .getReference("/${MainActivity.SERVER_KEY}/messages")

        ref.addChildEventListener(chatListener)
    }

    private fun removeChatListener() {
        val ref = FirebaseDatabase
            .getInstance(MainActivity.databaseUrl)
            .getReference("/${MainActivity.SERVER_KEY}/messages")

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

        MainActivity.listChat = arrayListOf()
        MainActivity.user = ""
    }
}