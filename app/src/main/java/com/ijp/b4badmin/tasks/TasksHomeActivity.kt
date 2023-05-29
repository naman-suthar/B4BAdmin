package com.ijp.b4badmin.tasks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vrcareer.b4badmin.R
import com.ijp.b4badmin.model.SubmittedTask
import com.ijp.b4badmin.model.User
import com.ijp.b4badmin.utils.convertLongToTime
import com.vrcareer.b4badmin.databinding.ActivityTasksHomeBinding

/**
 * This activity shows list of all pending task
 * */
class TasksHomeActivity : AppCompatActivity() {
    private var binding: ActivityTasksHomeBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val taskList = mutableListOf<SubmittedTask>()
    private var adapter: SubmittedTaskRVAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        adapter = SubmittedTaskRVAdapter(this,taskList){
            Log.d("Task isthe","$it")
            val intent = Intent(this,ApproveTaskActivity::class.java)
            intent.putExtra("task",it)
            startActivity(intent)
        }
        binding?.rvSubmittedTask?.let{
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
        /**
         * Fetching the list of submitted tasks from DB*/
        db.reference.child("submitted_task").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    taskList.clear()
                    for (snap in snapshot.children){
                        val userId = snap.key
                        if (snap.exists()){
                            for (task in snap.children){
                                val submittedTask = task.getValue(SubmittedTask::class.java)
                                if (submittedTask != null && submittedTask.status == "pending") {
                                    taskList.add(submittedTask)
                                }
                            }

                        }

                    }
                    if (taskList.isEmpty()){
                        binding?.txtNoTasksMessage?.visibility = View.VISIBLE
                        Toast.makeText(
                            this@TasksHomeActivity,
                            "No Pending Tasks Available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        binding?.txtNoTasksMessage?.visibility = View.GONE
                    }
                    Log.d("ValueSnap:","$taskList")
                    adapter?.updateList(taskList)

                }
                else{
                    Toast.makeText(
                        this@TasksHomeActivity,
                        "No Pending Tasks Available YES",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}

/**
 * Adapter for tasks list */
class SubmittedTaskRVAdapter(context: Context, private var tasksList: MutableList<SubmittedTask>,val onTaskItemClicked: (SubmittedTask)->Unit)
    :RecyclerView.Adapter<SubmittedTaskRVAdapter.SubmittedTaskViewHolder>(){
    private val db = FirebaseDatabase.getInstance()

    fun updateList(list:MutableList<SubmittedTask>){

       tasksList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmittedTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_task_submitted_list_item,parent,false)
        return SubmittedTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubmittedTaskViewHolder, position: Int) {
        val currTask = tasksList[position]
        db.reference.child("users").child(currTask.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    val user = it.getValue(User::class.java)
                    holder.taskName.text = "${user?.name} ${convertLongToTime(currTask.time_of_submission!!)}"
                }
            }

        holder.taskCard.setOnClickListener {
            onTaskItemClicked(currTask)
        }
    }

    override fun getItemCount(): Int = tasksList.size

    class SubmittedTaskViewHolder(view:View): RecyclerView.ViewHolder(view){
            val taskName: TextView = view.findViewById(R.id.tv_submitted_task)
        val taskCard: MaterialCardView = view.findViewById(R.id.mc_taskItem)
        }
}