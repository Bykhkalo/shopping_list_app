package com.example.shoppinglistapp.presenters

import android.annotation.SuppressLint
import com.example.shoppinglistapp.App
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.room.ShopItemsRoomRepository
import com.example.shoppinglistapp.utils.DataType
import com.example.shoppinglistapp.viewstates.ShopItemListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ShopItemListPresenter : MvpPresenter<ShopItemListView>() {

    init {
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var repository: ShopItemsRoomRepository



    fun loadData(dataType: DataType = DataType.ALL) {
        when (dataType) {
            DataType.ALL -> loadAllData()
            DataType.BOUGHT -> loadBought()
            DataType.NOT_BOUGHT -> loadNotBought()
        }
    }

    @SuppressLint("CheckResult")
    private fun loadAllData() {
        repository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                viewState.showData(items)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }


    @SuppressLint("CheckResult")
    private fun loadBought() {
        repository.getBought()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                viewState.showData(items)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    private fun loadNotBought() {
        repository.getNotBought()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                viewState.showData(items)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun removeItems(items: List<ShopItem>) {
        repository.delete(items)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.removeRecyclerViewItems(items)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun removeItem(item: ShopItem, position: Int) {
        repository.delete(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.removeRecyclerViewItem(position)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun updateItem(item: ShopItem, position: Int, dataType: DataType) {
        repository.updateItem(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.notifyRecyclerViewItemUpdated(item, position, dataType)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun updateItems(items: List<ShopItem>, dataType: DataType) {
        repository.updateItems(items)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.notifyRecyclerViewItemsUpdated(items, dataType)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

}