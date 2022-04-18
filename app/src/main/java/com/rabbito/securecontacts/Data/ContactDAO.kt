package com.rabbito.securecontacts.Data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rabbito.securecontacts.Model.Contact

@Dao
interface ContactDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(contact: Contact)

    @Delete
    suspend fun deleteUser(contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM contact_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table ORDER BY firstName ASC")
    fun readAllDataSortedOrder(): LiveData<List<Contact>>

    @Update
    suspend fun updateUser(contact: Contact)

    @Query("SELECT * FROM contact_table WHERE firstName LIKE :searchQuery OR lastName LIKE :searchQuery OR number LIKE :searchQuery ORDER BY firstName ASC")
    fun searchContact(searchQuery: String): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table")
    fun searchName(): List<Contact>

}