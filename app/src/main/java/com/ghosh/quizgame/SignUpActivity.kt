package com.ghosh.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ghosh.quizgame.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var signUpBinding: ActivitySignUpBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding=ActivitySignUpBinding.inflate(layoutInflater)
        val view= signUpBinding.root
        setContentView(view)

        signUpBinding.buttonSignUpSignIn.setOnClickListener {
            val email= signUpBinding.editTextSignUpEmail.text.toString()
            val password=signUpBinding.editTextSignUpPassword.text.toString()
            signUpWithFirebase(email, password)
        }
    }

    fun signUpWithFirebase( email: String,password:String)
    {
        signUpBinding.signUpProgressBar.visibility= View.VISIBLE
        signUpBinding.buttonSignUpSignIn.isClickable=false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Your account has been created",Toast.LENGTH_LONG).show()
                finish()
                signUpBinding.signUpProgressBar.visibility= View.INVISIBLE
                signUpBinding.buttonSignUpSignIn.isClickable=true
            }
            else{
                Toast.makeText(applicationContext, task.exception?.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}