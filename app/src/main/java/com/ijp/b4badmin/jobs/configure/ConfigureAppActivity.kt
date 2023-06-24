package com.ijp.b4badmin.jobs.configure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.*
import com.ijp.b4badmin.model.Job
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivityConfigureAppBinding


class ConfigureAppActivity : AppCompatActivity() {
    private var binding: ActivityConfigureAppBinding? = null
    private val db = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureAppBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        db.reference.child("Jobs").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val jobList = mutableListOf<String>()
                    for (jobId in snapshot.children) {
                        val job = jobId.key.toString()
                        Log.d("JobId", job)
                        jobList.add(job)
                    }
                    binding?.frameItemLoadingSelectJob?.visibility = View.GONE
                    binding?.let { b ->
                        b.rvJobsItem.let { rv ->
                            rv.layoutManager = LinearLayoutManager(this@ConfigureAppActivity)
                            rv.adapter = RvJobConfigureAdapter(this@ConfigureAppActivity, jobList)
                            { job_id ->
                                val ref = FirebaseDatabase.getInstance().reference
                                val applesQuery: Query =
                                    ref.child("Jobs").orderByChild("job_id").equalTo("$job_id")

                                applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (appleSnapshot in dataSnapshot.children) {
                                            appleSnapshot.ref.removeValue()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.e("B4B", "onCancelled", databaseError.toException())
                                    }
                                })
                            }
                        }
                    }
                } else {
                    binding?.progress?.visibility = View.GONE
                    Toast.makeText(
                        this@ConfigureAppActivity,
                        "No Job Applications available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class RvJobConfigureAdapter(val context: Context, val jobIdList: List<String>, val onJobDeleteIconClicked: (String)-> Unit)
    : RecyclerView.Adapter<RvJobConfigureAdapter.RvJobListItem>(){
    private val db = FirebaseDatabase.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobListItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.job_configure_item,parent,false)
        return RvJobListItem(view)
    }

    override fun onBindViewHolder(holder: RvJobListItem, position: Int) {
        val currJobId = jobIdList[position]

        db.reference.child("Jobs").child(currJobId).get().addOnSuccessListener {
            if (it.exists()){
                val jobItem = it.getValue(Job::class.java)
                holder.frameLayout.visibility = View.GONE
                holder.jobTitle.text = jobItem?.job_title
                holder.jobLogo.load(jobItem?.job_icon)

                holder.iconDelete.setOnClickListener {
                    onJobDeleteIconClicked(currJobId)
                }
                holder.iconEdit.setOnClickListener {
                    val intent = Intent(context,EditJobActivity::class.java)
                    intent.putExtra("job",jobItem)
                    context.startActivity(intent)
                }
            }else{
                holder.frameLayout.visibility = View.GONE
                Toast.makeText(context, "No application Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = jobIdList.size

    class RvJobListItem(view: View): RecyclerView.ViewHolder(view){
        val jobTitle: TextView = view.findViewById(R.id.tv_job_option_title)
        val frameLayout: FrameLayout = view.findViewById(R.id.frame_item_loading)
        val mcJopOption: MaterialCardView = view.findViewById(R.id.mc_job_item_option)
        val iconDelete: ImageView = view.findViewById(R.id.icon_delete)
        val iconEdit: ImageView = view.findViewById(R.id.icon_edit)
        val jobLogo: ImageView = view.findViewById(R.id.img_logo_job_option)
    }



}