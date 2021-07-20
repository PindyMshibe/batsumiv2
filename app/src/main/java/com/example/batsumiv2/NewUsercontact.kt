package com.example.batsumiv2

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
//import com.wintech.myapplication.R
import android.text.TextUtils
import android.widget.Toast
import android.content.Intent
import android.provider.ContactsContract
import android.app.Activity
import android.view.View
import android.widget.Button
//import com.wintech.myapplication.MainActivity
//import kotlinx.android.synthetic.main.activity_create_new_contact.*

class NewUsercontact : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_usercontact)
        val AddUserContact = findViewById<Button>(R.id.AddUserContact)


        AddUserContact.setOnClickListener(View.OnClickListener {
            //on below line we are getting text from our edit text.
            val usersName = findViewById<EditText>(R.id.usersName)
            val usersNumber = findViewById<EditText>(R.id.usersNumber)
            val usersEmail = findViewById<EditText>(R.id.usersEmail)

            val name = usersName.getText().toString()
            val phone = usersNumber.getText().toString()
            val email = usersEmail.getText().toString()

            //on below line we are making a text validation.
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
                Toast.makeText(this@NewUsercontact, "Please enter the data in all fields. ", Toast.LENGTH_SHORT).show()
            } else {
                //calling a method to add contact.
                addContact(name, email, phone)
            }
        })
    }

    private fun addContact(name: String, email: String, phone: String) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent
            .putExtra(ContactsContract.Intents.Insert.NAME, name)
            .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Contact has been added.", Toast.LENGTH_SHORT).show()
                val i = Intent(this@NewUsercontact, MainActivity::class.java)
                startActivity(i)
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled Added Contact",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}