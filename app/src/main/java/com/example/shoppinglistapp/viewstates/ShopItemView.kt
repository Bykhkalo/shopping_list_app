package com.example.shoppinglistapp.viewstates

import com.example.shoppinglistapp.model.ShopItem
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = SkipStrategy::class)
interface ShopItemView: MvpView {

    fun showItem(item: ShopItem)
    fun showError(message: Int)
    fun showError(message: String)
    fun finishWork()


}