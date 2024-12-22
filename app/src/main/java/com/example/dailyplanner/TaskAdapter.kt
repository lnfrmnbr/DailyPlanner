package com.example.dailyplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(
    private val tasks: MutableList<Task>,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<TasksAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val taskDescr: TextView = itemView.findViewById(R.id.taskDescr)
        private val taskTime: TextView = itemView.findViewById(R.id.taskTime)

        fun bind(task: Task) {
            taskName.text = "${task.name}"
            taskDescr.text = "${task.description} "
//
//            itemView.findViewById<Button>(R.id.editButton).setOnClickListener {
//                    onEditClick(employer)
//            }
//            itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
//                    employer.id?.let { it1 -> onDeleteClick(it1) }
//            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}