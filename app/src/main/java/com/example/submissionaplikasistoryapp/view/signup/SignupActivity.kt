package com.example.submissionaplikasistoryapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistoryapp.custom.CustomButton
import com.example.submissionaplikasistoryapp.custom.CustomEditText
import com.example.submissionaplikasistoryapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    private lateinit var customButton: CustomButton
    private lateinit var customEditText: CustomEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signupViewModel = ViewModelProvider(
            this
        )[SignupViewModel::class.java]

        customButton = binding.signupButton
        customEditText = binding.edRegisterPassword

        setupViewModel()
        setupAction()
        playAnimation()

        setMyButtonEnable()

        customEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
                //
            }
        })
    }

    private fun setMyButtonEnable(){
        val result = customEditText.text
        customButton.isEnabled = result != null && result.toString().isNotEmpty() && result.length > 7
    }

    private fun setupViewModel() {
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

    private fun setupAction() {
        signupViewModel.isLoading.observe(this){
            showLoading(it)
        }
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                else -> {
                    signupViewModel.registerAction(name, password, email)
                }
            }
            signupViewModel.isRegistered.observe(this){
                if (it) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Berhasil regis, silahkan masuk")
                        setPositiveButton("Lanjut") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Oops!")
                        setMessage("Maaf, regis gagal. Akun sudah ada.")
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
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signUp = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, nameEdit, email, emailEdit, password, passwordEdit, signUp)
            start()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}