package com.example.batsumiv2

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object Screenshot {
    fun getScreenShot(view: View): Bitmap {
        val ViewScreen = view.rootView
        ViewScreen.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(ViewScreen.drawingCache)
        ViewScreen.isDrawingCacheEnabled = false
        return bitmap
    }

    /*  Create Directory where screenshot will save for sharing screenshot  */
    fun getMainDirectoryName(context: Context): File {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        val mainDirectory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo")

        //If File is not present create directory
        if (!mainDirectory.exists()) {
            if (mainDirectory.mkdir()) Log.e("Create Directory", "Main Directory Created : $mainDirectory")
        }
        return mainDirectory
    }

    /*  Store taken screenshot into above created path  */
    fun store(bitm: Bitmap, fileName: String?, saveFilePath: File): File {
        val directory = File(saveFilePath.absolutePath)
        if (!directory.exists()) directory.mkdirs()
        val file = File(saveFilePath.absolutePath, fileName)
        try {
            val fileOut = FileOutputStream(file)
            bitm.compress(Bitmap.CompressFormat.JPEG, 85, fileOut)
            fileOut.flush()
            fileOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }
}