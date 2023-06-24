package com.ijp.b4badmin.jobs.job_application

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.FragmentJobsListOptionBinding
import com.ijp.b4badmin.model.Job

/**
 * This fragment is used to select job from available jobs*/
class JobsListOptionFragment : Fragment() {
    private val db = FirebaseDatabase.getInstance()
    private var binding: FragmentJobsListOptionBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobsListOptionBinding.inflate(inflater,container,false)
        db.reference.child("job_application").get().addOnSuccessListener {
            if (it.exists()){
                val jobList = mutableListOf<String>()
                for(jobId in it.children){
                    val job = jobId.key.toString()
                    Log.d("JobId", job)
                    jobList.add(job)
                }
                binding?.frameItemLoadingSelectJob?.visibility = View.GONE
                binding?.let { b->
                    b.rvJobsItem?.let { rv->
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = context?.let { it1 -> RvJobOptionAdapter(it1,jobList){job_id->
                            val action = JobsListOptionFragmentDirections.actionJobsListOptionFragmentToJobListFragment(job_id)
                            findNavController().navigate(action)
                        } }
                    }
                }
            }
            else{
                binding?.progress?.visibility = View.GONE
                Toast.makeText(
                    context,
                    "No Job Applications available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding?.root
    }


}

class RvJobOptionAdapter(val context: Context, val jobIdList: List<String>, val onJobItemClicked: (String)-> Unit)
    :RecyclerView.Adapter<RvJobOptionAdapter.RvJobListItem>(){
    private val db = FirebaseDatabase.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvJobListItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.job_application_job_option_item,parent,false)
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
                holder.mcJopOption.setOnClickListener {
                    onJobItemClicked(currJobId)
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
        val jobLogo: ImageView = view.findViewById(R.id.img_logo_job_option)
        }



}