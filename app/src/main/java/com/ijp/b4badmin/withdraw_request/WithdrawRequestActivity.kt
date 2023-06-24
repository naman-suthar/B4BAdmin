package com.ijp.b4badmin.withdraw_request

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vrcareer.b4badmin.R
import com.ijp.b4badmin.model.WithdrawalRequest
import com.vrcareer.b4badmin.databinding.ActivityWithdrawRequestBinding

/**
 * This activity is used to show the list of Withdrawal requests
 * */
class WithdrawRequestActivity : AppCompatActivity() {
    private var binding: ActivityWithdrawRequestBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val withdrawRequestList = mutableListOf<WithdrawalRequest>()
    private var adapter: WithdrawRequestRv? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawRequestBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        /**
         * Fetching all withdrawal request from database
         * and update List
         * */
        adapter = WithdrawRequestRv(this@WithdrawRequestActivity, withdrawRequestList) { req ->
            val intent = Intent(this@WithdrawRequestActivity, WithdrawDetailsActivity::class.java)
            intent.putExtra("request", req)
            startActivity(intent)
        }
        binding?.rvWithdrawalRequests?.let { rv ->
            rv.layoutManager = LinearLayoutManager(this@WithdrawRequestActivity)
            rv.adapter = adapter
        }
        db.reference.child("withdraw_request").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    withdrawRequestList.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val request = snap.getValue(WithdrawalRequest::class.java)
                            if (request != null) {
                                Log.d("InitializedBVH", "listReq -> $request")
                                withdrawRequestList.add(request)
                            }
                        }
                        adapter?.updateList(withdrawRequestList)
                        if (withdrawRequestList.isEmpty()) {
                            binding?.txtNoAssessmentMessage?.visibility = View.VISIBLE
                        } else {
                            binding?.txtNoAssessmentMessage?.visibility = View.GONE
                        }
                    } else {
                        adapter?.updateList(withdrawRequestList)
                        binding?.rvWithdrawalRequests?.visibility = View.GONE
                        binding?.txtNoAssessmentMessage?.visibility = View.VISIBLE

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
        /*db.reference.child("withdraw_request").get().addOnSuccessListener {
            if (it.exists()){
                withdrawRequestList.clear()
                for (snap in it.children){
                    val request = snap.getValue(WithdrawalRequest::class.java)
                    if (request != null) {
                        withdrawRequestList.add(request)
                    }
                }
                adapter = WithdrawRequestRv(this,withdrawRequestList){req->
                    val intent = Intent(this,WithdrawDetailsActivity::class.java)
                    intent.putExtra("request",req)
                    startActivity(intent)
                }
                binding?.rvWithdrawalRequests?.let {rv->
                    rv.layoutManager = LinearLayoutManager(this)
                    rv.adapter = adapter
                }
                if (withdrawRequestList.isEmpty()){
                    binding?.txtNoAssessmentMessage?.visibility = View.VISIBLE
                }else{
                    binding?.txtNoAssessmentMessage?.visibility = View.GONE
                }
            }
        }*/
    }
}

class WithdrawRequestRv(
    context: Context,
    private var requestList: List<WithdrawalRequest>,
    val onRequestClicked: (WithdrawalRequest) -> Unit
) : RecyclerView.Adapter<WithdrawRequestRv.WithdrawViewHolder>() {

    fun updateList(list: List<WithdrawalRequest>) {
        Log.d("InitializedBVH", "Inp ->$list")
        requestList = list as MutableList<WithdrawalRequest>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_task_submitted_list_item, parent, false)
        return WithdrawViewHolder(view)
    }

    override fun onBindViewHolder(holder: WithdrawViewHolder, position: Int) {
        Log.d("InitializedBVH", "true")
        val currRequest = requestList[position]
        holder.withdraweeName.text = currRequest.userName
        holder.mcItem.setOnClickListener {
            onRequestClicked(currRequest)
        }
    }

    override fun getItemCount(): Int = requestList.size

    class WithdrawViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val withdraweeName: TextView = view.findViewById(R.id.tv_submitted_task)
        val mcItem: MaterialCardView = view.findViewById(R.id.mc_taskItem)
    }

}