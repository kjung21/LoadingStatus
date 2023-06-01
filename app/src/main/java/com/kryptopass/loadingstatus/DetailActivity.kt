package com.kryptopass.loadingstatus

import android.app.NotificationManager
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
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        val fileName = intent.getStringExtra(FILE_TITLE)
        val status = intent.getStringExtra(STATUS)

        binding.layoutDetail.fileTitleValue.text = fileName
        binding.layoutDetail.statusValue.text = status

        binding.layoutDetail.detailButton.setOnClickListener {
            finish()
        }
    }
}
