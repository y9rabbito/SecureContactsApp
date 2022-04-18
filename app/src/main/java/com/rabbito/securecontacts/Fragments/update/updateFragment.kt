package com.rabbito.securecontacts.Fragments.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rabbito.securecontacts.Model.Contact
import com.rabbito.securecontacts.R
import com.rabbito.securecontacts.ViewModel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*

class updateFragment : Fragment() {

    private val args by navArgs<updateFragmentArgs>()
    private lateinit var mContactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)


        //viewModel provider
        mContactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)


        //setting the fields
        view.updateContactEditFirstName.setText(args.currentContact.firstName)
        view.updateContactEditLastName.setText(args.currentContact.lastName)
        view.updateContactEditPhoneNumber.setText(args.currentContact.number)


        //setting the menu
        setHasOptionsMenu(true)


        //handle the update button click event
        view.updateButton.setOnClickListener {
            updateItem()
        }

        return view
    }

    private fun updateItem() {

        //Getting the fields
        var firstName = updateContactEditFirstName.text.toString().replace("\\s+".toRegex(), " ")
        firstName = firstName.trim().capitalize()
        var lastName = updateContactEditLastName.text.toString().replace("\\s+".toRegex(), " ")
        lastName = lastName.trim().capitalize()
        var number = updateContactEditPhoneNumber.text.toString().replace("\\s+".toRegex(), "")
        number = number.trim()


        if (inputNullCheck(firstName, number)) {
            Toast.makeText(requireContext(), "Please fill out the Fields", Toast.LENGTH_SHORT)
                .show()

        } else {
            //Create user object
            val updateObject = Contact(args.currentContact.id, firstName, lastName, number)

            //Update Current User
            mContactViewModel.updateUser(updateObject)

            Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
            //Navigate Back to the list fragment
            findNavController().navigate(R.id.action_updateFragment_to_phoneBookFragment)

        }

    }

    private fun inputNullCheck(firstName: String, number: String): Boolean {
        if (firstName.isEmpty() && number.isEmpty()) {
            updateContactEditFirstNameInput.error = "You need to enter a name"
            updateContactEditPhoneNumberInput.error = "You need to enter a number"
            return true
        }
        if (firstName.isEmpty() && number.isNotEmpty()) {
            updateContactEditFirstNameInput.error = "You need to enter a name"
            updateContactEditPhoneNumberInput.error = null
            return true
        }
        if (firstName.isNotEmpty() && number.isEmpty()) {
            updateContactEditPhoneNumberInput.error = "You need to enter a number"
            updateContactEditFirstNameInput.error = null
            return true
        }
        return false
    }

}