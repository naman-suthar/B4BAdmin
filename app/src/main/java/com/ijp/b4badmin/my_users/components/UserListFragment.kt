package com.ijp.b4badmin.my_users.components

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.ijp.b4badmin.R
import com.ijp.b4badmin.databinding.FragmentUserListBinding
import com.ijp.b4badmin.model.User

/**
 * This fragment is used to show list of users*/
class UserListFragment : Fragment() {
    private val db = FirebaseDatabase.getInstance()
    private var binding: FragmentUserListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        db.reference.child("users")
            .get().addOnSuccessListener {
                if (it.exists()) {
                    binding?.frameLoadingUserList?.visibility = View.GONE
                    val userList = mutableListOf<User>()
                    for (user in it.children) {
                       //For filtering Cordinator
                       /* if (user.child("cordinator").value == "Nam"){

                        }*/
                        val user = user.getValue(User::class.java)
                        if (user != null ) {
                            userList.add(user)
                        }
                    }
                    Log.d("Users", "$userList")
                    binding?.rvMyUsersList?.let { rv->
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = context?.let { it1 ->
                            RvMyUserListAdapter(it1,userList){user->
                                val action = UserListFragmentDirections.actionUserListFragmentToUserDetailsFragment(user)
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
                else{
                    binding?.frameLoadingUserList?.visibility = View.GONE
                    Toast.makeText(requireContext(),"No Users Available",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
            }
        return binding?.root
    }


}

class RvMyUserListAdapter(context: Context, var userList: MutableList<User>, var onUserItemClicked: (User)-> Unit) :
    RecyclerView.Adapter<RvMyUserListAdapter.MyUserItemViewHolder>() {

    fun updateUserList(list: MutableList<User>) {
        userList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUserItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_job_application_user_item, parent, false)
        return MyUserItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyUserItemViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.framelayout.visibility = View.GONE
        holder.userName.text = currentUser.name
        holder.phoneNo.text = currentUser.phoneNo
        holder.mc_user.setOnClickListener {
            onUserItemClicked(currentUser)
        }
    }

    override fun getItemCount(): Int = userList.size

    class MyUserItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tv_application_user_name)
        val phoneNo: TextView = view.findViewById(R.id.tv_time_of_request)
        val framelayout : FrameLayout = view.findViewById(R.id.frame_item_loading_job_application_user_list)
        val mc_user: MaterialCardView = view.findViewById(R.id.mc_application_item)
    }
}