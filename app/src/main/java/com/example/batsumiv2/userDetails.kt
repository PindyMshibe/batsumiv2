package com.example.batsumiv2

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
//import kotlinx.android.synthetic.main.activity_contact_detail.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class userDetails : AppCompatActivity() {

    private var userName: String? = null
    private var userNumber: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val userCall = findViewById<ImageView>(R.id.userCall)
        val userMessage = findViewById<ImageView>(R.id.UserMessage)
        val userShare = findViewById<ImageView>(R.id.userShare)
        val shareImage = findViewById<Button>(R.id.shareImage)
        userName = intent.getStringExtra("name")
        userNumber = intent.getStringExtra("contact")
        userCall.setOnClickListener(View.OnClickListener {
            makeCall(userNumber)
        })
        userMessage.setOnClickListener(View.OnClickListener {
            sendMessage(userNumber)
        })
        userShare.setOnClickListener(
            View.OnClickListener {
                shareContact()
            })
        userDetails.verifyStoragePermission(this)
        shareImage.setOnClickListener(View.OnClickListener { takeScreenShot(window.decorView) })
    }

    private fun sendMessage(contactNumber: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$contactNumber"))
        intent.putExtra("sms_body", "Enter your messaage")
        startActivity(intent)
    }
    private fun shareContact(){
        val shareIntent:Intent = Intent().apply { action= Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,userNumber)
            type = "text/plain"}
        startActivity(shareIntent)
    }

    private fun makeCall(contactNumber: String?) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$contactNumber")
        if (ActivityCompat.checkSelfPermission(this@userDetails,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startActivity(callIntent)
    }

    private fun takeScreenShot(view: View) {
        val date = Date()
        val format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date)
        try {
            val mainDir = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare")
            if (!mainDir.exists()) {
                val mkdir = mainDir.mkdir()
            }
            val path = "$mainDir/TrendOceans-$format.jpeg"
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            val imageFile = File(path)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            shareScreenShot(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Share ScreenShot
    private fun shareScreenShot(imageFile: File) {
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + "." + localClassName + ".provider",
            imageFile)
        Log.d("buildData", BuildConfig.APPLICATION_ID + "." + localClassName + ".provider")
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TEXT, "Download Application from Instagram")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        //Permissions Check
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun verifyStoragePermission(activity: Activity?) {
            val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE)
            }
        }
    }
}