package com.rabbito.securecontacts.Fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.CallLog
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rabbito.securecontacts.Adapter.CallLogListAdapter
import com.rabbito.securecontacts.Database.ContactDatabase
import com.rabbito.securecontacts.Model.CallLogContact
import com.rabbito.securecontacts.Model.Contact
import com.rabbito.securecontacts.R
import com.rabbito.securecontacts.Repository.ContactRepository
import kotlinx.android.synthetic.main.fragment_call_log.*
import kotlinx.android.synthetic.main.fragment_call_log.view.*
import java.text.SimpleDateFormat

class callLogFragment : Fragment() {
    private var list: ArrayList<CallLogContact> = arrayListOf()
    private var tempList = ArrayList<CallLogContact>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_call_log, container, false)
        if (view?.let {
                ContextCompat.checkSelfPermission(
                    it.context,
                    Manifest.permission.READ_CALL_LOG,
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            //if this is n't granted then request for the same
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.READ_CALL_LOG),
                299
            )
        }

        // else(if the request is granted then readLog()
        else {
            readLog()
        }

        val layoutManager = LinearLayoutManager(activity)
        view.recyclerViewCallLog.layoutManager = layoutManager
        view.recyclerViewCallLog.adapter = CallLogListAdapter(tempList, this.requireContext())
        return view
    }

    private fun readLog() {
        val repository: ContactRepository
        val userDao =
            activity?.let { ContactDatabase.getDatabase(it.applicationContext).contactDao() }
        repository = ContactRepository(userDao!!)


        var contact_array_list: List<Contact>? = null
        AsyncTask.execute {
            contact_array_list = repository.findName()
        }

        val cols = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE,
        )

        val rs = activity?.contentResolver?.query(
            CallLog.Calls.CONTENT_URI,
            cols,
            null,
            null,
            "${CallLog.Calls.LAST_MODIFIED} DESC"
        )
        while (rs?.moveToNext()!!) {

            val number = rs.getString(1)
            val duration = rs.getInt(3)
            var callDuration: String
            var typeCall = rs.getString(2).toInt()
            if (typeCall == 2 && duration == 0) {
                typeCall = 10
                callDuration = "Missed Outgoing"
            } else {
                when (typeCall) {
                    3 -> callDuration = "Missed Call"
                    5 -> callDuration = "Rejected"
                    else -> {
                        val mnts = duration / 60
                        val sec = duration % 60
                        callDuration = if (mnts == 0 || sec == 0) {
                            if (mnts == 0)
                                "$sec sec"
                            else
                                "$mnts min"
                        } else {
                            "$mnts m $sec s"
                        }
                    }
                }
            }
            var name = number.toString()
            if (contact_array_list?.isNotEmpty() == true) {
                for (items in contact_array_list!!) {
                    if (PhoneNumberUtils.compare(number, items.number)) {
                        name = items.firstName + " " + items.lastName
                        break
                    }
                }
            }
            val date = rs.getString(4).toLong()
            //getting formatted date and time
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val dateTime = simpleDateFormat.format(date).toString()

            list.add(CallLogContact(name, number, typeCall, callDuration, dateTime))
        }
        tempList.addAll(list)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 299 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        //Permission is with us
        {
            readLog()
        } else {   //Permission isn't with us
            Toast.makeText(view?.context, "Permission Deny", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        return when (item.itemId) {
            101 -> {
                if (tempList[item.groupId].number.toString() == tempList[item.groupId].name.toString()) {
                    findNavController().navigate(
                        R.id.action_callLogFragment_to_addFragment,
                        Bundle().apply {
                            putString(
                                "number",
                                PhoneNumberUtils.formatNumber(tempList[item.groupId].number)
                                    .toString()
                            )
                        })
                } else {
                    Toast.makeText(context, "Number is Already Saved", Toast.LENGTH_SHORT).show()
                }
                true
            }
            102 -> {
                if (view?.let {
                        ActivityCompat.checkSelfPermission(
                            it.context,
                            Manifest.permission.WRITE_CALL_LOG
                        )
                    } != PackageManager.PERMISSION_GRANTED
                ) { //if permission isn't available then request for same
                    ActivityCompat.requestPermissions(
                        view?.context as Activity,
                        arrayOf(Manifest.permission.WRITE_CALL_LOG),
                        112
                    )
                    //tempList.removeAt(item.groupId)
                    //view?.recyclerViewCallLog?.adapter = CallLogListAdapter(tempList, this.requireContext())
                } else {
                    //Working fine but resetting everytime
                    var number: String =
                        PhoneNumberUtils.formatNumber(tempList[item.groupId].number)
                    number = "NUMBER='$number'"
                    context?.contentResolver?.delete(CallLog.Calls.CONTENT_URI, number, null)
                    tempList.removeAt(item.groupId)
                    readLog()
                    view?.recyclerViewCallLog?.adapter =
                        CallLogListAdapter(tempList, this.requireContext())
                    Snackbar.make(rootElement, "Deleted!", Snackbar.LENGTH_SHORT).show()
                }
                true

            }
            else -> false
        }
    }

}

