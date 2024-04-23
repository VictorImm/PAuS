package com.victoris.neumorphism_chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.victoris.neumorphism_chat.data.Chat
import com.victoris.neumorphism_chat.data.DateTime
import com.victoris.neumorphism_chat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityMainBinding

    // widgets
    private lateinit var navController: NavController

    // user
    companion object {
        var SERVER_KEY = -1
        var user: String = ""
        var listChat: ArrayList<Chat> = arrayListOf()
        const val databaseUrl = "https://paus-b5ca0-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}