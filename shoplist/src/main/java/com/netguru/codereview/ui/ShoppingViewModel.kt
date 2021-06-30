package com.netguru.codereview.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netguru.codereview.network.ShopListApiMock
import com.netguru.codereview.network.ShopListRepository
import com.netguru.codereview.network.model.ShopListItemResponse
import com.netguru.codereview.network.model.ShopListResponse
import com.netguru.codereview.ui.model.ShopList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {

    private val shopListRepository = ShopListRepository(ShopListApiMock())

    private val events: Flow<String> = shopListRepository.updateEvents()
    private val shoppingState = MutableStateFlow<ShoppingState>(ShoppingState.Default)

    fun fetchShoppings() {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingState.value = ShoppingState.Loading
            try {
                val lists = shopListRepository.getShopLists()
                val data = mutableListOf<Pair<ShopListResponse, List<ShopListItemResponse>>>()
                for (list in lists) {
                    val items = shopListRepository.getShopListItems(list.list_id)
                    data.add(list to items)
                }
                val shoppingLists = data.map { mapShopList(it.first, it.second) }
                shoppingState.value = ShoppingState.Success(shoppingLists)
            } catch (exception: Exception) {
                shoppingState.value = ShoppingState.Error(exception)
            }
        }
    }

    fun getUpdateEvents(): Flow<String> = events
    fun getShoppinngListData(): Flow<ShoppingState> = shoppingState

    private fun mapShopList(list: ShopListResponse, items: List<ShopListItemResponse>) =
        ShopList(
            list.list_id,
            list.userId,
            list.listName,
            null, //todo find a place from which we can fetch it
            items
        )

    sealed class ShoppingState {
        object Default : ShoppingState()
        object Loading : ShoppingState()
        class Error(val exception: Exception) : ShoppingState()
        class Success(val lists: List<ShopList>) : ShoppingState()
    }
}
