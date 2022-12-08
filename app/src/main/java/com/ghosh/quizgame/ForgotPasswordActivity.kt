package com.ghosh.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ghosh.quizgame.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding
    val auth:FirebaseAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForgotPasswordBinding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        val  view=activityForgotPasswordBinding.root
        setContentView(view)

        activityForgotPasswordBinding.buttonLoginForgotReset.setOnClickListener {
            val userEmail = activityForgotPasswordBinding.editTextLoginForgotEmail.text.toString()

            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    Toast.makeText(applicationContext,"We sent a password reset email to your email account",Toast.LENGTH_LONG).show()
                    finish()
                }
                else
                {
                    Toast.makeText(applicationContext,
                        task.exception?.localizedMessage.toString(),Toast.LENGTH_LONG).show()
                }
            }



        }
    }
}