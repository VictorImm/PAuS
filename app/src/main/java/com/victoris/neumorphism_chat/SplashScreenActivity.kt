package com.victoris.neumorphism_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.victoris.neumorphism_chat.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var tvPaus: TextView
    private lateinit var tvPausAcronym: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        tvPaus = binding.tvPaus
        tvPausAcronym = binding.tvPausAcronym

        tvPaus.alpha = 0f
        tvPausAcronym.alpha = 0f

        tvPaus.animate().setDuration(1500).alpha(1f).withEndAction {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        tvPausAcronym.animate().setDuration(1500).alpha(1f)

    }
}