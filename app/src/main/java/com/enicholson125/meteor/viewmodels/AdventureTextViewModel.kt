package com.enicholson125.meteor.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.enicholson125.meteor.data.TextSnippetRepository
import com.enicholson125.meteor.data.TextSnippet
import com.enicholson125.meteor.data.SnippetType

/**
 * The ViewModel used in [SnippetDetailFragment].
 */
class AdventureTextViewModel(
    private val textSnippetRepository: TextSnippetRepository,
    private var snippetID: String = "T1",
    //val adventureText: MutableLiveData<String> = MutableLiveData<String>()
) : ViewModel() {
    var adventureText = ""
    val adventureTextLiveData: MutableLiveData<String> = MutableLiveData<String>("Default text")

    val snippetIDLiveData = MutableLiveData<String>(snippetID)

    val textSnippetLiveData: LiveData<TextSnippet> = Transformations.switchMap(
        snippetIDLiveData, ::updateTextSnippet
    )

    // This is a bit of a hack, as I don't actually want a liveData
    // output from this transformation, I just want to trigger the
    // mutable data updates
    val notifierLiveData: LiveData<TextSnippet> = Transformations.switchMap(
        textSnippetLiveData, ::updateAdventureText
    )

    private fun updateTextSnippet(snippetID: String): LiveData<TextSnippet> {
        return textSnippetRepository.getTextSnippetByID(snippetID)
    }

    fun updateAdventureText(snippet: TextSnippet): LiveData<TextSnippet> {
        adventureText = adventureText + snippet.description
        if (snippet.nextSnippets.size == 1) {
            adventureTextLiveData.setValue(adventureText)
            snippetIDLiveData.setValue(snippet.nextSnippets.get(0))
            return textSnippetRepository.getTextSnippetByID("T1")
        } else {
            adventureText = adventureText + "\nChoices here"
            adventureTextLiveData.setValue(adventureText)
            return textSnippetRepository.getTextSnippetByID("T1")
        }
    }

    fun updateSnippetID(id: String) {
        Log.e("Blah", "!!!!!!EN!!!!!!!!!")
        Log.e("Blah", "updateID")
        snippetIDLiveData.setValue(id)
    }

    fun updateDescription(desc: String) {
        adventureTextLiveData.setValue(desc)
    }

    // private fun buildAdventureText() {
    //     // This logic should probably be in the repository, where
    //     // it won't be stuck in live datas
    //     var textSnippet = textSnippetRepository.getTextSnippetByID(snippetID).getValue()
    //     if (textSnippet == null) {
    //         adventureText.setValue("Sorry, an error has occurred while loading the adventure.")
    //         return
    //     }
    //     var adventureDescription = textSnippet.description
    //     var nextSnippetIDs = textSnippet.nextSnippets
    //     while (nextSnippetIDs.size == 1) {
    //         snippetID = nextSnippetIDs.get(0)
    //         textSnippet = textSnippetRepository.getTextSnippetByID(snippetID).getValue()
    //         if (textSnippet == null) {
    //             adventureDescription = adventureDescription + "\nSorry, an error has occurred."
    //             adventureText.setValue(adventureDescription)
    //             return
    //         }
    //         adventureDescription = adventureDescription + textSnippet.description
    //         nextSnippetIDs = textSnippet.nextSnippets
    //     }
    //     adventureDescription = adventureDescription + textSnippet!!.description
    //     adventureText.setValue(adventureDescription)
    // }
}

