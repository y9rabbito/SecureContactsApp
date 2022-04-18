package com.rabbito.securecontacts.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rabbito.securecontacts.Database.ContactDatabase
import com.rabbito.securecontacts.Model.Contact
import com.rabbito.securecontacts.Repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Contact>>
    val sortedData: LiveData<List<Contact>>
    private val repository: ContactRepository

    init {
        val userDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(userDao)
        readAllData = repository.readAllData
        sortedData = repository.readAllDataSortedOrder
    }


    fun addUser(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(contact)
        }
    }

    fun updateUser(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(contact)
        }
    }

    fun deleteUser(contact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(contact)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUsers()
        }
    }

    fun searchContact(searchQuery: String): LiveData<List<Contact>> {
        return repository.searchContact(searchQuery)
    }
}