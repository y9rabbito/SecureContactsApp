package com.rabbito.securecontacts.Fragments.add

import android.app.Activity
import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rabbito.securecontacts.Model.Contact
import com.rabbito.securecontacts.R
import com.rabbito.securecontacts.ViewModel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*


class addFragment : Fragment() {

    private lateinit var mContactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        //getting the ViewModel
        mContactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        try {
            view.addContactEditPhoneNumber.text =
                Editable.Factory.getInstance().newEditable(requireArguments().getString("number"))
        } catch (e: Exception) {

        }

        view.phoneBookAddButton.setOnClickListener {
            hideSoftKeyboard(requireActivity())
            insertDataToDatabase()
        }

        view.phoneBookCancelButton.setOnClickListener {
            hideSoftKeyboard(requireActivity())
            view.addContactEditFirstName.text = null
            view.addContactEditLastName.text = null
            view.addContactEditPhoneNumber.text = null
        }

        view.addContactEditFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(s)
            }
        })

        view.addContactEditPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditNumber(s)
            }
        })

        return view
    }

    private fun validateEditNumber(s: Editable) {
        if (!TextUtils.isEmpty(s)) {
            addContactEditPhoneNumberInput.error = null
        } else {
            addContactEditPhoneNumberInput.error = "Please enter a number"
        }
    }

    private fun validateEditText(s: Editable) {
        if (!TextUtils.isEmpty(s)) {
            addContactEditFirstNameInput.error = null
        } else {
            addContactEditFirstNameInput.error = "Please enter a name"
        }
    }

    private fun insertDataToDatabase() {
        var firstName = "empty"
        var lastName = "empty"
        var number = "empty"

        firstName = addContactEditFirstName.text.toString()
        lastName = addContactEditLastName.text.toString()
        number = addContactEditPhoneNumber.text.toString()

        //Handling the whitespaces
        firstName = firstName.replace("\\s+".toRegex(), " ")
        firstName = firstName.trim()
        firstName = firstName.capitalize()
        lastName = lastName.replace("\\s+".toRegex(), " ")
        lastName = lastName.trim()
        lastName = lastName.capitalize()
        number = number.replace("\\s+".toRegex(), "")
        number = number.trim()

        if (inputNullCheck(firstName, number)) {
            Toast.makeText(requireContext(), "Add the Required Fields", Toast.LENGTH_SHORT).show()
        } else {
            addContactEditFirstName.error = null
            addContactEditPhoneNumber.error = null
            val contact = Contact(0, firstName, lastName, number)
            mContactViewModel.addUser(contact)
            Toast.makeText(requireContext(), "$firstName Successfully Added", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_addFragment_to_phoneBookFragment)
        }
    }

    private fun inputNullCheck(firstName: String, number: String): Boolean {
        if (firstName.isEmpty() && number.isEmpty()) {
            addContactEditFirstNameInput.error = "Please enter a name"
            addContactEditPhoneNumberInput.error = "Please enter a number"
            return true
        }
        if (firstName.isEmpty() && number.isNotEmpty()) {
            addContactEditFirstNameInput.error = "Please enter a name"
            addContactEditPhoneNumberInput.error = null
            return true
        }
        if (firstName.isNotEmpty() && number.isEmpty()) {
            addContactEditPhoneNumberInput.error = "Please enter a number"
            addContactEditFirstNameInput.error = null
            return true
        }
        return false
    }

    private fun showInputMethod() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun hideSoftKeyboard(context: Activity) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(context.window.decorView.applicationWindowToken, 0)
        context.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
}