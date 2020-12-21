package com.example.shoppinglistapp.room

import androidx.room.*
import com.example.shoppinglistapp.model.ShopItem
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface ShopItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<ShopItem>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ShopItem): Completable



    @Query("SELECT * FROM shop_items ORDER BY date DESC")
    fun getItems() : Single<List<ShopItem>>

    @Query("SELECT * FROM shop_items WHERE isBought!=0 ORDER BY date DESC")
    fun getBought(): Single<List<ShopItem>>

    @Query("SELECT * FROM shop_items WHERE isBought=0 ORDER BY date DESC")
    fun getNotBought(): Single<List<ShopItem>>

    @Query("SELECT * FROM shop_items WHERE id = :id")
    fun getItemById(id: Int): Single<ShopItem>




    @Update
    fun updateItem(item: ShopItem): Completable

    @Update
    fun updateItems(items: List<ShopItem>): Completable



    @Query("DELETE FROM shop_items WHERE id = :id")
    fun deleteById(id: Int): Completable

    @Query("DELETE FROM shop_items")
    fun deleteAll(): Completable

    @Delete
    fun delete(item: ShopItem) : Completable

    @Delete
    fun delete(items: List<ShopItem>): Completable

}