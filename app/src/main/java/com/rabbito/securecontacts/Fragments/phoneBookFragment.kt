package com.rabbito.securecontacts.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rabbito.securecontacts.Adapter.PhoneBookListAdapter
import com.rabbito.securecontacts.GenerateFakeCallLog
import com.rabbito.securecontacts.Pdf_Viewer
import com.rabbito.securecontacts.R
import com.rabbito.securecontacts.ViewModel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_phone_book.*
import kotlinx.android.synthetic.main.fragment_phone_book.view.*


class phoneBookFragment : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private lateinit var mContactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_book, container, false)

        //Setting up the RecyclerView
        val adapter = PhoneBookListAdapter(this.requireContext())
        val recyclerView = view.recyclerViewPhoneBook
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //menu
        setHasOptionsMenu(true)


        //contactViewModel
        mContactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        mContactViewModel.sortedData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)
        })


        //Handle the click of add contacts button
        view.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_phoneBookFragment_to_addFragment)
        }

        return view
    }


    //Menu item selection handling
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_order_phonebook -> sortByOrder()
            R.id.deleteAll_contacts -> deleteAllUsers()
            R.id.generate_fake_call_log -> generateFakeCallLog()
            R.id.info -> showInfo()
            R.id.share -> shareAPP()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun shareAPP() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://drive.google.com/drive/folders/1rN5PnWjIJHQj7xAgxLzBOBOdn8CWdS9q?usp=sharing"
        )
        shareIntent.type = "text/plain"
        startActivity(shareIntent)
    }

    private fun showInfo() {
        if (isNetworkConnected()) {
            val intent = Intent(context, Pdf_Viewer::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "Check your internet Connection!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm =
            context?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }


    //Function for generating fake call log
    private fun generateFakeCallLog() {
        try {
            startActivity(Intent(this.context, GenerateFakeCallLog::class.java))
        } catch (e: Exception) {
            Toast.makeText(context, "Something Wrong!", Toast.LENGTH_SHORT).show()
        }
    }


    //Function for Sort by Alphabetical Order
    private fun sortByOrder() {
        var adapter = PhoneBookListAdapter(this.requireContext())
        var recyclerView = view?.recyclerViewPhoneBook
        if (recyclerView != null) {
            recyclerView.adapter = adapter
        }
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        mContactViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)
        })
    }


    //Function for Delete All Users
    private fun deleteAllUsers() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mContactViewModel.deleteAllUsers()
            Toast.makeText(
                requireContext(),
                "Successfully Deleted All Contacts",
                Toast.LENGTH_SHORT
            ).show()
            //we don't need the navigation call
            //findNavController().navigate(R.id.action_updateFragment_to_listFragment2)
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete all Contacts?")
        builder.setMessage("Are you sure want to delete all contacts?")
        builder.create().show()

    }

    private lateinit var searchView: SearchView

    //inflating the menu layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.phonebook_options_menu, menu)

        val search = menu.findItem(R.id.searchBarPhoneBook)
        searchView = search?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchContact(query)
        }
        return true
    }

    private fun searchContact(query: String) {
        var searchQuery = "%${query}%"
        var adapter = PhoneBookListAdapter(this.requireContext())
        recyclerViewPhoneBook.adapter = adapter
        recyclerViewPhoneBook.layoutManager = LinearLayoutManager(this.requireContext())
        mContactViewModel.searchContact(searchQuery).observe(this) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }


}