package com.nxg.im.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.IMClient
import com.nxg.im.core.data.Friend
import com.nxg.im.core.data.Result
import com.nxg.im.core.module.user.User
import com.nxg.im.core.module.user.UserService
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ContactUiState(val contactList: List<Contact>, val contactDetail: ContactDetail? = null)

class ContactViewModel(val contactRepository: ContactRepository) : ViewModel(),SimpleLogger {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ContactUiState(emptyList(), null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {


            }
        }
    }

    fun getMyFriends() {
        viewModelScope.launch {
            IMClient.userService.myFriends().let {
                when (it) {
                    is Result.Error -> {
                        logger.debug { it.exception }
                    }
                    is Result.Success -> {
                        val contactList = mutableListOf<Contact>()
                        contactList.addAll(
                            listOf(
                                Contact.ContactFriendList(it.data)
                            )
                        )
                        _uiState.emit(_uiState.value.copy(contactList = contactList))
                    }
                }
            }

        }
    }

    suspend fun loadContactDetail(friend: Friend) {
        _uiState.emit(_uiState.value.copy(contactDetail = ContactDetail(friend)))

    }

}

