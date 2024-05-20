package com.victoris.neumorphism_chat.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.databaseUrl
import com.victoris.neumorphism_chat.MainActivity.Companion.user
import com.victoris.neumorphism_chat.ui.host.HostChatRoomActivity

class HostLoginViewModel(context: Context): ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess
    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }

    // define listener
    private val serverListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                // SERVER_KEY valid
                val ref = FirebaseDatabase
                    .getInstance(databaseUrl)
                    .getReference("/$SERVER_KEY/list-user")
                    .push()
                ref.setValue(user)

                _loginSuccess.value = true
            } else {
                // SERVER_KEY tidak valid
                _loginSuccess.value = false

                // SERVER_KEY tidak valid, berikan pesan kesalahan kepada pengguna
                Toast
                    .makeText(context, "Invalid Server Key!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    // listener remover
    fun removeServerListener() {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY")

        ref.removeEventListener(serverListener)
        SERVER_KEY = -1
    }

    // function to check if the server key is VALID
    fun serverKeyCorrect(serverKey: Int) {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$serverKey")

        ref.addListenerForSingleValueEvent(serverListener)
    }

    fun setPassword(pass: String) {
        val ref = FirebaseDatabase
            .getInstance(databaseUrl)
            .getReference("/$SERVER_KEY/password")
        ref.setValue(pass)
    }
}

class HostLoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HostLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HostLoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}