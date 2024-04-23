package com.victoris.neumorphism_chat.host

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.victoris.neumorphism_chat.MainActivity
import com.victoris.neumorphism_chat.MainActivity.Companion.SERVER_KEY
import com.victoris.neumorphism_chat.MainActivity.Companion.databaseUrl
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.databinding.HostFragmentLoginBinding
import com.victoris.neumorphism_chat.viewmodel.HostLoginViewModel
import com.victoris.neumorphism_chat.viewmodel.HostLoginViewModelFactory
import soup.neumorphism.NeumorphButton

class HostLoginFragment : Fragment() {

    // binding
    private lateinit var binding: HostFragmentLoginBinding

    // widgets
    private lateinit var inputUname: EditText
    private lateinit var inputKey: EditText
    private lateinit var btnLogin: NeumorphButton
    private lateinit var btnUser1: TextView
    private lateinit var btnUser2: TextView

    // viewModel
    private val viewModel: HostLoginViewModel by viewModels {
        HostLoginViewModelFactory(requireContext())
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
        binding = HostFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // widget init
        btnLogin = binding.btnLogin
        inputUname = binding.inputLogin
        inputKey = binding.inputKey
        btnUser1 = binding.tvChange1
        btnUser2 = binding.tvChange2

        // widget properties
        btnLogin.setOnClickListener{
            logIn()
        }

        val extras = FragmentNavigatorExtras(
            binding.tvAdmin to "admin_up",
            binding.tvUser to "user_down",
            binding.tvChange1 to "user"
        )
        btnUser1.setOnClickListener {
            viewModel.removeServerListener()

            this.findNavController().navigate(
                R.id.action_hostLoginFragment_to_clientLoginFragment,
                null,
                null,
                extras
            )
        }
        btnUser2.setOnClickListener {
            viewModel.removeServerListener()

            this.findNavController().navigate(
                R.id.action_hostLoginFragment_to_clientLoginFragment,
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
            MainActivity.user = inputUname.text.toString()
            SERVER_KEY = inputKey.text.toString().toInt()

            // create room in Firebase database
            viewModel.serverKeyCorrect(SERVER_KEY)

            // listen for VALID input
            val observer = object : Observer<Boolean> {
                override fun onChanged(loginSuccess: Boolean?) {
                    loginSuccess?.let {
                        if (it) {
                            // reset loginSuccess value to false agar tidak melakukan navigasi ulang saat fragment di-recreate
                            viewModel.resetLoginSuccess()

                            // remove observer if no use more
                            viewModel.loginSuccess.removeObserver(this)

                            val intent = Intent(context, HostChatRoomActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }
            viewModel.loginSuccess.observe(viewLifecycleOwner, observer)
        } else {
            Toast
                .makeText(this.context, "Please enter text in email or room key", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }
}