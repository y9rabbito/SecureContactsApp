package com.rabbito.securecontacts.Fragments.call

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rabbito.securecontacts.Fragments.update.updateFragmentArgs
import com.rabbito.securecontacts.R
import com.rabbito.securecontacts.ViewModel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_call.view.*


class callFragment : Fragment() {

    private val args by navArgs<updateFragmentArgs>()
    private lateinit var mContactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_call, container, false)


        //viewModel provider
        mContactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]


        //setting the fields
        val name = args.currentContact.firstName + " " + args.currentContact.lastName
        view.contactName.text = name
        view.contactNumber.text = args.currentContact.number

        setHasOptionsMenu(true)

        view.callButton.setOnClickListener {
            makePhoneCall()
        }

        return view
    }

    private fun makePhoneCall() {
        val number = args.currentContact.number
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
            val dial = "tel:$number"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.call_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteContact -> deleteContact()
            R.id.editContact -> updateContact()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateContact() {
        val action =
            callFragmentDirections.actionCallFragmentToUpdateFragment(args.currentContact)
        view?.findNavController()?.navigate(action)
    }

    private fun deleteContact() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mContactViewModel.deleteUser(args.currentContact)
            Toast.makeText(
                requireContext(),
                "Successfully deleted",
                Toast.LENGTH_SHORT
            ).show()
            //we don't need the navigation call
            findNavController().navigate(R.id.action_callFragment_to_phoneBookFragment)
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete contact ${args.currentContact.firstName}?")
        builder.setMessage("Are you sure want to delete ${args.currentContact.firstName}?")
        builder.create().show()
    }
}
