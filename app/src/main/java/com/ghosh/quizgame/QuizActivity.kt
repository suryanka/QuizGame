package com.ghosh.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ghosh.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    val database= FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("Questions")

    var question=""
    var answerA=""
    var answerB=""
    var answerC=""
    var answerD=""
    var correctanswer=""
    var questionCount=0
    var questionNumber=0

    var userAnswer=""
    var userCorrect=0
    var userWrong=0

    lateinit var  timer: CountDownTimer
    private  val totalTime = 25000L
    var timerContinue=false
    var leftTime= totalTime

    val auth= FirebaseAuth.getInstance()
    val user= auth.currentUser
    val scoreref= database.reference

    val questions=HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding=ActivityQuizBinding.inflate(layoutInflater)
        val view= quizBinding.root
        setContentView(view)

        do{
            val number= Random.nextInt(1,11)
            Log.d("number", number.toString())
            questions.add(number)
        }while(questions.size < 5)

        Log.d("numberOfQuestions", questions.toString())


        gameLogic()

        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }
        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }
        quizBinding.textViewA.setOnClickListener {
            pauseTimer()
            userAnswer="a"
            if(userAnswer==correctanswer)
            {
                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.TextViewCorrect.text=userCorrect.toString()
            }
            else{
                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.TextViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
        quizBinding.textViewB.setOnClickListener {
            pauseTimer()
            userAnswer="b"
            if(userAnswer==correctanswer)
            {
                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.TextViewCorrect.text=userCorrect.toString()
            }
            else{
                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.TextViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()
        }
        quizBinding.textViewC.setOnClickListener {
            pauseTimer()
            userAnswer="c"
            if(userAnswer==correctanswer)
            {
                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.TextViewCorrect.text=userCorrect.toString()
            }
            else{
                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.TextViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()
        }
        quizBinding.textViewD.setOnClickListener {
            pauseTimer()
            userAnswer="d"
            if(userAnswer==correctanswer)
            {
                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.TextViewCorrect.text=userCorrect.toString()
            }
            else{
                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.TextViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
    }

    private fun gameLogic()
    {
        restoreOptions()
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount=snapshot.childrenCount.toInt()

                if(questionNumber<questions.size)
                {
                    question=snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA=snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB=snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC=snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD=snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctanswer=snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text=question
                    quizBinding.textViewA.text=answerA
                    quizBinding.textViewB.text=answerB
                    quizBinding.textViewC.text=answerC
                    quizBinding.textViewD.text=answerD

                    quizBinding.progressBar.visibility=View.INVISIBLE
                    quizBinding.LinearLayoutInfo.visibility=View.VISIBLE
                    quizBinding.linearLayoutButton.visibility=View.VISIBLE
                    quizBinding.LinearLayoutQuestion.visibility=View.VISIBLE

                    startTimer()


                }
                else
                {
                    //disableClickableOptions()
                   // Toast.makeText(applicationContext,"You answered all questions", Toast.LENGTH_LONG).show()

                    var dialogMessage=  AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("Congratulations!!\nYou have answerwed all the questions\nDo you want to see the result")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See result"){dialogWindow,position->
                         sendScore()
                    }

                    dialogMessage.setNegativeButton("Play Again"){dialogWindow,position->

                        val intent= Intent(this@QuizActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    dialogMessage.create().show()


                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }

    fun findAnswer()
    {
        when(correctanswer)
        {
            "a"-> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b"-> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c"-> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d"-> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    fun disableClickableOptions()
    {
        quizBinding.textViewA.isClickable=false
        quizBinding.textViewB.isClickable=false
        quizBinding.textViewC.isClickable=false
        quizBinding.textViewD.isClickable=false
    }

    fun restoreOptions()
    {
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable=true
        quizBinding.textViewB.isClickable=true
        quizBinding.textViewC.isClickable=true
        quizBinding.textViewD.isClickable=true

    }

    private fun startTimer()
    {
        timer= object:CountDownTimer(leftTime,1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                leftTime=millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text =
                    "Sorry ! time is up! Continue with next question."
                timerContinue = false
            }
        }.start()

        timerContinue=true
    }

    fun updateCountDownText()
    {
        var timeRemaining : Int= (leftTime/1000).toInt()
        quizBinding.TextViewTime.text=timeRemaining.toString()

    }

    fun pauseTimer()
    {
        timer.cancel()
        timerContinue=false
    }

    fun resetTimer()
    {
        pauseTimer()
        leftTime=totalTime
        updateCountDownText()
    }

    fun sendScore()
    {

        user?.let {
            val userUID= it.uid
            scoreref.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreref.child("scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {
                Toast.makeText(applicationContext,"Scores sent to database.",Toast.LENGTH_LONG).show()
                val intent= Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}