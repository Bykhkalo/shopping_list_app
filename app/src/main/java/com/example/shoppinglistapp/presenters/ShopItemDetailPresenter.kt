package com.example.shoppinglistapp.presenters

import android.annotation.SuppressLint
import com.example.shoppinglistapp.App
import com.example.shoppinglistapp.R
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.room.ShopItemsRoomRepository
import com.example.shoppinglistapp.viewstates.ShopItemView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ShopItemDetailPresenter : MvpPresenter<ShopItemView>() {

    init {
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var repository: ShopItemsRoomRepository


    @SuppressLint("CheckResult")
    fun loadItem(id: Int) {
        repository.getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ item ->
                viewState.showItem(item)
            }, { error ->
                viewState.showError(error.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun updateItem(item: ShopItem) {
        if (validateItem(item)) {
            repository.updateItem(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.finishWork()
                }, { error ->
                    viewState.showError(error.message.orEmpty())
                })
        } else {
            viewState.showError(R.string.item_data_error)
        }
    }

    @SuppressLint("CheckResult")
    fun createItem(item: ShopItem) {

        if (validateItem(item)) {
            repository.insert(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.finishWork()
                }, { error ->
                    viewState.showError(error.message.orEmpty())
                })
        } else {
            viewState.showError(R.string.item_data_error)
        }

    }

    private fun validateItem(item: ShopItem): Boolean {
        return item.content.isNotEmpty()
    }


}