package com.rabbito.securecontacts.Fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dialer.*
import kotlinx.android.synthetic.main.fragment_dialer.view.*


class dialerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(com.rabbito.securecontacts.R.layout.fragment_dialer, container, false)

        view.buttonCall.setOnClickListener {
            makePhoneCall()
        }

        view.buttonCancel.setOnClickListener {
            view.editNumber.text = null
        }
        view.editNumber.requestFocus()
        showInputMethod()
        view.editNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(s)
            }
        })

        return view
    }

    private fun showInputMethod() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }


    private fun makePhoneCall() {

        val number: String = editNumber.text.toString()
        //checking the dialpad if its empty or not
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            //checking for permission
            if (view?.let {
                    ContextCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.CALL_PHONE
                    )
                } != PackageManager.PERMISSION_GRANTED  //if permission isn't granted
            ) {
                // Granting the permission
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    111
                )
            } else {
                //Making an call
                val fullnumber = countryChooser.fullNumber + number
                val dial = "tel:$fullnumber"
                editNumberLayout.error = null
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {    // Hint for enter the number
            editNumberLayout.error = "Please enter a number"
        }
    }

    private fun hideSoftKeyboard(context: Activity) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(context.window.decorView.applicationWindowToken, 0)
        context.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        hideSoftKeyboard(requireActivity())
        view?.editNumber?.clearFocus()
        super.onStop()
    }

    override fun onPause() {
        view?.editNumber?.clearFocus()
        hideSoftKeyboard(requireActivity())
        super.onPause()
    }

    override fun onResume() {
        view?.editNumber?.requestFocus()
        showInputMethod()
        super.onResume()
    }

    override fun onStart() {
        showInputMethod()
        view?.requestFocus()
        super.onStart()
    }

    private fun validateEditText(s: Editable) {
        if (!TextUtils.isEmpty(s)) {
            editNumberLayout.error = null
        } else {
            editNumberLayout.error = "Please enter a number"
        }
    }
}