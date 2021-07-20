package com.example.batsumiv2

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*

class MainActivity() : AppCompatActivity() {
    private var contactsArrayList: ArrayList<Contacts>? = null
    private var UsercontactRV: RecyclerView? = null
    private var contactRecVAdapter: ExampleAdapter? = null
    private var loadingProgBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactsArrayList = ArrayList()
        UsercontactRV = findViewById(R.id.userContacts)
        val addNewContact = findViewById<FloatingActionButton>(R.id.ButtonAdd)
        loadingProgBar = findViewById(R.id.LoadingID)
        prepareContactRecV()
        requestPermissions()
        addNewContact.setOnClickListener {
            val i = Intent(this@MainActivity, NewUsercontact::class.java)
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchViewItem = menu.findItem(R.id.app_bar_search)
        val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText.toLowerCase())
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filter(text: String) {
        val filteredlist = ArrayList<Contacts>()
        for (item: Contacts in contactsArrayList!!) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show()
        } else {
            contactRecVAdapter!!.filterList(filteredlist)
        }
    }

    private fun prepareContactRecV() {
        contactRecVAdapter = contactsArrayList?.let { ExampleAdapter(this, it) }
        //on below line we are setting layout mnager.
        UsercontactRV!!.layoutManager = LinearLayoutManager(this)
        UsercontactRV!!.adapter = contactRecVAdapter
    }

    private fun requestPermissions() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS, Manifest.permission.WRITE_CONTACTS
        ).withListener(object : MultiplePermissionsListener {

            override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    contacts
                    Toast.makeText(
                        this@MainActivity,
                        "All the permissions are granted..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                    // permission is denied permanently,
                    // we will show user a dialog message.
                    showSettingsDialog()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                list: List<com.karumi.dexter.listener.PermissionRequest>,
                permissionToken: PermissionToken
            ) {
                permissionToken.continuePermissionRequest()
            }
        }).withErrorListener(PermissionRequestErrorListener {
            Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
        }).onSameThread().check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Need Permissions")

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

                dialog.cancel()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, 101)
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

                dialog.cancel()
            }
        })

        builder.show()
    }

    val contacts: Unit
        get() {
            var contactId = ""
            var displayName: String = ""
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            if (cursor!!.count > 0) {
                while (cursor.moveToNext()) {
                    val hasPhoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt()
                    if (hasPhoneNumber > 0) {
                        contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        displayName =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )
                        if (phoneCursor!!.moveToNext()) {
                            val phoneNumber =
                                phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactsArrayList!!.add(Contacts(displayName, phoneNumber))
                        }
                        phoneCursor.close()
                    }
                }
            }
            //on below line we are closing our cursor.
            cursor.close()
            //on below line we are hiding our progress bar and notifying our adapter class.
            loadingProgBar!!.visibility = View.GONE
            contactRecVAdapter!!.notifyDataSetChanged()
        }


}

