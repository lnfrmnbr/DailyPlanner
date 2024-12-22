package com.example.dailyplanner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import android.widget.ScrollView
import android.widget.TableRow
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
//                db.taskListDao().deleteAllTaskLists()
//                val newTask = Task(dateStart = "2024-12-22 12:22:22", dateFinish = "2024-12-22 15:23:23", name = "Спать", description = "Крепко спать")
//                db.taskListDao().addTaskToList(newTask)
//                val newTask2 = Task(dateStart = "2024-12-22 13:22:22", dateFinish = "2024-12-22 14:23:23", name = "Играть в комп", description = "Добить 40 ранг")
//                db.taskListDao().addTaskToList(newTask2)
                Log.e("VIK", "${db.taskListDao().getAllTaskLists()}")
                val tasksList = TaskListTypeConverter().toTaskList(
                    db.taskListDao().getAllTaskLists()[0].tasksJson
                ).filter {
                    stringToTimestamp(it.dateStart)?.getDate() == calendar.getSelectedDates()
                        .first().getDate()
                }
                val splitTaskList = splitTasksByHour(tasksList)
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
            val calendar = Calendar.getInstance().apply { time = startDate }

            while (calendar.time <= finishDate) {
                val hourKey = calendar.get(Calendar.HOUR_OF_DAY)
                tasksByHour.computeIfAbsent(hourKey) { mutableListOf() }.add(task)
                calendar.add(Calendar.HOUR_OF_DAY, 1)
            }
        }

        return (0..23).map { hour ->
            TimeBox(time = hour, taskList = tasksByHour[hour] ?: mutableListOf())
        }
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
            override fun whenWeekMonthYearChanged(
                weekNumber: String,
                monthNumber: String,
                monthName: String,
                year: String,
                date: Date
            ) {
                super.whenWeekMonthYearChanged(weekNumber, monthNumber, monthName, year, date)
            }

            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
                fetchTasks()
            }

            override fun whenCalendarScrolled(dx: Int, dy: Int) {
                super.whenCalendarScrolled(dx, dy)
            }

            override fun whenSelectionRestored() {
                super.whenSelectionRestored()
            }

            override fun whenSelectionRefreshed() {
                super.whenSelectionRefreshed()
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