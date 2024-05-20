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
import com.victoris.neumorphism_chat.databinding.MainFragmentSigninBinding
import com.victoris.neumorphism_chat.ui.UserActivity
import soup.neumorphism.NeumorphButton

class MainSigninFragment : Fragment() {

    // binding
    private lateinit var binding: MainFragmentSigninBinding

    // widgets
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var btnSignin: NeumorphButton
    private lateinit var btnLogin1: TextView
    private lateinit var btnLogin2: TextView

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
        binding = MainFragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // widget init
        btnSignin = binding.btnLogin
        inputEmail = binding.inputLogin
        inputPass = binding.inputPw
        btnLogin1 = binding.tvChange1
        btnLogin2 = binding.tvChange2

        // widget properties
        btnSignin.setOnClickListener {
            // TODO: sign in Firebase
            val intent = Intent(context, UserActivity::class.java)
            startActivity(intent)
        }
        val extras = FragmentNavigatorExtras(
            binding.tvLogin to "login_down",
            binding.tvSignin to "signin_up",
            binding.tvChange1 to "login"
        )
        btnLogin1.setOnClickListener {
            this.findNavController().navigate(
                R.id.action_mainSigninFragment_to_mainLoginFragment,
                null,
                null,
                extras
            )
        }
        btnLogin2.setOnClickListener {
            this.findNavController().navigate(
                R.id.action_mainSigninFragment_to_mainLoginFragment,
                null,
                null,
                extras
            )
        }
    }
}