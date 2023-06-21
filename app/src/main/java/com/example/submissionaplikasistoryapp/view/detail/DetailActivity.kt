package com.example.submissionaplikasistoryapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submissionaplikasistoryapp.databinding.ActivityDetailBinding
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.view.ViewModelFactory
import com.example.submissionaplikasistoryapp.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    companion object{
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val barAction = supportActionBar
        barAction!!.title = "Story Detail"

        setupView()
        setupViewModel()
        setupAction()
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
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
    }

    private fun setupViewModel(){
        detailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreferences.getInstance(dataStore))
        )[DetailViewModel::class.java]
    }

    private fun setupAction(){
        detailViewModel.getUser().observe(this){ it ->
            val token = it.token
            val getIntentId = intent.getStringExtra(EXTRA_ID)
            getIntentId?.let { detailViewModel.setDetailStory("Bearer $token", it) }
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.getDetailStory().observe(this){
            binding.tvDetailName.text = it.name.toString()
            binding.tvDetailDescription.text = it.description.toString()
            Glide.with(this@DetailActivity)
                .load(it.photoUrl)
                .into(binding.ivDetailPhoto)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}