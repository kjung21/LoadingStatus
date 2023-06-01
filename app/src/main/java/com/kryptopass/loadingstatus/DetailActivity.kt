package com.kryptopass.loadingstatus

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kryptopass.loadingstatus.MainActivity.Companion.FILE_TITLE
import com.kryptopass.loadingstatus.MainActivity.Companion.STATUS
import com.kryptopass.loadingstatus.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        val fileName = intent.getStringExtra(FILE_TITLE)
        val status = intent.getStringExtra(STATUS)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()

        binding.layoutDetail.fileTitleValue.text = fileName
        binding.layoutDetail.statusValue.text = status

        when (status) {
            "Success" -> {
                binding.layoutDetail.statusValue.setTextColor(Color.GREEN)
            }

            "Failed" -> {
                binding.layoutDetail.statusValue.setTextColor(Color.RED)
            }
        }

        binding.layoutDetail.detailButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
