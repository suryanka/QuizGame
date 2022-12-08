package com.ghosh.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ghosh.quizgame.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log
import kotlin.math.sign

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    val auth: FirebaseAuth= FirebaseAuth.getInstance()

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding= ActivityLoginBinding.inflate(layoutInflater)
        val view= loginBinding.root
        setContentView(view)

        val textOfGoogleSignIn = loginBinding.ButtonLoginGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleSignIn.text="Continue with Google"
        textOfGoogleSignIn.setTextColor(Color.BLACK)
        textOfGoogleSignIn.textSize =  18F

        //RegisterActivity
        registerActivityForGoogleSignIn()

        loginBinding.buttonLoginSignIn.setOnClickListener {
            val email= loginBinding.editTextLoginEmail.text.toString()
            val password= loginBinding.editTextLoginPassword.text.toString()

            signInWithEmail(email,password)

        }
        loginBinding.ButtonLoginGoogleSignIn.setOnClickListener {
            signInGoogle()
        }
        loginBinding.textViewSignUp.setOnClickListener {
            val intent= Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }
        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent=Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    fun signInWithEmail(email: String, password:String)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful)
            {
                Toast.makeText(applicationContext,"Welcome to quiz Game", Toast.LENGTH_LONG).show()
                val intent=Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(applicationContext, task.exception?.localizedMessage.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val user= auth.currentUser
        if(user!=null)
        {
            Toast.makeText(applicationContext,"Welcome to quiz Game", Toast.LENGTH_LONG).show()
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInGoogle()
    {
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("158803966325-60b3k3ltgamppdttc4t488j3ppng6ogv.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        signIn()
    }

    private fun signIn()
    {
        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun registerActivityForGoogleSignIn()
    {

        activityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result->
            val resultCode= result.resultCode
            val data= result.data
            if (resultCode== RESULT_OK && data!=null)
            {
                val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                firebaseSignInWithGoogle(task)
            }
        })
    }

    private fun firebaseSignInWithGoogle(task : Task<GoogleSignInAccount>)
    {
        try{
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, " Welcome to Quiz Game", Toast.LENGTH_LONG).show()
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }
        catch(e: ApiException){
            Toast.makeText(applicationContext, e.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseGoogleAccount(account : GoogleSignInAccount)
    {
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task->
            if(task.isSuccessful)
            {
//                val user= auth.currentUser

            }
            else{

            }
        }

    }



}