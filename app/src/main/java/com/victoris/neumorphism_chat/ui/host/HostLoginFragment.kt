package com.victoris.neumorphism_chat.ui.host

// TODO: create room capacity input with numberPicker

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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.neumorphism.NeumorphButton

class HostLoginFragment : Fragment() {

    // binding
    private lateinit var binding: HostFragmentLoginBinding

    // widgets
    private lateinit var inputUname: EditText
    private lateinit var inputId: EditText
    private lateinit var inputKey: EditText
    private lateinit var btnCreate: NeumorphButton
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
        btnCreate = binding.btnCreate
        inputUname = binding.inputRoom
        inputId = binding.inputId
        inputKey = binding.inputKey
        btnUser1 = binding.tvChange1
        btnUser2 = binding.tvChange2

        // widget properties
        btnCreate.setOnClickListener{
            createRoom()
        }

        val extras = FragmentNavigatorExtras(
            binding.layoutPaus to "layout_paus_up",
            binding.tvAdmin to "admin_up",
            binding.tvUser to "user_down",
            binding.layoutInput to "layout_input_hide",
            binding.cardRoom to "input_uname_hide",
            binding.cardId to "input_id_hide",
            binding.cardKey to "input_key_hide",
            binding.btnCreate to "btn_create_hide",
            binding.tvChange1 to "admin1_hide",
            binding.tvChange2 to "admin2_hide",
            binding.btnAdmin to "admin_real_show",
            binding.rvRoomChat to "room_show"
        )
        val userClickListener = View.OnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.removeServerListener()
            }

            this.findNavController().navigate(
                R.id.action_hostLoginFragment_to_clientLoginFragment,
                null,
                null,
                extras
            )
        }
        btnUser1.setOnClickListener(userClickListener)
        btnUser2.setOnClickListener(userClickListener)
    }

    private fun createRoom() {
        if (inputUname.text.toString().isNotEmpty() &&
            inputId.text.toString().isNotEmpty() &&
            inputId.text.toString().toDoubleOrNull() != null &&
            inputKey.text.toString().isNotEmpty()
        ) {
            MainActivity.user = inputUname.text.toString()
            SERVER_KEY = inputId.text.toString().toInt()

            // create room in Firebase database
            viewModel.serverKeyCorrect(SERVER_KEY)

            // listen for VALID input
            val observer = object : Observer<Boolean> {
                override fun onChanged(loginSuccess: Boolean?) {
                    loginSuccess?.let {
                        if (it) {
                            // set the room password
                            viewModel.setPassword(inputKey.text.toString())

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
                .makeText(this.context, "Please enter text in email or room ID", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }
}