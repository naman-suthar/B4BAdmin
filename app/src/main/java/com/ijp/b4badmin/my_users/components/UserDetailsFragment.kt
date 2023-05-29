package com.ijp.b4badmin.my_users.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.FragmentUserDetailsBinding
import com.ijp.b4badmin.model.PaymentDetails


/**
 * This fragment is used to show User details
 * */
class UserDetailsFragment : Fragment() {
    private val db = FirebaseDatabase.getInstance()

    private var binding: FragmentUserDetailsBinding? = null
    private val args: UserDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater,container,false)
        val user = args.user
        binding?.myUserDetailName?.text = user.name
        binding?.myUserDetailEmail?.text = user.email
        binding?.myUserDetailMobile?.text = user.phoneNo
        binding?.myUserDetailDob?.text = user.dob

        user.id?.let { uid->
            db.reference.child("payment_mode").child(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()){
                        val paymentDetails = it.getValue(PaymentDetails::class.java)
                        paymentDetails?.let {pd->
                            binding?.tvActivePaymentMode?.text = pd.selected_mode
                            binding?.tvUpiId?.text = pd.upiId ?: "Not Available"
                            binding?.tvBankName?.text = pd.bank_account_name ?: "Not Available"
                            binding?.tvBankAccountNumber?.text = pd.bank_account_number ?: "Not Available"
                            binding?.tvBankIfsc?.text = pd.bank_ifsc ?: "Not Available"
                            binding?.tvBankType?.text = pd.bank_account_type ?: "Not Available"
                        }

                    }
                    else{
                        binding?.tvActivePaymentMode?.text = "Not Available"
                        binding?.tvUpiId?.text = "Not Available"
                        binding?.tvBankName?.text = "Not Available"
                        binding?.tvBankAccountNumber?.text ="Not Available"
                        binding?.tvBankIfsc?.text = "Not Available"
                        binding?.tvBankType?.text ="Not Available"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"${it.message} Hence, Unable to load Payment Data",Toast.LENGTH_SHORT).show()
                    binding?.tvActivePaymentMode?.text = "NA"
                    binding?.tvUpiId?.text = "NA"
                    binding?.tvBankName?.text = "NA"
                    binding?.tvBankAccountNumber?.text = "NA"
                    binding?.tvBankIfsc?.text = "NA"
                    binding?.tvBankType?.text ="NA"
                }
        }

        binding?.btnDeleteUser?.setOnClickListener {
            db.reference.child("users").child(user.id!!).removeValue()
            db.reference.child("earnings").child(user.id!!).removeValue()

        }
        return binding?.root
    }


}