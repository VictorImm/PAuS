package com.victoris.neumorphism_chat.ui.main

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.victoris.neumorphism_chat.R
import com.victoris.neumorphism_chat.databinding.MainFragmentLoginBinding
import com.victoris.neumorphism_chat.ui.UserActivity
import soup.neumorphism.NeumorphButton

class MainLoginFragment : Fragment() {

    // binding
    private lateinit var binding: MainFragmentLoginBinding

    // widgets
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var btnLogin: NeumorphButton
    private lateinit var btnSignin1: TextView
    private lateinit var btnSignin2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater
            .from(requireContext())
            .inflateTransition(R.transition.transition_switch)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = MainFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // widget init
        btnLogin = binding.btnLogin
        inputEmail = binding.inputLogin
        inputPass = binding.inputPw
        btnSignin1 = binding.tvChange1
        btnSignin2 = binding.tvChange2

        // widget properties
        btnLogin.setOnClickListener {
            // TODO: Buat login auth
            val intent = Intent(context, UserActivity::class.java)
            startActivity(intent)
        }
        val extras = FragmentNavigatorExtras(
            binding.tvLogin to "login_up",
            binding.tvSignin to "signin_down",
            binding.tvChange1 to "signin"

        )
        btnSignin1.setOnClickListener {
            this.findNavController().navigate(
                R.id.action_mainLoginFragment_to_mainSigninFragment,
                null,
                null,
                extras
            )
        }
        btnSignin2.setOnClickListener {
            this.findNavController().navigate(
                R.id.action_mainLoginFragment_to_mainSigninFragment,
                null,
                null,
                extras
            )
        }
    }
}