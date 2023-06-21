package com.example.submissionaplikasistoryapp.view.main

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionaplikasistoryapp.R
import com.example.submissionaplikasistoryapp.adapter.AllStoriesAdapter
import com.example.submissionaplikasistoryapp.adapter.LoadingStateAdapter
import com.example.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.example.submissionaplikasistoryapp.view.maps.MapsActivity
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import com.example.submissionaplikasistoryapp.view.story.StoryActivity
import com.example.submissionaplikasistoryapp.view.ViewModelFactory
import com.example.submissionaplikasistoryapp.view.detail.DetailActivity
import com.example.submissionaplikasistoryapp.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: AllStoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val barAction = supportActionBar
        barAction!!.title = "List Stories"


        setupView()
        setupViewModel()
        setupAction()
        setListStories()

        mainViewModel.getUser().observe(this) { user ->
            if (!user.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else{
                val layoutManager = LinearLayoutManager(this)
                binding.rvStories.layoutManager = layoutManager
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.story.observe(this){
            adapter.submitData(lifecycle, it)
            if(it == null){
                Log.e(TAG, "onFailure: ")
            }
        }
        adapter.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_maps_story ->{
                Intent(this@MainActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.action_add_story ->{
                Intent(this@MainActivity,StoryActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.action_logout ->{
                mainViewModel.logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

    }

    private fun setupAction() {
        adapter = AllStoriesAdapter()

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setListStories(){
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        adapter.setOnItemClickCallback(object : AllStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                val id = story.id
                val intentToDetail = Intent(this@MainActivity, DetailActivity::class.java).putExtra(DetailActivity.EXTRA_ID, id)
                startActivity(intentToDetail)
                finish()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}