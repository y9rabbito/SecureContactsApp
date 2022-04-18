package com.rabbito.securecontacts

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_generate_fake_call_log.*
import java.text.SimpleDateFormat
import java.util.*


class GenerateFakeCallLog : AppCompatActivity() {

    var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_fake_call_log)
        editNumber.setOnClickListener {
            var i = Intent(Intent.ACTION_PICK)
            i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(i, 123)
        }

        editDate.setOnClickListener {
            var c = Calendar.getInstance()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    date = "$dayOfMonth/${month + 1}/$year"

                    TimePickerDialog(
                        this,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            date += " $hourOfDay:$minute"
                            editDate.setText(date)
                        },
                        c.get(Calendar.HOUR),
                        c.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonMakeEntry.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CALL_LOG
                ) != PackageManager.PERMISSION_GRANTED
            )   //if permission isn't available then request for same
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_CALL_LOG),
                    112
                )
            else {
                try {
                    if (editNumber.text.toString().isEmpty() || editDate.text.toString()
                            .isEmpty() || editDuration.text.toString().isEmpty()
                    ) {
                        Toast.makeText(this, "Please fill the all fields", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        writeCalLog()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Something Wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 112 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            writeCalLog()
        } else {
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val contacturi = data?.data ?: return
            val proj = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            var rs = contentResolver.query(contacturi, proj, null, null, null)
            if (rs?.moveToFirst()!!) {
                editNumber.setText(rs.getString(0))
            }
        }
    }


    fun writeCalLog() {

        var type = 1
        var call_type = spinner.selectedItem.toString()
        type = when (call_type) {
            "Incoming" -> 1
            "Outgoing" -> 2
            "Rejected Call" -> 5
            else -> 3
        }

        date = editDate.text.toString()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val cv = ContentValues()
        cv.put(CallLog.Calls.NUMBER, editNumber.text.toString())
        cv.put(CallLog.Calls.DURATION, editDuration.text.toString().toInt())
        cv.put(CallLog.Calls.DATE, sdf.parse(date).time)
        cv.put(CallLog.Calls.TYPE, type)

        //Inserting the data with resolver function
        contentResolver.insert(CallLog.Calls.CONTENT_URI, cv)

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }


}
