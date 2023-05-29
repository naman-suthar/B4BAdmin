package com.ijp.b4badmin.jobs.job_application

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.ijp.b4badmin.MainActivity
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.FragmentJobApplicationDetailsBinding
import com.ijp.b4badmin.model.Answer
import com.ijp.b4badmin.model.User
import com.ijp.b4badmin.utils.convertLongToTime

/**
 * This Fragment is used for displaying the Job Application details*/
class JobApplicationDetailsFragment : Fragment() {
    private val args: JobApplicationDetailsFragmentArgs by navArgs()
    private var binding: FragmentJobApplicationDetailsBinding? = null
    private val db = FirebaseDatabase.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobApplicationDetailsBinding.inflate(inflater, container, false)
        val jobApplication = args.application
        Log.d("JobApplication", "$jobApplication")
        jobApplication.user_id?.let {
            db.reference.child("users").child(it).get().addOnSuccessListener { ds ->
                if (ds.exists()) {
                    val user = ds.getValue(User::class.java)
                    binding?.applicationDetailsUserName?.text = user?.name
                    binding?.applicationDetailsUserEmail?.text = user?.email
                    binding?.applicationDetailsUserMobile?.text = user?.phoneNo
                    binding?.applicationDetailsUserDob?.text = user?.dob
                }
            }
        }
        jobApplication?.ansList?.let { list ->
            binding?.rvApplicationAnswers?.let { rv ->
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = context?.let { RvApplicationAnswersAdapter(it, list) }
            }
        }

        binding?.btnApplicationApproved?.setOnClickListener {
            jobApplication.user_id?.let { id ->
                val existingApprovedJobs = mutableListOf<String>()

                db.reference.child("users").child(id).child("approved_jobs")
                    .push()
                    .setValue(jobApplication.job_id).addOnSuccessListener {
                        db.reference.child("job_application/${jobApplication.job_id}/pending/${jobApplication.user_id}").removeValue().addOnSuccessListener {
                            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(requireActivity(),MainActivity::class.java)
                            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                            requireActivity().startActivity(intent)

                        }
                }

                   /* .get().addOnSuccessListener {
                    if (it.exists()){
                        for (job in it.children){
                            val jobId = job.value.toString()
                            existingApprovedJobs.add(jobId)
                        }
                    }
                    jobApplication.job_id?.let {
                            it1 -> existingApprovedJobs.add(it1)
                            db.reference.child("users").child(id).child("approved_jobs")
                                .setValue(existingApprovedJobs).addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT)
                                        .show()
                                }
                    }
                }*/
            }
        }
        binding?.btnApplicationReject?.setOnClickListener {
            jobApplication.user_id?.let { id ->
                db.reference.child("job_application/${jobApplication.job_id}/pending/${jobApplication.user_id}").removeValue().addOnSuccessListener {
                            Toast.makeText(requireContext(), "Rejected", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(requireActivity(),MainActivity::class.java)
                            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                            requireActivity().startActivity(intent)

                }


            }
        }
        return binding?.root
    }


}

class RvApplicationAnswersAdapter(context: Context, val answerList: List<Answer>) :
    RecyclerView.Adapter<RvApplicationAnswersAdapter.AnswerItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_answer_item, parent, false)
        return AnswerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerItemViewHolder, position: Int) {
        val currAnswer = answerList[position]
        holder.answerIndex.text = "${position + 1}."
        holder.questionText.text = currAnswer.question
        holder.answerText.text = currAnswer.answer
    }

    override fun getItemCount(): Int = answerList.size

    class AnswerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val answerIndex: TextView = view.findViewById(R.id.answer_index)
        val answerText: TextView = view.findViewById(R.id.answer_text)
        val questionText: TextView = view.findViewById(R.id.question_text)
    }
}