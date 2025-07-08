package com.project.farmingapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.project.farmingapp.R
import com.project.farmingapp.databinding.ActivityLoginBinding
import com.project.farmingapp.utilities.toast
import com.project.farmingapp.view.dashboard.DashboardActivity
import com.project.farmingapp.viewmodel.AuthListener
import com.project.farmingapp.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity(), AuthListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: AuthViewModel
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.authViewModel = viewModel
        viewModel.authListener = this

        // Auto-login if user already signed in
        firebaseAuth.currentUser?.let {
            navigateToDashboard()
        }

        binding.createaccountText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.signGoogleBtnLogin.setOnClickListener {
            signIn()
        }

        binding.forgotPasswdTextLogin.setOnClickListener {
            val email = binding.emailEditLogin.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reset email sent!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message ?: "Failed to send email", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.returnActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Exit app
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    override fun onStarted() {
        binding.progressLogin.visibility = View.VISIBLE
    }

    override fun onSuccess(authRepo: LiveData<String>) {
        authRepo.observe(this) {
            binding.progressLogin.visibility = View.GONE
            if (it == "Success") {
                toast("Logged In")
                navigateToDashboard()
            }
        }
    }

    override fun onFailure(message: String) {
        binding.progressLogin.visibility = View.GONE
        toast("Login failed: $message")
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
