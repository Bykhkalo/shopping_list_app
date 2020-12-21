package com.example.shoppinglistapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppinglistapp.model.ShopItem

@Database(
    entities = [ShopItem::class],
    version = 1,
    exportSchema = false
)
abstract class ShopItemDb: RoomDatabase() {
    abstract fun shopItems(): ShopItemDao
}