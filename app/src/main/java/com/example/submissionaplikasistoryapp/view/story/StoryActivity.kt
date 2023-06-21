package com.example.submissionaplikasistoryapp.view.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistoryapp.config.ApiConfig
import com.example.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.example.submissionaplikasistoryapp.databinding.ActivityStoryBinding
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.FileUploadResponse
import com.example.submissionaplikasistoryapp.util.createCustomTempFile
import com.example.submissionaplikasistoryapp.util.uriToFile
import com.example.submissionaplikasistoryapp.view.ViewModelFactory
import com.example.submissionaplikasistoryapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private var getFile: File? = null
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var fusedLocationProvideClient: FusedLocationProviderClient

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProvideClient = LocationServices.getFusedLocationProviderClient(this)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
        setupViewModel()
    }

    private fun setupViewModel(){
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreferences.getInstance(dataStore))
        )[StoryViewModel::class.java]
    }

    private fun setupAction(){
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener {
            storyViewModel.getUser().observe(this) {
                val token = it.token
                uploadImage("Bearer $token")
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGalery.launch(chooser)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoryActivity,
                "com.example.submissionaplikasistoryapp.view.story",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun uploadImage(token: String){
        if(getFile != null){
            val task = fusedLocationProvideClient.lastLocation
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
                return
            }
            var lat: Double?
            var lon: Double?
            task.addOnSuccessListener { location : Location? ->
                if (location != null){
                    lat = location.latitude
                    lon = location.longitude
                    val file = reduceFileImage(getFile as File)
                    val desc = binding.edAddDescription.text.toString()
                    val description = desc.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    val apiService = ApiConfig.getApiService()
                    val uploadImageRequest = apiService.uploadImage(token, imageMultipart, description, lat, lon)
                    uploadImageRequest.enqueue(object : Callback<FileUploadResponse> {
                        override fun onResponse(
                            call: Call<FileUploadResponse>,
                            response: Response<FileUploadResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null && !responseBody.error!!) {
                                    Toast.makeText(this@StoryActivity, "Upload Story with Location Successfully", Toast.LENGTH_SHORT).show()
                                    Intent(this@StoryActivity,MainActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                            } else {
                                Toast.makeText(this@StoryActivity, response.message(), Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                            Toast.makeText(this@StoryActivity, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        } else{
            Toast.makeText(this@StoryActivity, "Silahkan masukkan berkas gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                reduceFileImage(file)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGalery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@StoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do{
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}