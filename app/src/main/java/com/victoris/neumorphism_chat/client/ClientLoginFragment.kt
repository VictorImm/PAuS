package com.victoris.neumorphism_chat.client

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.databaseUrl
import com.victoris.neumorphism_chat.MainActivity.Companion.user
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.databinding.ClientFragmentLoginBinding
import com.victoris.neumorphism_chat.host.HostChatRoomActivity
import soup.neumorphism.NeumorphButton

class ClientLoginFragment : Fragment() {

    // binding
    private lateinit var binding: ClientFragmentLoginBinding

    // widgets
    private lateinit var inputUname: EditText
    private lateinit var inputKey: EditText
    private lateinit var btnLogin: NeumorphButton
    private lateinit var btnAdmin1: TextView
    private lateinit var btnAdmin2: TextView

    // listener
    private val serverListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                // SERVER_KEY valid
                userAvailable()
            } else {
                // SERVER_KEY tidak valid, berikan pesan kesalahan kepada pengguna
                Toast
                    .makeText(this@ClientLoginFragment.context, "Invalid Server Key!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    }
    private val userListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                // username valid
                var count = 0
                for (uname in snapshot.children) {
                    if (uname.getValue(String::class.java) == "$user-$SERVER_KEY") {
                        // server key found
                        count = 1
                    }
                }

                // username not found
                if (count == 0) {
                    val ref = FirebaseDatabase
                        .getInstance(databaseUrl)
                        .getReference("/$SERVER_KEY/list-user")
                        .push()
                    ref.setValue(user)

                    val intent = Intent(context, ClientChatRoomActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast
                        .makeText(this@ClientLoginFragment.context, "Username Already Exist!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // username tidak valid, berikan pesan kesalahan kepada pengguna
                Toast
                    .makeText(this@ClientLoginFragment.context, "Error Username Creation!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(R.transition.transition_switch)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ClientFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // widget init
        btnLogin = binding.btnLogin
        inputUname = binding.inputLogin
        inputKey = binding.inputKey
        btnAdmin1 = binding.tvChange1
        btnAdmin2 = binding.tvChange2

        // widget properties
        btnLogin.setOnClickListener{
            logIn()
        }

        val extras = FragmentNavigatorExtras(
            binding.tvAdmin to "admin_down",
            binding.tvUser to "user_up",
            binding.tvChange1 to "admin"
        )
        btnAdmin1.setOnClickListener {
            removeUserListener()
            removeServerListener()

            this.findNavController().navigate(
                R.id.action_clientLoginFragment_to_hostLoginFragment,
                null,
                null,
                extras
            )
        }
        btnAdmin2.setOnClickListener {
            removeUserListener()
            removeServerListener()

            this.findNavController().navigate(
                R.id.action_clientLoginFragment_to_hostLoginFragment,
                null,
                null,
                extras
            )
        }
    }

    private fun logIn() {
        if (inputUname.text.toString() != "" &&
            inputKey.text.toString() != "" &&
            inputKey.text.toString().toDoubleOrNull() != null
        ) {
            user = inputUname.text.toString()
            SERVER_KEY = inputKey.text.toString().toInt()

            // create room in Firebase database
            serverKeyCorrect()
        } else {
            Toast
                .makeText(this.context, "Please enter text in email or room key", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }

    private fun serverKeyCorrect() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY")

        ref.addListenerForSingleValueEvent(serverListener)
    }

    private fun userAvailable() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/list-user")

        ref.addListenerForSingleValueEvent(userListener)
    }

    private fun removeServerListener() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY")

        ref.removeEventListener(serverListener)
        SERVER_KEY = -1
    }

    private fun removeUserListener() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/list-user")

        ref.removeEventListener(userListener)
        user = ""
    }
}