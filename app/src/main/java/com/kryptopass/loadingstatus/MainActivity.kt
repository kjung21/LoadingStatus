package com.kryptopass.loadingstatus

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kryptopass.loadingstatus.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadId: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    // private lateinit var action: NotificationCompat.Action

    private var sourceMetadata: SourceMetadata? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        binding.layoutMain.buttonLoader.setOnClickListener {
            download()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            when (view.getId()) {
                binding.layoutMain.radioButtonGlideSelection.id -> {
                    sourceMetadata = SourceMetadata(
                        getString(R.string.glide),
                        getString(R.string.glide_title),
                        glide_url,
                        glide_zip,
                        getString(R.string.status_success)
                    )
                }

                binding.layoutMain.radioButtonStarterSelection.id -> {
                    sourceMetadata = SourceMetadata(
                        getString(R.string.starter),
                        getString(R.string.starter_title),
                        starter_url,
                        starter_zip,
                        getString(R.string.status_failed)
                    )
                }

                binding.layoutMain.radioButtonRetrofitSelection.id -> {
                    sourceMetadata = SourceMetadata(
                        getString(R.string.retrofit),
                        getString(R.string.retrofit_title),
                        retrofit_url,
                        retrofit_zip,
                        getString(R.string.status_success)
                    )
                }

                else -> createToast(getString(R.string.select_file))
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadId == id) {
                binding.layoutMain.buttonLoader.buttonState = ButtonState.Completed

                val contentIntent = Intent(applicationContext, DetailActivity::class.java)
                contentIntent.putExtra(FILE_TITLE, sourceMetadata?.fileTitle)
                contentIntent.putExtra(STATUS, sourceMetadata?.status)

                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    contentIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                createToast("Success, send notification: ${sourceMetadata?.fileTitle!!}")

                notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                notificationManager.sendNotification(sourceMetadata?.fileTitle!!, applicationContext, pendingIntent)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_title)
            notificationChannel.apply {
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        Timber.i(message)
    }

    private fun download() {
        if (!isNetworkAvailable()) {
            createToast(getString(R.string.no_internet))
        } else if (sourceMetadata == null || sourceMetadata?.repoUrl!!.isBlank()) {
            createToast(getString(R.string.select_file))
        } else {
            binding.layoutMain.buttonLoader.buttonState = ButtonState.Loading
            val request =
                DownloadManager.Request(Uri.parse(sourceMetadata?.repoZip))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setTitle(sourceMetadata?.fileName)
                    .setDescription(title)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "ZipFiles/${sourceMetadata?.fileName}.zip"
                    )
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)
            Timber.i("Download ID: $downloadId")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    }

    companion object {
        const val NOTIFICATION_ID = 0
        const val FILE_TITLE = "FILE_TITLE"
        const val STATUS = "STATUS"

        // NOTE: not translate-able so moved from strings resource
        private const val glide_url = "https://github.com/bumptech/glide"
        private const val glide_zip = "https://github.com/bumptech/glide/archive/master.zip"

        private const val retrofit_url = "https://github.com/square/retrofit"
        private const val retrofit_zip = "https://github.com/square/retrofit/archive/master.zip"

        private const val starter_url =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val starter_zip =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }
}