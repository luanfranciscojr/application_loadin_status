package com.udacity

import android.app.DownloadManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val fileName = findViewById<TextView>(R.id.fileName)
        fileName.text = intent.extras?.getString(getString(R.string.file_name_key), " File Name")
        val status = findViewById<TextView>(R.id.status)
        status.text = when (intent.extras?.getInt(getString(R.string.status_key), 0)) {
            DownloadManager.STATUS_FAILED -> getString(R.string.status_fail)
            DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.status_success)
            else -> getString(R.string.status_fail)
        }

    }

    fun finish(view: View) {
        finish()
    }

}
