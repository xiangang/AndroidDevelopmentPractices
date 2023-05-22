package com.nxg.im.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.im.core.module.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ContactUiState(val contactList: List<Contact>, val contactDetail: ContactDetail? = null)

class ContactViewModel(val contactRepository: ContactRepository) : ViewModel() {


    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(ContactUiState(emptyList(), null))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val contactList = mutableListOf<Contact>()
                val friendList = mutableListOf(
                    Friend(
                        User(
                            id = 1,
                            uuid = 51691563050860544,
                            username = "nxg",
                            password = "",
                            nickname = "失落轨迹",
                            email = "342005702@qq.com",
                            phone = "15607837955",
                            avatar = "https://randomuser.me/api/portraits/men/1.jpg",
                            address = "",
                            province = "",
                            city = "",
                            country = "",
                            status = 0,
                            createTime = "",
                            updateTime = ""
                        )
                    ),
                    Friend(
                        User(
                            id = 2,
                            uuid = 51691563610275840,
                            username = "xiangang",
                            password = "",
                            nickname = "Annon",
                            email = "342005702@qq.com",
                            phone = "15607837955",
                            avatar = "https://randomuser.me/api/portraits/men/2.jpg",
                            address = "",
                            province = "",
                            city = "",
                            country = "",
                            status = 0,
                            createTime = "",
                            updateTime = ""
                        )
                    )
                )
                contactList.addAll(
                    listOf(
                        Contact.ContactFriendList(friendList)
                    )
                )
                _uiState.emit(_uiState.value.copy(contactList = contactList))
            }
        }
    }

    suspend fun loadContactDetail(user: User) {
        _uiState.emit(_uiState.value.copy(contactDetail = ContactDetail(user)))

    }

}

