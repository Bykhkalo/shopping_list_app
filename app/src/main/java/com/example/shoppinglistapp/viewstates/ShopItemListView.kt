package com.example.shoppinglistapp.viewstates

import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.utils.DataType
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = SkipStrategy::class)
interface ShopItemListView : MvpView {
    fun showData(items: List<ShopItem>)
    fun showError(message: Int)
    fun showError(message: String)

    fun removeRecyclerViewItem(position: Int)
    fun notifyRecyclerViewItemUpdated(newItem: ShopItem, position: Int, dataType: DataType)
    fun notifyRecyclerViewItemsUpdated(newItems: List<ShopItem>, dataType: DataType)
    fun removeRecyclerViewItems(items: List<ShopItem>)
}