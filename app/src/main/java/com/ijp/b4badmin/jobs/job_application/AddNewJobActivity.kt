package com.ijp.b4badmin.jobs.job_application

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vrcareer.b4badmin.R

import com.ijp.b4badmin.model.Job
import com.ijp.b4badmin.model.Question
import com.ijp.b4badmin.model.QuestionType
import com.vrcareer.b4badmin.databinding.ActivityAddNewJobBinding

/**
 * This activity is for adding a new Job
 * */
class AddNewJobActivity : AppCompatActivity() {
    private var binding: ActivityAddNewJobBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var storageReference = storage.reference
    private var jobIconUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewJobBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val optionsTypes = listOf(
            QuestionType.Text.type,
            QuestionType.Boolean.type,
            QuestionType.MultiLineText.type,
            QuestionType.Number.type,
            QuestionType.Dropdown.type,
            QuestionType.Ratings.type
        )
        val questionTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, optionsTypes)
        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {

            binding?.jobLogoPreview?.setImageURI(it)
//                imgUri = it
            jobIconUri = it


        }
        binding?.btnSelectIcon?.setOnClickListener {
                galleryImage.launch("image/*")

        }
        binding?.btnGenerateForm?.setOnClickListener {
            val questionNo = binding?.etTotalQuestionNo?.text.toString().toIntOrNull()

            if (questionNo != null) {
                binding?.llFormContainer?.removeAllViews()
                for (i in questionNo) {

                    val view = LayoutInflater.from(this)
                        .inflate(R.layout.layout_question_item, binding?.llFormContainer, false)
                    val etTypeLayout = view.findViewById<TextInputLayout>(R.id.et_question_type)
                    val etType: EditText? = etTypeLayout.editText

                    val etOptionsLayout: TextInputLayout = view.findViewById(R.id.et_options_layout)
                    (etTypeLayout?.editText as? AutoCompleteTextView)?.setText(optionsTypes[0])
                    (etType as? AutoCompleteTextView)?.setAdapter(questionTypeAdapter)

                    etTypeLayout.editText?.doOnTextChanged { inputText, _, _, _ ->

                        if (inputText.toString() == "dropdown"){
                            Log.d("ValueChanged:","Drop $inputText")
                            etOptionsLayout.visibility = View.VISIBLE
                        }else{
                            Log.d("ValueChanged:","$inputText")
                            etOptionsLayout.visibility = View.GONE
                        }
                    }
                    binding?.llFormContainer?.addView(view)
                }
                binding?.etTotalQuestionNo?.text?.clear()
                binding?.etTotalQuestionNo?.clearFocus()
            }
            else{
                binding?.etTotalQuestionNo?.requestFocus()
                binding?.etTotalQuestionNo?.error = "Enter valid No"
            }

        }

        binding?.btnAddJob?.setOnClickListener {
            val job_description: String = binding?.etJobDescription?.text.toString()
            val job_id: String = binding?.etJobId?.text.toString()
            val job_title: String = binding?.etJobTitle?.text.toString()
            val job_tagline: String = binding?.etJobTagline?.text.toString()
            if (jobIconUri == null){
                binding?.jobLogoPreview?.requestFocus()
                Toast.makeText(this, "Select Icon", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (job_id.isEmpty()){
                binding?.etJobId?.requestFocus()
                binding?.etJobId?.error = "Job ID is mandatory"
                return@setOnClickListener
            }
            if (job_title.isEmpty()){
                binding?.etJobTitle?.requestFocus()
                binding?.etJobTitle?.error = "Job title is mandatory"
                return@setOnClickListener
            }
            if (job_tagline.isEmpty()){
                binding?.etJobTagline?.requestFocus()
                binding?.etJobTagline?.error = "Job Tagline is mandatory"
                return@setOnClickListener
            }
            if (job_description.isEmpty()){
                binding?.etJobDescription?.requestFocus()
                binding?.etJobDescription?.error = "Job description is mandatory"
                return@setOnClickListener
            }
            val questionList = mutableListOf<Question>()
            binding?.llFormContainer?.children?.forEachIndexed {i, it->
                (it as? MaterialCardView)?.children?.forEach{ mcq->
                    //Statement
                    val etQuestion: EditText = mcq.findViewWithTag("et_question_statement")
                    val questionStatement = etQuestion.text.toString()
                    if (questionStatement.isEmpty()){
                        etQuestion.requestFocus()
                        etQuestion.error = "Empty"
                        return@setOnClickListener
                    }
                    //Type
                    val etQuestionType: EditText = mcq.findViewWithTag("et_type")
                    val questionType = etQuestionType.text.toString()

                    //Options
                    val etOptions: EditText = mcq.findViewWithTag("options")
                    val options = etOptions.text.toString().split(",")
                    if (questionType == QuestionType.Dropdown.type && (options.isEmpty() || options.size< 2)){
                        etOptions.requestFocus()
                        etOptions.error = "at least 2 options requirred"
                        return@setOnClickListener
                    }
                    //New Question
                    val newQ = Question(qid = i.toString(), question_statement = questionStatement, question_type = questionType, options = options)
                    questionList.add(i,newQ)
                }
            }
            if (questionList.isEmpty()){
                Toast.makeText(this, "Screening form is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val progressBar = ProgressDialog(this)
            progressBar?.setTitle("Uploading ")
            progressBar?.setCancelable(false)
            progressBar?.show()

            jobIconUri?.let { uri ->

                val timeOfNow = System.currentTimeMillis()

                storageReference.child("Images/Icons").child(job_id).child("logo")
                    .putFile(uri).addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { urifs ->

                            //Submit Form
                            val job = Job(
                                job_id = job_id,
                                job_title = job_title,
                                "On Field",
                                job_tagline = job_tagline,
                                job_description = job_description,
                                screeningQuestions = questionList,
                                job_icon = urifs.toString(),
                                status = "Active"
                            )

                            db.reference.child("Jobs").child(job_id).setValue(job).addOnSuccessListener {
                                Toast.makeText(this@AddNewJobActivity, "Job added", Toast.LENGTH_SHORT).show()
                                progressBar.dismiss()
                                binding?.btnAddJob?.isEnabled = false
                                this.finish()
                            }.addOnFailureListener{
                                Toast.makeText(this@AddNewJobActivity, "Please retry ${it.message}", Toast.LENGTH_SHORT).show()
                                progressBar.dismiss()
                            }

                        }
                            .addOnFailureListener { e ->
                                progressBar.dismiss()
                                Toast.makeText(
                                    this,
                                    "Retry",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                    }
                    .addOnFailureListener { e ->
                        progressBar.dismiss()
                        Toast.makeText(
                            this,
                            "Storage Network error ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }


        }
    }
}

operator fun Int.iterator(): Iterator<Int> {
    return (0 until this).iterator()
}