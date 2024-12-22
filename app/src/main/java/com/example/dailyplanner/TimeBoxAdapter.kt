package com.example.dailyplanner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TimeBoxAdapter(
    private val times: MutableList<TimeBox>,
    private val context: Context
    ) : RecyclerView.Adapter<TimeBoxAdapter.TimeBoxViewHolder>(){

    inner class TimeBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.time)
        private val tasksRecycler: RecyclerView = itemView.findViewById(R.id.tasksRecyclerView)

        fun bind(timeBox: TimeBox) {
            if (timeBox.time >= 10) {
                timeText.text = "${timeBox.time}:00"
            }
            else {
                timeText.text = "0${timeBox.time}:00"
            }
            tasksRecycler.adapter = TasksAdapter(timeBox.taskList)
            tasksRecycler.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeBoxAdapter.TimeBoxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timebox, parent, false)
        return TimeBoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeBoxAdapter.TimeBoxViewHolder, position: Int) {
        holder.bind(times[position])
    }

    override fun getItemCount(): Int = times.size

    fun setTimes(newTimeBoxes: List<TimeBox>) {
        times.clear()
        times.addAll(newTimeBoxes)
        notifyDataSetChanged()
    }

}