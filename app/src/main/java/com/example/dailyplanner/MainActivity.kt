package com.example.dailyplanner

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import java.util.Date
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import android.widget.TimePicker
import com.google.android.material.textfield.TextInputEditText
import java.sql.Timestamp

import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: TimeBoxAdapter

    private val times = mutableListOf(
        TimeBox(0),
        TimeBox(1),
        TimeBox(2),
        TimeBox(3),
        TimeBox(4),
        TimeBox(5),
        TimeBox(6),
        TimeBox(7),
        TimeBox(8),
        TimeBox(9),
        TimeBox(10),
        TimeBox(11),
        TimeBox(12),
        TimeBox(13),
        TimeBox(14),
        TimeBox(15),
        TimeBox(16),
        TimeBox(17),
        TimeBox(18),
        TimeBox(19),
        TimeBox(20),
        TimeBox(21),
        TimeBox(22),
        TimeBox(23)
    )
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendar: SingleRowCalendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerViewTimes)
        calendar = findViewById(R.id.main_single_row_calendar)

        setupCalendar()

        db = AppDatabase.getDatabase(this)

        adapter = TimeBoxAdapter(times = times, context = this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        fetchTasks()

        findViewById<Button>(R.id.add).setOnClickListener{
            showAddTaskDialog()
        }
    }


    private fun stringToTimestamp(dateString: String): Timestamp? {
        return try {
            val format = "yyyy-MM-dd HH:mm:ss"

            val dateFormat = SimpleDateFormat(format, Locale.getDefault())

            val date: Date = dateFormat.parse(dateString) ?: return null

            Timestamp(date.time)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchTasks() {

        lifecycleScope.launch {
            try {
                Log.e("VIK", "${db.taskListDao().getAllTaskLists()}")
                val tasksList = TaskListTypeConverter().toTaskList(
                    db.taskListDao().getAllTaskLists()[0].tasksJson
                ).filter {
                    calendar.getSelectedDates().first() >= stringToTimestamp(it.dateStart)!! &&
                            calendar.getSelectedDates().first() <= stringToTimestamp(it.dateFinish)!!
                }
                val splitTaskList = splitTasksByHour(tasksList)
                Log.e("VIK", "$splitTaskList")
                adapter.setTimes(splitTaskList)
            } catch (e: Exception) {
            }
        }
    }

    private fun splitTasksByHour(tasks: List<Task>): List<TimeBox> {
        val tasksByHour = mutableMapOf<Int, MutableList<Task>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (task in tasks) {
            val startDate = dateFormat.parse(task.dateStart) ?: continue
            val finishDate = dateFormat.parse(task.dateFinish) ?: continue

            val startOfDay = Calendar.getInstance().apply {
                time = calendar.getSelectedDates().first()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            val endOfDay = Calendar.getInstance().apply {
                time = calendar.getSelectedDates().first()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            val calendarDop = Calendar.getInstance().apply { time = startOfDay }
            Log.e("VIK", "$endOfDay")
            while (calendarDop.time <= endOfDay) {
                val hourKey = calendarDop.get(Calendar.HOUR_OF_DAY)
                Log.e("VIK", "$startDate $finishDate ${calendarDop.time}")

                if ((calendarDop.time >= startDate)){
                    tasksByHour.computeIfAbsent(hourKey) { mutableListOf() }.add(task)
                }

                calendarDop.add(Calendar.HOUR_OF_DAY, 1)
            }
        }

        return (0..23).map { hour ->
            TimeBox(time = hour, taskList = tasksByHour[hour]?.distinct()?.toMutableList() ?: mutableListOf())
        }
    }



    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_task, null)

        val nameInput = dialogLayout.findViewById<TextInputEditText>(R.id.name)
        val descrInput = dialogLayout.findViewById<TextInputEditText>(R.id.descr)
        val dateStartInput = dialogLayout.findViewById<DatePicker>(R.id.datePickerStart)
        val timeStartInput = dialogLayout.findViewById<TimePicker>(R.id.timePickerStart)

        val timeFinishInput = dialogLayout.findViewById<TextInputEditText>(R.id.timeFinish)

        val sdfDateStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfTimeStart = SimpleDateFormat("HH:mm", Locale.getDefault())


        builder.setTitle("Добавить дело")
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val descr = descrInput.text.toString()
                var calendar = Calendar.getInstance()
                var selectedDate = Calendar.getInstance()
                dateStartInput.init(
                    dateStartInput.year,
                    dateStartInput.month,
                    dateStartInput.dayOfMonth
                ) { _, year, month, dayOfMonth ->
                    selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                }

                timeStartInput.setOnTimeChangedListener { _, hourOfDay, minute ->
                    calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }

                }
                val dateStart = sdfDateStart.format(selectedDate.time)
                val timeStart = sdfTimeStart.format(calendar.time)
                val timeFinish = timeFinishInput.text.toString()

                val newTask = Task(name = name, description = descr, dateStart = "$dateStart $timeStart:00", dateFinish =  "2024-12-23 23:22:12")

                lifecycleScope.launch {
                    try {
                        db.taskListDao().addTaskToList(newTask)
                        fetchTasks()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }


    private fun setupCalendar() {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                return R.layout.calendar_item_layout
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.findViewById<TextView>(R.id.date_text_view).text =
                    SimpleDateFormat("dd", Locale.getDefault()).format(date)
                val card = holder.itemView.findViewById<MaterialCardView>(R.id.item_card)
                if (position == 10 && !isSelected) {
                    card.strokeColor = Color.BLACK
                }
                if (isSelected) {
                    card.setCardBackgroundColor(Color.GREEN)
                } else {
                    card.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
                fetchTasks()
            }
        }

        calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 10
            pastDaysCount = 10
            includeCurrentDate = true
            init()
        }
        calendar.setItemsSelected(listOf(10), true)
    }
}
