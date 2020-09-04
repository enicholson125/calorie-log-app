package com.enicholson125.meteor.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * The Data Access Object for the [TextSnippet] class.
 */
@Dao
interface TextSnippetDAO {
    @Query("SELECT * FROM text_snippets WHERE id = :id")
    fun getTextSnippetByID(id: String): LiveData<TextSnippet>
}
