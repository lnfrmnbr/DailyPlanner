package com.example.dailyplanner

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
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
import android.widget.Toast
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

        findViewById<Button>(R.id.add).setOnClickListener {
            showAddTaskDialog()
        }
    }

    override fun onStart() {
        super.onStart()
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
                val selectedDate = calendar.getSelectedDates().first()

                val calendarInstance = Calendar.getInstance().apply {
                    time = selectedDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val dateAtMidnight = calendarInstance.time
                val tasksList = TaskListTypeConverter().toTaskList(
                    db.taskListDao().getAllTaskLists()[0].tasksJson
                ).filter {
                    dateAtMidnight <= stringToTimestamp(it.dateStart)!! ||
                            dateAtMidnight <= stringToTimestamp(it.dateFinish)!!
                }
                val splitTaskList = splitTasksByHour(tasksList)
                adapter.setTimes(splitTaskList)
            } catch (_: Exception) {
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
            while (calendarDop.time <= endOfDay) {
                val hourKey = calendarDop.get(Calendar.HOUR_OF_DAY)

                if ((calendarDop.time >= startDate) && ((calendarDop.time <= finishDate) || (calendarDop.time.hours == finishDate.hours))) {
                    tasksByHour.computeIfAbsent(hourKey) { mutableListOf() }.add(task)
                }

                calendarDop.add(Calendar.HOUR_OF_DAY, 1)
            }
        }

        return (0..23).map { hour ->
            TimeBox(
                time = hour,
                taskList = tasksByHour[hour]?.distinct()?.toMutableList() ?: mutableListOf()
            )
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
        val dateFinishInput = dialogLayout.findViewById<DatePicker>(R.id.datePickerFinish)
        val timeFinishInput = dialogLayout.findViewById<TimePicker>(R.id.timePickerFinish)

        builder.setTitle("Добавить дело")
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val descr = descrInput.text.toString()
                val startYear = dateStartInput.year
                val startMonth = dateStartInput.month
                val startDay = dateStartInput.dayOfMonth
                val startHour = timeStartInput.hour
                val startMinute = timeStartInput.minute

                val finishYear = dateFinishInput.year
                val finishMonth = dateFinishInput.month
                val finishDay = dateFinishInput.dayOfMonth
                val finishHour = timeFinishInput.hour
                val finishMinute = timeFinishInput.minute

                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay, startHour, startMinute)
                }
                val finishDate = Calendar.getInstance().apply {
                    set(finishYear, finishMonth, finishDay, finishHour, finishMinute)
                }

                if (name.isEmpty() || descr.isEmpty()) {
                    Toast.makeText(this, "Заполните поля названия и описания", Toast.LENGTH_LONG)
                        .show()
                    return@setPositiveButton
                }

                if (startDate.after(finishDate)) {
                    Toast.makeText(
                        this,
                        "Дата начала не может быть больше даты окончания",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                val newTask = Task(
                    name = name,
                    description = descr,
                    dateStart = String.format(
                        "%04d-%02d-%02d %02d:%02d:00",
                        startYear,
                        startMonth + 1,
                        startDay,
                        startHour,
                        startMinute
                    ),
                    dateFinish = String.format(
                        "%04d-%02d-%02d %02d:%02d:00",
                        finishYear,
                        finishMonth + 1,
                        finishDay,
                        finishHour,
                        finishMinute
                    )
                )

                lifecycleScope.launch {
                    try {
                        db.taskListDao().addTaskToList(newTask)
                        fetchTasks()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка добавления дела",
                            Toast.LENGTH_LONG
                        ).show()
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
