package com.sokoldev.budgo.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sokoldev.budgo.common.data.models.NotificationItem
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.data.repo.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val appRepository = AppRepository()

    private val _notificationState =
        MutableStateFlow<ApiResponse<PagingData<NotificationItem>>>(ApiResponse.Loading)
    val notificationState: StateFlow<ApiResponse<PagingData<NotificationItem>>> = _notificationState


    fun getNotifications(token: String) {
        viewModelScope.launch {
            _notificationState.value = ApiResponse.Loading
            appRepository.getNotifications(token).flow
                .cachedIn(viewModelScope)
                .catch { exception ->
                    _notificationState.value =
                        ApiResponse.Error(exception.localizedMessage ?: "An error occurred")
                }
                .collect { pagingData ->
                    _notificationState.value = ApiResponse.Success(pagingData)
                }
        }
    }

}