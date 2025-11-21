package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummaryActivity : AppCompatActivity() {

    private lateinit var dao: ActivityDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        dao = AppDatabase.getDatabase(this).activityDao()

        val date = intent.getStringExtra("date") ?: ""
        val dateText = findViewById<TextView>(R.id.dateText)
        val totalText = findViewById<TextView>(R.id.totalDurationText) // fixed

        dateText.text = "Date: $date"

        lifecycleScope.launch(Dispatchers.IO) {
            val total = dao.getTotalDuration(date) ?: 0
            withContext(Dispatchers.Main) {
                totalText.text = "Total Duration: $total minutes"
            }
        }
    }
}
