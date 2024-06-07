package com.tidycity.code.views

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.tidycity.code.R
import com.tidycity.code.firebase_utils.FirebaseDeclarations.firebaseAuth
import com.tidycity.code.gps_utils.GPService
import com.tidycity.code.utilities.Extensions.checkGpsStatus
import com.tidycity.code.utilities.Extensions.enableGPS
import com.tidycity.code.utilities.Extensions.storedInfo
import com.tidycity.code.utilities.NetworkMonitoring
import com.tidycity.code.webservices_utils.ExtensionsRetrofit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class PodUploadingActivity : AppCompatActivity() {

    private lateinit var displayUsername: TextView
    private lateinit var recordData: Button
    private lateinit var showHistory: Button
    private lateinit var settings: Button
    private lateinit var switchView: Button

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PERMISSIONS = 2
    private lateinit var currentPhotoPath: String

    private var userEmail: String? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pod_uploading)

        NetworkMonitoring(this@PodUploadingActivity).enable()

        recordData = findViewById(R.id.btnCamera)
        showHistory = findViewById(R.id.btnHistory)
        settings = findViewById(R.id.btnSettings)
        displayUsername = findViewById(R.id.userName)

        // get the email of current user until "@"
        val extras = intent.extras
        if (extras != null) {
            username = extras.getString("username")
            userEmail = extras.getString("email")
        }

        displayUsername.text = username

        recordData.setOnClickListener {
            ExtensionsRetrofit(this@PodUploadingActivity)
                .uploadLedger()

        }
        showHistory.setOnClickListener {

                // Trigger the camera intent
                dispatchTakePictureIntent()

        }

        settings.setOnClickListener {
//            showMenu()
        }

        switchView = findViewById(R.id.btnHelp)
        switchView.setOnClickListener {
            val intent = Intent(this, CreatePodAccountActivity::class.java)
            startActivity(intent)
        }

        val btn1: Button = findViewById<Button>(R.id.btnHelp1)
        btn1.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        val btn2: Button = findViewById<Button>(R.id.btnHelp2)
        btn2.setOnClickListener {
            val intent = Intent(this, UserPodFiles::class.java)
            startActivity(intent)
        }


    }

//    private fun showUploadDialog() {
//        runOnUiThread {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle(getString(R.string.alert_description))
//            builder.setMessage(getString(R.string.videos_to_upload))
//            builder.setCancelable(false)
//
//            builder.setPositiveButton(getString(R.string.leave_it)) { _, _ ->
//                if (!isInternetEnabled())
//                    toast("Your internet connectivity doesn't match with your settings")
////                else uploadContent(videosToUpload)
//            }
//            builder.setNegativeButton(getString(R.string.not_now)) { _, _ -> }
//            builder.show()
//        }
//    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            uploadImage(file)
        }
    }

    private fun uploadImage(file: File) {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
        ExtensionsRetrofit(this@PodUploadingActivity).uploadPicPod(multipartBody)
//        val call = apiService.uploadImage(
//            url = "https://nfp.dume-arditi.com/theia-vision/01Mar2024/Inicio_11h20min_Fim_11h44min/Placeholder1.jpg",
//            authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ3ZWJJZCI6Imh0dHBzOi8vYW5kcm9pZDEyMy5kdW1lLWFyZGl0aS5jb20vcHJvZmlsZS9jYXJkI21lIiwiaWF0IjoxNzE3Njg2NjEyLCJleHAiOjE3MTc5NDU4MTJ9.hYhunFqX9rtnICbJ0TSJBbTUOLHJj5o5_zAU8fJUH8o",
//            contentType = "image/jpeg",
//            file = multipartBody
//        )
//
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    // Handle the successful response here
//                    println("Response: ${response.body()?.string()}")
//                } else {
//                    // Handle the error response here
//                    println("Error: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                // Handle the failure here
//                println("Failure: ${t.message}")
//            }
//        })
    }


}