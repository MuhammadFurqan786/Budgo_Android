package com.sokoldev.budgo.common.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sokoldev.budgo.common.data.models.NotificationItem
import com.sokoldev.budgo.common.data.remote.network.ApiServices

class NotificationPagingSource(
    private val apiService: ApiServices,
    private val token: String
) : PagingSource<Int, NotificationItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationItem> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.getNotifications(token, currentPage)
            val data = response.body()!!.notificationData.data

            LoadResult.Page(
                data = data,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
