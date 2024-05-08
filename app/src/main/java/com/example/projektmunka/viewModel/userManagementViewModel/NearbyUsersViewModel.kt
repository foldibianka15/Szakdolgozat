package com.example.projektmunka.viewModel.userManagementViewModel

import androidx.lifecycle.viewModelScope
import com.example.projektmunka.data.User
import com.example.projektmunka.repository.NearbyUsersRepository
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyUsersViewModel @Inject constructor(private val nearbyUsersRepository: NearbyUsersRepository) : BaseViewModel() {

    private val _nearbyUsers = MutableSharedFlow<MutableList<User>>()
    val nearbyUsers = _nearbyUsers.asSharedFlow()

    fun getNearbyUsers(friendZone: Double) {

        viewModelScope.launch(coroutineContext) {
            val result = nearbyUsersRepository.getNearbyUsers(friendZone)
            _nearbyUsers.emit(result)
        }
    }
}