package com.ijp.b4badmin.jobs.configure.task

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ijp.b4badmin.model.Question
import com.ijp.b4badmin.model.QuestionType
import com.ijp.b4badmin.model.TaskItem
import com.vrcareer.b4badmin.R
import com.vrcareer.b4badmin.databinding.ActivityEditTaskBinding

class EditTaskActivity : AppCompatActivity() {
    private var binding: ActivityEditTaskBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var storageReference = storage.reference
    private var qrUri: Uri? = null
    private val priceTypes = listOf(
        "Fixed", "Percentage",
    )
    private var selectedType = priceTypes[0]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
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
        val task = intent.getSerializableExtra("task") as? TaskItem
        task?.let { taskItem ->
            qrUri = taskItem.task_qr_url?.toUri()
            binding?.etSelectJob?.editText?.setText(taskItem.jobId)
            binding?.etTaskId?.setText(taskItem.taskId)
            binding?.etTaskTitle?.setText(taskItem.task_title)
            binding?.etTaskTagline?.setText(taskItem.task_tagline)
            binding?.etPriceTagline?.setText(taskItem.price_tagline)
            taskItem.task_qr_url?.let { qr ->
                binding?.qrPreview?.load(qr)
            }
            binding?.etPriceType?.editText?.setText(taskItem.price_type)
            binding?.etTaskPrice?.setText(taskItem.task_earning_price.toString())
            binding?.etTaskSteps?.setText(taskItem.task_steps_to_follow)
            binding?.etTaskGuidelines?.setText(taskItem.task_guidelines)
            binding?.etTaskNote?.setText(taskItem.task_note)
            binding?.etTaskTrainingPageMessage?.setText(taskItem.training_note)
            binding?.etTaskTrainingVideoId?.setText(taskItem.training_video_ID)
            binding?.etPmInfo?.setText(taskItem.principal_info_note)
            binding?.etTotalImages?.setText(taskItem.no_of_images_proof.toString())

            taskItem.assessmentQuestions?.forEach { q ->
                val view = LayoutInflater.from(this)
                    .inflate(
                        R.layout.layout_question_item,
                        binding?.llAssessmentFormContainer,
                        false
                    )
                val questionStatement = view.findViewById<EditText>(R.id.et__question)
                val etTypeLayout = view.findViewById<TextInputLayout>(R.id.et_question_type)
                val etType: EditText? = etTypeLayout.editText
                val btnDelete: FloatingActionButton = view.findViewById(R.id.btn_delete_question)
                btnDelete.visibility = View.VISIBLE
                val etOptionsLayout: TextInputLayout? = view.findViewById(R.id.et_options_layout)
                (etTypeLayout?.editText as? AutoCompleteTextView)?.setText(q.question_type)
                (etType as? AutoCompleteTextView)?.setAdapter(questionTypeAdapter)

                etTypeLayout.editText?.doOnTextChanged { inputText, _, _, _ ->

                    if (inputText.toString() == "dropdown") {
                        Log.d("ValueChanged:", "Drop $inputText")
                        etOptionsLayout?.visibility = View.VISIBLE
                        etOptionsLayout?.editText?.setText(q.options?.joinToString(","))
                    } else {
                        Log.d("ValueChanged:", "$inputText")
                        etOptionsLayout?.visibility = View.GONE
                    }
                }
                questionStatement.setText(q.question_statement)
                if (q.question_type == "dropdown") {
                    etOptionsLayout?.visibility = View.VISIBLE
                    etOptionsLayout?.editText?.setText(q.options?.joinToString(","))
                } else {
                    etOptionsLayout?.visibility = View.GONE
                }
                btnDelete.setOnClickListener {
                    binding?.llAssessmentFormContainer?.removeView(view)
                }
                binding?.llAssessmentFormContainer?.addView(view)

            }

            taskItem.screeningQuestions?.forEach { q ->
                val view = LayoutInflater.from(this)
                    .inflate(
                        R.layout.layout_question_item,
                        binding?.llSubmissionFormContainer,
                        false
                    )
                val questionStatement = view.findViewById<EditText>(R.id.et__question)
                val etTypeLayout = view.findViewById<TextInputLayout>(R.id.et_question_type)
                val etType: EditText? = etTypeLayout.editText
                val btnDelete: FloatingActionButton = view.findViewById(R.id.btn_delete_question)
                btnDelete.visibility = View.VISIBLE
                val etOptionsLayout: TextInputLayout? = view.findViewById(R.id.et_options_layout)
                (etTypeLayout?.editText as? AutoCompleteTextView)?.setText(q.question_type)
                (etType as? AutoCompleteTextView)?.setAdapter(questionTypeAdapter)

                etTypeLayout.editText?.doOnTextChanged { inputText, _, _, _ ->

                    if (inputText.toString() == "dropdown") {
                        Log.d("ValueChanged:", "Drop $inputText")
                        etOptionsLayout?.visibility = View.VISIBLE
                        etOptionsLayout?.editText?.setText(q.options?.joinToString(","))
                    } else {
                        Log.d("ValueChanged:", "$inputText")
                        etOptionsLayout?.visibility = View.GONE
                    }
                }
                questionStatement.setText(q.question_statement)
                if (q.question_type == "dropdown") {
                    etOptionsLayout?.visibility = View.VISIBLE
                    etOptionsLayout?.editText?.setText(q.options?.joinToString(","))
                } else {
                    etOptionsLayout?.visibility = View.GONE
                }
                btnDelete.setOnClickListener {
                    binding?.llSubmissionFormContainer?.removeView(view)
                }
                binding?.llSubmissionFormContainer?.addView(view)

            }

            val galleryImage = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {

                binding?.qrPreview?.setImageURI(it)
                qrUri = it

            }
            binding?.btnSelectQr?.setOnClickListener {
                galleryImage.launch("image/*")
            }

            binding?.btnAddQuestionInSubmissionForm?.setOnClickListener {
                val no_of_questions = binding?.etSubmitQuestionNo?.text.toString().toIntOrNull()
                if (no_of_questions != null) {
                    for (i in 1..no_of_questions) {
                        val view = LayoutInflater.from(this)
                            .inflate(
                                R.layout.layout_question_item,
                                binding?.llSubmissionFormContainer,
                                false
                            )
                        val questionStatement = view.findViewById<EditText>(R.id.et__question)
                        val etTypeLayout = view.findViewById<TextInputLayout>(R.id.et_question_type)
                        val etType: EditText? = etTypeLayout.editText
                        val btnDelete: FloatingActionButton =
                            view.findViewById(R.id.btn_delete_question)
                        btnDelete.visibility = View.VISIBLE
                        val etOptionsLayout: TextInputLayout? =
                            view.findViewById(R.id.et_options_layout)
                        (etTypeLayout?.editText as? AutoCompleteTextView)?.setText("text")
                        (etType as? AutoCompleteTextView)?.setAdapter(questionTypeAdapter)

                        etTypeLayout.editText?.doOnTextChanged { inputText, _, _, _ ->

                            if (inputText.toString() == "dropdown") {
                                Log.d("ValueChanged:", "Drop $inputText")
                                etOptionsLayout?.visibility = View.VISIBLE
                            } else {
                                Log.d("ValueChanged:", "$inputText")
                                etOptionsLayout?.visibility = View.GONE
                            }
                        }
                        btnDelete.setOnClickListener {
                            binding?.llSubmissionFormContainer?.removeView(view)
                        }
                        binding?.llSubmissionFormContainer?.addView(view)
                    }
                    binding?.etSubmitQuestionNo?.text?.clear()
                    binding?.etSubmitQuestionNo?.clearFocus()
                }
            }
            binding?.btnAddQuestionInAssessmentForm?.setOnClickListener {
                val no_of_questions =
                    binding?.etTotalQuestionAssessment?.text.toString().toIntOrNull()
                if (no_of_questions != null) {
                    for (i in 1..no_of_questions) {
                        val view = LayoutInflater.from(this)
                            .inflate(
                                R.layout.layout_question_item,
                                binding?.llAssessmentFormContainer,
                                false
                            )
                        val questionStatement = view.findViewById<EditText>(R.id.et__question)
                        val etTypeLayout = view.findViewById<TextInputLayout>(R.id.et_question_type)
                        val etType: EditText? = etTypeLayout.editText
                        val btnDelete: FloatingActionButton =
                            view.findViewById(R.id.btn_delete_question)
                        btnDelete.visibility = View.VISIBLE
                        val etOptionsLayout: TextInputLayout? =
                            view.findViewById(R.id.et_options_layout)
                        (etTypeLayout?.editText as? AutoCompleteTextView)?.setText("text")
                        (etType as? AutoCompleteTextView)?.setAdapter(questionTypeAdapter)

                        etTypeLayout.editText?.doOnTextChanged { inputText, _, _, _ ->

                            if (inputText.toString() == "dropdown") {
                                Log.d("ValueChanged:", "Drop $inputText")
                                etOptionsLayout?.visibility = View.VISIBLE
                            } else {
                                Log.d("ValueChanged:", "$inputText")
                                etOptionsLayout?.visibility = View.GONE
                            }
                        }
                        btnDelete.setOnClickListener {
                            binding?.llAssessmentFormContainer?.removeView(view)
                        }
                        binding?.llAssessmentFormContainer?.addView(view)
                    }
                    binding?.etTotalQuestionAssessment?.text?.clear()
                    binding?.etTotalQuestionAssessment?.clearFocus()
                }
            }


            binding?.btnAddTask?.setOnClickListener {
                val taskTitle = binding?.etTaskTitle?.text.toString()
                val taskPrice = binding?.etTaskPrice?.text.toString()
                val taskTagline = binding?.etTaskTagline?.text.toString()
                val taskStepsToFollow = binding?.etTaskSteps?.text.toString()
                val taskGuidelines = binding?.etTaskGuidelines?.text.toString()
                val taskNote = binding?.etTaskNote?.text.toString()
                val trainingPageMessage = binding?.etTaskTrainingPageMessage?.text.toString()
                val trainingVideoId = binding?.etTaskTrainingVideoId?.text.toString()
                val totalImagesInSubmission = binding?.etTotalImages?.text.toString()
                val priceTagline = binding?.etPriceTagline?.text.toString()
                val principalNoteInfo = binding?.etPmInfo?.text.toString()

                if (taskTitle.isEmpty()) {
                    binding?.etTaskTitle?.requestFocus()
                    binding?.etTaskTitle?.error = "Empty"
                    return@setOnClickListener
                }
                if (taskTagline.isEmpty()) {
                    binding?.etTaskTagline?.requestFocus()
                    binding?.etTaskTagline?.error = "Empty"
                    return@setOnClickListener
                }
                if (priceTagline.isEmpty()) {
                    binding?.etPriceTagline?.requestFocus()
                    binding?.etPriceTagline?.error = "Empty"
                    return@setOnClickListener
                }
                if (taskPrice.isEmpty()) {
                    binding?.etTaskPrice?.requestFocus()
                    binding?.etTaskPrice?.error = "Empty"
                    return@setOnClickListener
                }
                if (taskStepsToFollow.isEmpty()) {
                    binding?.etTaskSteps?.requestFocus()
                    binding?.etTaskSteps?.error = "Empty"
                    return@setOnClickListener
                }
                if (taskGuidelines.isEmpty()) {
                    binding?.etTaskGuidelines?.requestFocus()
                    binding?.etTaskGuidelines?.error = "Empty"
                    return@setOnClickListener
                }
                if (taskNote.isEmpty()) {
                    binding?.etTaskNote?.requestFocus()
                    binding?.etTaskNote?.error = "Empty"
                    return@setOnClickListener
                }
                if (trainingPageMessage.isEmpty()) {
                    binding?.etTaskTrainingPageMessage?.requestFocus()
                    binding?.etTaskTrainingPageMessage?.error = "Empty"
                    return@setOnClickListener
                }
                if (trainingVideoId.isEmpty()) {
                    binding?.etTaskTrainingVideoId?.requestFocus()
                    binding?.etTaskTrainingVideoId?.error = "Empty"
                    return@setOnClickListener
                }
                if (totalImagesInSubmission.isEmpty()) {
                    binding?.etTotalImages?.requestFocus()
                    binding?.etTotalImages?.error = "Empty"
                    return@setOnClickListener
                }
                if (principalNoteInfo.isEmpty()) {
                    binding?.etPmInfo?.requestFocus()
                    binding?.etPmInfo?.error = "Empty"
                    return@setOnClickListener
                }
                val questionAssessmentList = mutableListOf<Question>()
                binding?.llAssessmentFormContainer?.children?.forEachIndexed { i, it ->
                    (it as? MaterialCardView)?.children?.forEach { mcq ->
                        //Statement
                        val etQuestion: EditText = mcq.findViewWithTag("et_question_statement")
                        val questionStatement = etQuestion.text.toString()
                        if (questionStatement.isEmpty()) {
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
                        if (questionType == QuestionType.Dropdown.type && (options.isEmpty() || options.size < 2)) {
                            etOptions.requestFocus()
                            etOptions.error = "at least 2 options requirred"
                            return@setOnClickListener
                        }
                        //New Question
                        val newQ = Question(
                            qid = i.toString(),
                            question_statement = questionStatement,
                            question_type = questionType,
                            options = options
                        )
                        questionAssessmentList.add(i, newQ)
                    }
                }
                if (questionAssessmentList.isEmpty()) {
                    Toast.makeText(this, "Assessment form is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val questionSubmissionList = mutableListOf<Question>()
                binding?.llSubmissionFormContainer?.children?.forEachIndexed { i, it ->
                    (it as? MaterialCardView)?.children?.forEach { mcq ->
                        //Statement
                        val etQuestion: EditText = mcq.findViewWithTag("et_question_statement")
                        val questionStatement = etQuestion.text.toString()
                        if (questionStatement.isEmpty()) {
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
                        if (questionType == QuestionType.Dropdown.type && (options.isEmpty() || options.size < 2)) {
                            etOptions.requestFocus()
                            etOptions.error = "at least 2 options requirred"
                            return@setOnClickListener
                        }
                        //New Question
                        val newQ = Question(
                            qid = i.toString(),
                            question_statement = questionStatement,
                            question_type = questionType,
                            options = options
                        )
                        questionSubmissionList.add(i, newQ)
                    }
                }
                if (questionSubmissionList.isEmpty()) {
                    Toast.makeText(this, "Submission form is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val progressBar = ProgressDialog(this)
                progressBar.setTitle("Uploading ")
                progressBar.setCancelable(false)
                progressBar.show()



                if (qrUri != null && qrUri != taskItem.task_qr_url!!.toUri() ) {
                    storageReference.child("Images/QR").child(taskItem.taskId!!).child("qr")
                        .putFile(qrUri!!).addOnSuccessListener { task ->
                            task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { urifs ->

                                //Submit Form
                                val task = TaskItem(
                                    jobId = taskItem.jobId,
                                    jobLogo = taskItem.jobLogo,
                                    taskId = taskItem.taskId,
                                    task_title = taskTitle,
                                    task_earning_price = taskPrice.toFloat(),
                                    task_tagline = taskTagline,
                                    price_tagline = priceTagline,
                                    task_guidelines = taskGuidelines,
                                    task_steps_to_follow = taskStepsToFollow,
                                    task_note = taskNote,
                                    screeningQuestions = questionSubmissionList,
                                    assessmentQuestions = questionAssessmentList,
                                    training_note = trainingPageMessage,
                                    training_video_ID = trainingVideoId,
                                    task_qr_url = urifs.toString(),
                                    price_type = selectedType,
                                    no_of_images_proof = totalImagesInSubmission.toInt(),
                                    principal_info_note = principalNoteInfo
                                )
                                db.reference.child("tasks").child(taskItem.jobId!!.trim()).child(taskItem.taskId!!.trim())
                                    .setValue(task).addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Task Added Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val finish = false
                                    progressBar.dismiss()
                                    finish()
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
                } else {
                    val task = TaskItem(
                        jobId = taskItem.jobId,
                        jobLogo = taskItem.jobLogo,
                        taskId = taskItem.taskId,
                        task_title = taskTitle,
                        task_earning_price = taskPrice.toFloat(),
                        task_tagline = taskTagline,
                        price_tagline = priceTagline,
                        task_guidelines = taskGuidelines,
                        task_steps_to_follow = taskStepsToFollow,
                        task_note = taskNote,
                        screeningQuestions = questionSubmissionList,
                        assessmentQuestions = questionAssessmentList,
                        training_note = trainingPageMessage,
                        training_video_ID = trainingVideoId,
                        task_qr_url = taskItem.task_qr_url,
                        price_type = selectedType,
                        no_of_images_proof = totalImagesInSubmission.toInt(),
                        principal_info_note = principalNoteInfo
                    )
                    db.reference.child("tasks").child(taskItem.jobId!!.trim()).child(taskItem.taskId!!.trim()).setValue(task)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT)
                                .show()
                            val finish = false

                            finish()
                        }
                }

            }
        }

    }
}