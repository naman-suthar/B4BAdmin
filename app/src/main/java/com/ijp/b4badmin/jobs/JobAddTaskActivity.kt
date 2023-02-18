package com.ijp.b4badmin.jobs

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.ijp.b4badmin.R
import com.ijp.b4badmin.databinding.ActivityJobAddTaskBinding
import com.ijp.b4badmin.jobs.job_application.iterator
import com.ijp.b4badmin.model.Job
import com.ijp.b4badmin.model.Question
import com.ijp.b4badmin.model.QuestionType
import com.ijp.b4badmin.model.TaskItem

/**
 * This Activity is for adding task in job
 * */
class JobAddTaskActivity : AppCompatActivity() {
    private var binding: ActivityJobAddTaskBinding? = null
    private val db = FirebaseDatabase.getInstance()
    private val jobOptions = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobAddTaskBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        db.reference.child("Jobs").addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        jobOptions.clear()
                        for (job in snapshot.children){
                            val job = job.getValue(Job::class.java)
                            if (job != null){
                                jobOptions.add("${job.job_id}- ${job.job_title}")
                            }

                        }
                        val adapter = ArrayAdapter(
                            this@JobAddTaskActivity,
                            android.R.layout.simple_list_item_1, jobOptions
                        )
                        (binding?.etSelectJob?.editText as? AutoCompleteTextView)?.setText(jobOptions[0])
                        (binding?.etSelectJob?.editText as? AutoCompleteTextView)?.setAdapter(adapter)

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )

        val optionsTypes = listOf(
            QuestionType.Text.type,
            QuestionType.MultiLineText.type,
            QuestionType.Number.type,
            QuestionType.Boolean.type,
            QuestionType.Dropdown.type,
            QuestionType.Ratings.type,
            QuestionType.Photo.type
        )
        val questionTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, optionsTypes)
        binding?.btnGenerateAssessmentForm?.setOnClickListener {
            val questionNo = binding?.etTotalQuestionAssessment?.text.toString().toIntOrNull()

            if (questionNo != null) {
                binding?.llAssessmentFormContainer?.removeAllViews()
                for (i in questionNo) {

                    val view = LayoutInflater.from(this)
                        .inflate(R.layout.layout_question_item, binding?.llAssessmentFormContainer, false)
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
                    binding?.llAssessmentFormContainer?.addView(view)
                }
                binding?.etTotalQuestionAssessment?.text?.clear()
                binding?.etTotalQuestionAssessment?.clearFocus()
            }
            else{
                binding?.etTotalQuestionAssessment?.requestFocus()
                binding?.etTotalQuestionAssessment?.error = "Enter valid No"
            }

        }
        binding?.btnGenerateSubmissionForm?.setOnClickListener {
            val questionNo = binding?.etSubmitQuestionNo?.text.toString().toIntOrNull()

            if (questionNo != null) {
                binding?.llSubmissionFormContainer?.removeAllViews()
                for (i in questionNo) {

                    val view = LayoutInflater.from(this)
                        .inflate(R.layout.layout_question_item, binding?.llSubmissionFormContainer, false)
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
                    binding?.llSubmissionFormContainer?.addView(view)
                }
                binding?.etSubmitQuestionNo?.text?.clear()
                binding?.etSubmitQuestionNo?.clearFocus()
            }
            else{
                binding?.etSubmitQuestionNo?.requestFocus()
                binding?.etSubmitQuestionNo?.error = "Enter valid No"
            }

        }
        binding?.btnAddTask?.setOnClickListener {
            val jobId = binding?.etSelectJob?.editText?.text.toString().split("-")[0]
            val taskId = binding?.etTaskId?.text.toString()
            val taskTitle = binding?.etTaskTitle?.text.toString()
            val taskPrice = binding?.etTaskPrice?.text.toString()
            val taskTagline = binding?.etTaskTagline?.text.toString()
            val taskStepsToFollow = binding?.etTaskSteps?.text.toString()
            val taskGuidelines = binding?.etTaskGuidelines?.text.toString()
            val taskNote = binding?.etTaskNote?.text.toString()
            val trainingPageMessage = binding?.etTaskTrainingPageMessage?.text.toString()
            val trainingVideoId = binding?.etTaskTrainingVideoId?.text.toString()
            if (jobId.isEmpty()){
                binding?.etSelectJob?.editText?.requestFocus()
                binding?.etSelectJob?.editText?.error = "Empty"
            }
            if (taskId.isEmpty()){
                binding?.etTaskId?.requestFocus()
                binding?.etTaskId?.error = "Empty"
                return@setOnClickListener
            }
            if (taskTitle.isEmpty()){
                binding?.etTaskTitle?.requestFocus()
                binding?.etTaskTitle?.error = "Empty"
                return@setOnClickListener
            }
            if (taskTagline.isEmpty()){
                binding?.etTaskTagline?.requestFocus()
                binding?.etTaskTagline?.error = "Empty"
                return@setOnClickListener
            }
            if (taskPrice.isEmpty()){
                binding?.etTaskPrice?.requestFocus()
                binding?.etTaskPrice?.error = "Empty"
                return@setOnClickListener
            }
            if (taskStepsToFollow.isEmpty()){
                binding?.etTaskSteps?.requestFocus()
                binding?.etTaskSteps?.error = "Empty"
                return@setOnClickListener
            }
            if (taskGuidelines.isEmpty()){
                binding?.etTaskGuidelines?.requestFocus()
                binding?.etTaskGuidelines?.error = "Empty"
                return@setOnClickListener
            }
            if (taskNote.isEmpty()){
                binding?.etTaskNote?.requestFocus()
                binding?.etTaskNote?.error = "Empty"
                return@setOnClickListener
            }
            if (trainingPageMessage.isEmpty()){
                binding?.etTaskTrainingPageMessage?.requestFocus()
                binding?.etTaskTrainingPageMessage?.error = "Empty"
                return@setOnClickListener
            }
            if (trainingVideoId.isEmpty()){
                binding?.etTaskTrainingVideoId?.requestFocus()
                binding?.etTaskTrainingVideoId?.error = "Empty"
                return@setOnClickListener
            }
            val questionAssessmentList = mutableListOf<Question>()
            binding?.llAssessmentFormContainer?.children?.forEachIndexed {i, it->
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
                    questionAssessmentList.add(i,newQ)
                }
            }
            if (questionAssessmentList.isEmpty()){
                Toast.makeText(this, "Assessment form is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val questionSubmissionList = mutableListOf<Question>()
            binding?.llSubmissionFormContainer?.children?.forEachIndexed {i, it->
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
                    questionSubmissionList.add(i,newQ)
                }
            }
            if (questionSubmissionList.isEmpty()){
                Toast.makeText(this, "Submission form is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = TaskItem(
                jobId = jobId,
                taskId = taskId,
                task_title = taskTitle,
                task_earning_price = taskPrice.toInt(),
                task_tagline = taskTagline,
                task_guidelines = taskGuidelines,
                task_steps_to_follow = taskStepsToFollow,
                task_note = taskNote,
                screeningQuestions = questionSubmissionList,
                assessmentQuestions = questionAssessmentList,
                training_note = trainingPageMessage,
                training_video_ID = trainingVideoId
            )
            db.reference.child("tasks").child(jobId).child(taskId).setValue(task).addOnSuccessListener {
                Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                val finish = false
                /*db.reference.child("Jobs").child(jobId).runTransaction(
                    object : Transaction.Handler{
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            currentData?.value?.let {
                                val job = currentData?.getValue(Job::class.java)
                                val activeTasks: MutableList<TaskItem> = job?.associatedTasks ?: mutableListOf()
                                activeTasks.
                            }
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            TODO("Not yet implemented")
                        }
                    }
                )*/
                finish()
            }
            Log.d("Success","$task")
        }
    }
}