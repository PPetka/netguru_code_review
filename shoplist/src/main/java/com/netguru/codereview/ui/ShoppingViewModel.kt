package com.netguru.codereview.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netguru.codereview.network.ShopListApiMock
import com.netguru.codereview.network.ShopListRepository
import com.netguru.codereview.network.model.ShopListItemResponse
import com.netguru.codereview.network.model.ShopListResponse
import com.netguru.codereview.ui.model.ShopList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {

    private val shopListRepository = ShopListRepository(ShopListApiMock())

    val shopLists = MutableLiveData<List<ShopList>>()
    private val events: Flow<String> = shopListRepository.updateEvents()

    init {
        viewModelScope.launch {
            val lists = shopListRepository.getShopLists()
            val data = mutableListOf<Pair<ShopListResponse, List<ShopListItemResponse>>>()
            for (list in lists) {
                val items = shopListRepository.getShopListItems(list.list_id)
                data.add(list to items)
            }
            shopLists.postValue(data.map { mapShopList(it.first, it.second) })
        }
    }

    fun getUpdateEvents(): Flow<String> = events

    private fun mapShopList(list: ShopListResponse, items: List<ShopListItemResponse>) =
        ShopList(
            list.list_id,
            list.userId,
            list.listName,
            null, //todo find a place from which we can fetch it
            items
        )
}
