package com.ijp.b4badmin.jobs.configure.task

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.ijp.b4badmin.jobs.configure.EditJobActivity
import com.ijp.b4badmin.model.TaskItem
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivityConfigureTaskListBinding

class ConfigureTaskListActivity : AppCompatActivity() {
    private var binding: ActivityConfigureTaskListBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private var adapterRV: RvTaskConfigureAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val jobId = intent.getStringExtra("jobId")

        adapterRV = RvTaskConfigureAdapter(this@ConfigureTaskListActivity, emptyList())
        { taskItem ->
            val ref = FirebaseDatabase.getInstance().reference

            ref.child("tasks").child(jobId!!.trim()).child(taskItem.taskId!!)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        this@ConfigureTaskListActivity,
                        "Task Item deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
        binding?.let { b ->
            b.rvJobsItem.let { rv ->
                rv.layoutManager =
                    LinearLayoutManager(this@ConfigureTaskListActivity)
                rv.adapter = adapterRV

            }
        }

        db.reference.child("tasks").child(jobId!!.trim())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ConfigureTask", "snap is $snapshot")
                    val taskList = mutableListOf<TaskItem>()
                    if (snapshot.exists()) {
                        for (task in snapshot.children) {
                            val taskItem = task.getValue(TaskItem::class.java)
                            if (taskItem != null) {
                                taskList.add(taskItem)
                            }
                        }

                        binding?.frameItemLoadingSelectJob?.visibility = View.GONE
                        adapterRV?.updateList(taskList)
                    } else {
                        adapterRV?.updateList(emptyList())
                        binding?.progress?.visibility = View.GONE
                        Toast.makeText(
                            this@ConfigureTaskListActivity,
                            "No Tasks available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}

class RvTaskConfigureAdapter(
    val context: Context,
    var taskList: List<TaskItem>,
    val onTaskDeleteIconClicked: (TaskItem) -> Unit
) : RecyclerView.Adapter<RvTaskConfigureAdapter.RvJobListItem>() {

    fun updateList(newList: List<TaskItem>) {
        this.taskList = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobListItem {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.job_configure_item, parent, false)
        return RvJobListItem(view)
    }

    override fun onBindViewHolder(holder: RvJobListItem, position: Int) {
        val currTask = taskList[position]

        holder.frameLayout.visibility = View.GONE
        holder.jobTitle.text = currTask?.task_title
        holder.jobLogo.load(currTask?.jobLogo)

        holder.iconDelete.setOnClickListener {
            onTaskDeleteIconClicked(currTask)
        }
        holder.iconEdit.setOnClickListener {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("task", currTask)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = taskList.size

    class RvJobListItem(view: View) : RecyclerView.ViewHolder(view) {
        val jobTitle: TextView = view.findViewById(R.id.tv_job_option_title)
        val frameLayout: FrameLayout = view.findViewById(R.id.frame_item_loading)
        val mcJopOption: MaterialCardView = view.findViewById(R.id.mc_job_item_option)
        val iconDelete: ImageView = view.findViewById(R.id.icon_delete)
        val iconEdit: ImageView = view.findViewById(R.id.icon_edit)
        val jobLogo: ImageView = view.findViewById(R.id.img_logo_job_option)
    }


}