package com.example.shoppinglistapp.room

import com.example.shoppinglistapp.model.ShopItem
import io.reactivex.Completable
import io.reactivex.Single

class ShopItemsRoomRepositoryImpl(private val dao: ShopItemDao) : ShopItemsRoomRepository {

    override fun insert(item: ShopItem): Completable {
        return dao.insert(item)
    }

    override fun insert(items: List<ShopItem>): Completable {
        return dao.insert(items)
    }

    override fun getAll(): Single<List<ShopItem>> {
        return dao.getItems()
    }

    override fun getBought(): Single<List<ShopItem>> {
        return dao.getBought()
    }

    override fun getNotBought(): Single<List<ShopItem>> {
        return dao.getNotBought()
    }

    override fun getById(id: Int): Single<ShopItem> {
        return dao.getItemById(id)
    }

    override fun updateItem(item: ShopItem): Completable {
        return dao.updateItem(item)
    }

    override fun updateItems(items: List<ShopItem>): Completable {
        return dao.updateItems(items)
    }

    override fun removeById(id: Int): Completable {
        return dao.deleteById(id)
    }

    override fun removeAll(): Completable {
        return dao.deleteAll()
    }

    override fun delete(item: ShopItem): Completable {
        return dao.delete(item)
    }

    override fun delete(items: List<ShopItem>): Completable {
        return dao.delete(items)
    }


}