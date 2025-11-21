package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var dao: ActivityDao

    private lateinit var inputName: EditText
    private lateinit var inputDuration: EditText
    private lateinit var pickDateButton: Button
    private lateinit var selectedDateText: TextView
    private lateinit var saveButton: Button
    private lateinit var filterButton: Button
    private lateinit var showAllButton: Button
    private lateinit var listView: ListView

    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        dao = database.activityDao()

        inputName = findViewById(R.id.inputName)
        inputDuration = findViewById(R.id.inputDuration)
        selectedDateText = findViewById(R.id.calendarView)
        saveButton = findViewById(R.id.btnSave)
        filterButton = findViewById(R.id.btnFilter)
        showAllButton = findViewById(R.id.btnShowAll)
        listView = findViewById(R.id.listView)

        selectedDateText.setOnClickListener { showDatePicker() }
        saveButton.setOnClickListener { saveActivity() }
        filterButton.setOnClickListener { filterActivities() }
        showAllButton.setOnClickListener { showAllActivities() }

        showAllActivities()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(this,
            { _, year, month, day ->
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                calendar.set(year, month, day)
                selectedDate = fmt.format(calendar.time)
                selectedDateText.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun saveActivity() {
        val name = inputName.text.toString()
        val duration = inputDuration.text.toString().toIntOrNull()

        if (name.isEmpty() || duration == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val activity = ActivityEntity(activityName = name, duration = duration, date = selectedDate)
        lifecycleScope.launch(Dispatchers.IO) {
            dao.insertActivity(activity)
            launch(Dispatchers.Main) { showAllActivities() }
        }
    }

    private fun filterActivities() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Pick a date first", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val activities = dao.getActivitiesByDate(selectedDate)
            launch(Dispatchers.Main) { updateList(activities) }
        }
    }

    private fun showAllActivities() {
        lifecycleScope.launch(Dispatchers.IO) {
            val activities = dao.getAllActivities()
            launch(Dispatchers.Main) { updateList(activities) }
        }
    }

    private fun updateList(activities: List<ActivityEntity>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            activities.map { "${it.activityName} - ${it.duration} min - ${it.date}" })
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val activityDate = activities[position].date
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra("date", activityDate)
            startActivity(intent)
        }
    }
}
