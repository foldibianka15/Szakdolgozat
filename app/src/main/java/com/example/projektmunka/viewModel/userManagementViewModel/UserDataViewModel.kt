package com.example.projektmunka.viewModel.userManagementViewModel

import androidx.lifecycle.viewModelScope
import com.example.firstapp.repository.UserDataRepository
import com.example.projektmunka.repository.AuthRepository
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository, private val authRepository: AuthRepository
) : BaseViewModel() {

    val currentUserData = userDataRepository.currentUserData

    init {
        getUserData()
    }

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.currentUser.collect {
                if (it != null) {
                    userDataRepository.getUserProfileData(it.uid)
                }
            }
        }
    }

    /*private fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserData
                .filterNotNull()
                .collect { user ->
                    userDataRepository.getUserProfileData(user.id)
                }
        }
    }*/

}