package com.rabbito.securecontacts.Repository

import androidx.lifecycle.LiveData
import com.rabbito.securecontacts.Data.ContactDAO
import com.rabbito.securecontacts.Model.Contact


class ContactRepository(private val contactDao: ContactDAO) {
    val readAllData: LiveData<List<Contact>> = contactDao.readAllData()
    val readAllDataSortedOrder: LiveData<List<Contact>> = contactDao.readAllDataSortedOrder()

    suspend fun addUser(contact: Contact) {
        contactDao.addUser(contact)
    }

    suspend fun updateUser(contact: Contact) {
        contactDao.updateUser(contact)
    }

    suspend fun deleteUser(contact: Contact) {
        contactDao.deleteUser(contact)
    }

    suspend fun deleteAllUsers() {
        contactDao.deleteAllUsers()
    }

    fun searchContact(searchQuery: String): LiveData<List<Contact>> {
        return contactDao.searchContact(searchQuery)
    }

    fun findName(): List<Contact> {
        return contactDao.searchName()
    }


}