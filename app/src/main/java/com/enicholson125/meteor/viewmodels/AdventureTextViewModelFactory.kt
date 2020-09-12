package com.enicholson125.meteor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enicholson125.meteor.data.TextSnippetRepository
import com.enicholson125.meteor.data.TextHistoryRepository

/**
 * Factory for creating a [AventureDetailViewModelt] with a constructor that
 * takes a [TextSnippetRepository] and a [TextHistoryRepository]
 */
class AdventureTextViewModelFactory(
    private val textSnippetRepository: TextSnippetRepository,
    private val textHistoryRepository: TextHistoryRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdventureTextViewModel(textSnippetRepository, textHistoryRepository) as T
    }
}
