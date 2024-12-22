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
    private lateinit var recyclerView0: RecyclerView
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    private lateinit var recyclerView4: RecyclerView
    private lateinit var recyclerView5: RecyclerView
    private lateinit var recyclerView6: RecyclerView
    private lateinit var recyclerView7: RecyclerView
    private lateinit var recyclerView8: RecyclerView
    private lateinit var recyclerView9: RecyclerView
    private lateinit var recyclerView10: RecyclerView
    private lateinit var recyclerView11: RecyclerView
    private lateinit var recyclerView12: RecyclerView
    private lateinit var recyclerView13: RecyclerView
    private lateinit var recyclerView14: RecyclerView
    private lateinit var recyclerView15: RecyclerView
    private lateinit var recyclerView16: RecyclerView
    private lateinit var recyclerView17: RecyclerView
    private lateinit var recyclerView18: RecyclerView
    private lateinit var recyclerView19: RecyclerView
    private lateinit var recyclerView20: RecyclerView
    private lateinit var recyclerView21: RecyclerView
    private lateinit var recyclerView22: RecyclerView


    private lateinit var adapter0: TasksAdapter
    private lateinit var adapter1: TasksAdapter
    private lateinit var adapter2: TasksAdapter
    private lateinit var adapter3: TasksAdapter
    private lateinit var adapter4: TasksAdapter
    private lateinit var adapter5: TasksAdapter
    private lateinit var adapter6: TasksAdapter
    private lateinit var adapter7: TasksAdapter
    private lateinit var adapter8: TasksAdapter
    private lateinit var adapter9: TasksAdapter
    private lateinit var adapter10: TasksAdapter
    private lateinit var adapter11: TasksAdapter
    private lateinit var adapter12: TasksAdapter
    private lateinit var adapter13: TasksAdapter
    private lateinit var adapter14: TasksAdapter
    private lateinit var adapter15: TasksAdapter
    private lateinit var adapter16: TasksAdapter
    private lateinit var adapter17: TasksAdapter
    private lateinit var adapter18: TasksAdapter
    private lateinit var adapter19: TasksAdapter
    private lateinit var adapter20: TasksAdapter
    private lateinit var adapter21: TasksAdapter
    private lateinit var adapter22: TasksAdapter

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
            scrollView.scrollTo(0, findViewById<TableRow>(R.id.row8).top)
        }
        db = AppDatabase.getDatabase(this)

        setupRecyclerViews()

        fetchTasks()

    }

    private fun setupRecyclerViews() {
        recyclerView0 = findViewById(R.id.tasks0)
        adapter0 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
        recyclerView0.adapter = adapter0
        recyclerView0.layoutManager = LinearLayoutManager(this)

        recyclerView1 = findViewById(R.id.tasks1)
        adapter1 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
        recyclerView1.adapter = adapter1
        recyclerView1.layoutManager = LinearLayoutManager(this)

        recyclerView2 = findViewById(R.id.tasks2)
        adapter2 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
        recyclerView2.adapter = adapter2
        recyclerView2.layoutManager = LinearLayoutManager(this)

        recyclerView3 = findViewById(R.id.tasks3)
        adapter3 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
        recyclerView3.adapter = adapter3
        recyclerView3.layoutManager = LinearLayoutManager(this)

        recyclerView4 = findViewById(R.id.tasks4)
        adapter4 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView5 = findViewById(R.id.tasks5)
        adapter5 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView6 = findViewById(R.id.tasks6)
        adapter6 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView7 = findViewById(R.id.tasks7)
        adapter7 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView8 = findViewById(R.id.tasks8)
        adapter8 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView9 = findViewById(R.id.tasks9)
        adapter9 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView10 = findViewById(R.id.tasks10)
        adapter10 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView11 = findViewById(R.id.tasks11)
        adapter11 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView12 = findViewById(R.id.tasks12)
        adapter12 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView13 = findViewById(R.id.tasks13)
        adapter13 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView14 = findViewById(R.id.tasks14)
        adapter14 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView15 = findViewById(R.id.tasks15)
        adapter15 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView16 = findViewById(R.id.tasks16)
        adapter16 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView17 = findViewById(R.id.tasks17)
        adapter17 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView18 = findViewById(R.id.tasks18)
        adapter18 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView19 = findViewById(R.id.tasks19)
        adapter19 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView20 = findViewById(R.id.tasks20)
        adapter20 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView21 = findViewById(R.id.tasks21)
        adapter21 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)

        recyclerView22 = findViewById(R.id.tasks22)
        adapter22 = TasksAdapter(tasks, ::onEditTask, ::onDeleteTask)
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
                splitTaskList["0"]?.let { adapter0.setTasks(it) }
                splitTaskList["1"]?.let { adapter1.setTasks(it) }
                splitTaskList["2"]?.let { adapter2.setTasks(it) }
                splitTaskList["3"]?.let { adapter3.setTasks(it) }
                splitTaskList["4"]?.let { adapter4.setTasks(it) }
                splitTaskList["5"]?.let { adapter5.setTasks(it) }
                splitTaskList["6"]?.let { adapter6.setTasks(it) }
                splitTaskList["7"]?.let { adapter7.setTasks(it) }
                splitTaskList["8"]?.let { adapter8.setTasks(it) }
                splitTaskList["9"]?.let { adapter9.setTasks(it) }
                splitTaskList["10"]?.let { adapter10.setTasks(it) }
                splitTaskList["11"]?.let { adapter11.setTasks(it) }
                splitTaskList["12"]?.let { adapter12.setTasks(it) }
                splitTaskList["13"]?.let { adapter13.setTasks(it) }
                splitTaskList["14"]?.let { adapter14.setTasks(it) }
                splitTaskList["15"]?.let { adapter15.setTasks(it) }
                splitTaskList["16"]?.let { adapter16.setTasks(it) }
                splitTaskList["17"]?.let { adapter17.setTasks(it) }
                splitTaskList["18"]?.let { adapter18.setTasks(it) }
                splitTaskList["19"]?.let { adapter19.setTasks(it) }
                splitTaskList["20"]?.let { adapter20.setTasks(it) }
                splitTaskList["21"]?.let { adapter21.setTasks(it) }
                splitTaskList["22"]?.let { adapter22.setTasks(it) }
            } catch (e: Exception) {
            }
        }
    }

    private fun splitTasksByHour(tasks: List<Task>): Map<String, List<Task>> {
        val tasksByHour = mutableMapOf<String, MutableList<Task>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (task in tasks) {
            val startDate = dateFormat.parse(task.dateStart) ?: continue
            val finishDate = dateFormat.parse(task.dateFinish) ?: continue

            val calendar = Calendar.getInstance().apply { time = startDate }

            while (calendar.time <= finishDate) {
                val hourKey = SimpleDateFormat("HH", Locale.getDefault()).format(calendar.time)
                tasksByHour.computeIfAbsent(hourKey) { mutableListOf() }.add(task)
                calendar.add(Calendar.HOUR_OF_DAY, 1)
            }
        }

        return tasksByHour
    }


    private fun onEditTask(task: Task) {

    }

    private fun onDeleteTask(id: Long) {

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