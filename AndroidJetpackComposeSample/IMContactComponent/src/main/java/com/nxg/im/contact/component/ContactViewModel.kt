package com.nxg.im.contact.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.Utils
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.db.entity.Friend
import com.nxg.im.core.data.db.KtChatDatabase
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactUiState(val contactList: List<Contact>, val contactDetail: ContactDetail? = null)

class ContactViewModel(val contactRepository: ContactRepository) : ViewModel(), SimpleLogger {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ContactUiState(emptyList(), null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            KtChatDatabase.getInstance(Utils.getApp()).friendDao().flowFriendList().collect {
                logger.debug { "flowFriendList: $it" }
                val contactList = mutableListOf<Contact>(Contact.ContactFriendList(it))
                _uiState.emit(_uiState.value.copy(contactList = contactList))
            }
        }
    }

    fun getMyFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            IMClient.userService.getMyFriends()
        }
    }

    suspend fun loadContactDetail(friend: Friend) {
        _uiState.emit(_uiState.value.copy(contactDetail = ContactDetail(friend)))

    }

}

