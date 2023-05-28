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
                val friendList = mutableListOf<Friend>()
                friendList.add(
                    Friend(
                        User(
                            id = 1,
                            uuid = 51691563050860544,
                            username = "nxg",
                            password = "",
                            nickname = "福田大飞机",
                            email = "342005702@qq.com",
                            phone = "15607837955",
                            avatar = "https://img1.baidu.com/it/u=2702325053,1055770562&fm=253&fmt=auto&app=138&f=JPEG",
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
                for (i in 2..100) {
                    friendList.add(
                        Friend(
                            User(
                                id = i+1,
                                uuid = 51691563050860544,
                                username = "$i",
                                password = "",
                                nickname = "机器人${i}号",
                                email = "342005702@qq.com",
                                phone = "15607837955",
                                avatar = "https://randomuser.me/api/portraits/men/$i.jpg",
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
                }
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

