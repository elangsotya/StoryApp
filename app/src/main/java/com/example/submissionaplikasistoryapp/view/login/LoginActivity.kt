package com.example.submissionaplikasistoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistoryapp.custom.CustomButton
import com.example.submissionaplikasistoryapp.custom.CustomEditText
import com.example.submissionaplikasistoryapp.view.main.MainActivity
import com.example.submissionaplikasistoryapp.databinding.ActivityLoginBinding
import com.example.submissionaplikasistoryapp.model.UserModel
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.view.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    private lateinit var customButton: CustomButton
    private lateinit var customEditText: CustomEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customButton = binding.loginButton
        customEditText = binding.edLoginPassword

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
        setMyButtonEnable()

        customEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { setMyButtonEnable() }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setMyButtonEnable(){
        val result = customEditText.text
        customButton.isEnabled = result != null && result.toString().isNotEmpty() && result.length > 7
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel(){
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
        loginViewModel.isLoading.observe(this){
            showLoading(it)
        }
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                else -> {
                    loginViewModel.loginAction(email, password)
                }
            }
            loginViewModel.isLogin.observe(this){
                if (it) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Berhasil login, silahkan masuk")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    binding.passwordEditTextLayout.error = "Password tidak sesuai"
                    binding.emailEditTextLayout.error = "Email tidak sesuai"
                    AlertDialog.Builder(this).apply {
                        setTitle("Oops!")
                        setMessage("Maaf, login gagal. Cek kembali")
                        setPositiveButton("Kembali") { _, _ ->
                        }
                        create()
                        show()
                    }

                }
            }
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, login)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}