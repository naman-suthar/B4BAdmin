package com.ijp.b4badmin.trainings

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
import com.vrcareer.b4badmin.databinding.ActivityTrainingSubmittionListBinding
import com.ijp.b4badmin.model.Assessment
import com.ijp.b4badmin.model.User
import com.ijp.b4badmin.tasks.ApproveTaskActivity
import com.ijp.b4badmin.utils.ApplicationResponse

/**
 * This Activity is used to show the list of submitted Assessment*/
class TrainingSubmissionListActivity : AppCompatActivity() {
    private var binding: ActivityTrainingSubmittionListBinding? = null
    private var db = FirebaseDatabase.getInstance()
    private var assessmentList = mutableListOf<Assessment>()
    private var adapter: SubmittedAssessmentRVAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingSubmittionListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        adapter = SubmittedAssessmentRVAdapter(this,assessmentList){
            val intent = Intent(this, ApproveAssessmentActivity::class.java)
            intent.putExtra("assessment",it)
            startActivity(intent)
        }
        binding?.rvSubmittedAssessment?.let{
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
        /**
         * Fetching all Assessment submission and updating list
         * */
        db.reference.child("trainings").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    assessmentList.clear()

                    for (snap in snapshot.children){
                        val userId = snap.key
                        if (snap.exists()){
                            for (task in snap.children){
                                val assess = task.getValue(Assessment::class.java)
                                if (assess != null && assess.status == ApplicationResponse.Pending.name) {
                                    assessmentList.add(assess)
                                }
                            }
                        }


                    }
                    Log.d("ValueSnap:","$assessmentList")
                    adapter?.updateList(assessmentList)
                    if (assessmentList.isEmpty()){
                        binding?.txtNoAssessmentMessage?.visibility = View.VISIBLE
                    }else{
                        binding?.txtNoAssessmentMessage?.visibility = View.GONE
                    }

                }else{
                    binding?.txtNoAssessmentMessage?.visibility = View.VISIBLE
                    Toast.makeText(
                        this@TrainingSubmissionListActivity,
                        "Network error",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}


class SubmittedAssessmentRVAdapter(context: Context, private var assessmentList: MutableList<Assessment>, val onTaskItemClicked: (Assessment)->Unit)
    : RecyclerView.Adapter<SubmittedAssessmentRVAdapter.SubmittedAssessmentViewHolder>(){
    private val db = FirebaseDatabase.getInstance()
    fun updateList(list:MutableList<Assessment>){

        assessmentList = list
        Log.d("ValueSnap","It is Called ${assessmentList.size}")
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmittedAssessmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_task_submitted_list_item,parent,false)
        return SubmittedAssessmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubmittedAssessmentViewHolder, position: Int) {
        val currAssessment = assessmentList[position]
        db.reference.child("users").child(currAssessment.user_id!!)
            .get().addOnSuccessListener {
                if (it.exists()){
                    val user = it.getValue(User::class.java)
                    holder.taskName.text ="${user?.name} - ${currAssessment.assessment_id}"
                }
            }

        holder.taskCard.setOnClickListener {
            onTaskItemClicked(currAssessment)
        }
    }

    override fun getItemCount(): Int = assessmentList.size

    class SubmittedAssessmentViewHolder(view: View): RecyclerView.ViewHolder(view){
        val taskName: TextView = view.findViewById(R.id.tv_submitted_task)
        val taskCard: MaterialCardView = view.findViewById(R.id.mc_taskItem)
    }
}