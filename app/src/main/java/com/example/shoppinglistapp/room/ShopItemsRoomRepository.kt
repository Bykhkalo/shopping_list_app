package com.example.shoppinglistapp.room

import com.example.shoppinglistapp.model.ShopItem
import io.reactivex.Completable
import io.reactivex.Single

interface ShopItemsRoomRepository {

    fun insert(item: ShopItem) : Completable
    fun insert(items: List<ShopItem>) : Completable

    fun getAll(): Single<List<ShopItem>>
    fun getBought(): Single<List<ShopItem>>
    fun getNotBought(): Single<List<ShopItem>>

    fun getById(id: Int): Single<ShopItem>

    fun updateItem(item: ShopItem) : Completable
    fun updateItems(items: List<ShopItem>) : Completable


    fun removeById(id:Int): Completable
    fun removeAll(): Completable
    fun delete(item: ShopItem) : Completable
    fun delete(items: List<ShopItem>) : Completable
}