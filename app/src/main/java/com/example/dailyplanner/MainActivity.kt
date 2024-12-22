package com.example.dailyplanner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context.MODE_PRIVATE
import android.widget.ScrollView
import android.widget.TableRow
import kotlinx.coroutines.withContext
import java.sql.Timestamp

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView1: RecyclerView

    private lateinit var adapter0: TasksAdapter
//    private lateinit var adapter1: TasksAdapter
//    private lateinit var adapter2: TasksAdapter
//    private lateinit var adapter3: TasksAdapter
//    private lateinit var adapter4: TasksAdapter
//    private lateinit var adapter5: TasksAdapter
//    private lateinit var adapter6: TasksAdapter
//    private lateinit var adapter7: TasksAdapter
//    private lateinit var adapter8: TasksAdapter
//    private lateinit var adapter9: TasksAdapter
//    private lateinit var adapter10: TasksAdapter
//    private lateinit var adapter11: TasksAdapter
//    private lateinit var adapter12: TasksAdapter
//    private lateinit var adapter13: TasksAdapter
//    private lateinit var adapter14: TasksAdapter
//    private lateinit var adapter15: TasksAdapter
//    private lateinit var adapter16: TasksAdapter
//    private lateinit var adapter17: TasksAdapter
//    private lateinit var adapter18: TasksAdapter
//    private lateinit var adapter19: TasksAdapter
//    private lateinit var adapter20: TasksAdapter
//    private lateinit var adapter21: TasksAdapter
//    private lateinit var adapter22: TasksAdapter
//    private lateinit var adapter23: TasksAdapter

    private val tasks = mutableListOf<Task>()
    private lateinit var db: AppDatabase
    private lateinit var scrollView: ScrollView
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
        scrollView = findViewById(R.id.scrollView)
        calendar = findViewById(R.id.main_single_row_calendar)

        setupCalendar()

        scrollView.post {
            scrollView.scrollTo( 0,  findViewById<TableRow>(R.id.row8).top)
        }
        db = AppDatabase.getDatabase(this)

        recyclerView1 = findViewById(R.id.tasks0)
        adapter0 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
        recyclerView1.adapter = adapter0
        recyclerView1.layoutManager = LinearLayoutManager(this)

        fetchTasks()

    }

    fun stringToTimestamp(dateString: String): Timestamp? {
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
                //val newTask = Task(dateStart = "2024-12-21 22:22:22", dateFinish = "2024-12-21 23:23:23", name = "ДЗ", description = "Матем, русский")
                //db.taskDao().insert(newTask)
                val tasksList = db.taskDao().getAllTasks()
                adapter0.setTasks(tasksList.filter {
                    stringToTimestamp(it.dateStart)?.getDate() == calendar.getSelectedDates().first().getDate()
                })
                tasksList.map { Log.e("VIK", "${stringToTimestamp(it.dateStart)?.getTime()}")
                    Log.e("VIK", "${System.currentTimeMillis()}")
                    stringToTimestamp(it.dateStart)?.getTime()!! < System.currentTimeMillis()
                    }
            } catch (e: Exception) {
            }
        }
    }

    private fun onEditTask(task: Task) {

    }

    private fun onDeleteTask(id: Long) {

    }

    private fun setupCalendar(){
        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(position: Int, date: Date, isSelected: Boolean): Int {
                return R.layout.calendar_item_layout
            }

            override fun bindDataToCalendarView(holder: SingleRowCalendarAdapter.CalendarViewHolder, date: Date, position: Int, isSelected: Boolean) {
                holder.itemView.findViewById<TextView>(R.id.date_text_view).text = SimpleDateFormat("dd", Locale.getDefault()).format(date)
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
            override fun whenWeekMonthYearChanged(weekNumber: String, monthNumber: String, monthName: String, year: String, date: Date) {
                super.whenWeekMonthYearChanged(weekNumber, monthNumber, monthName, year, date)
            }

            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
                //loadTasksForDate(date)
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