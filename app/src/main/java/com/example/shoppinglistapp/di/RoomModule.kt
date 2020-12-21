package com.example.shoppinglistapp.di

import android.content.Context
import androidx.room.Room
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.room.ShopItemDao
import com.example.shoppinglistapp.room.ShopItemDb
import com.example.shoppinglistapp.room.ShopItemsRoomRepository
import com.example.shoppinglistapp.room.ShopItemsRoomRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideDatabase(): ShopItemDb {
        return Room.databaseBuilder(context, ShopItemDb::class.java, "database")
            .build()
    }

    @Provides
    fun provideShopItemDao(db: ShopItemDb): ShopItemDao{
        return db.shopItems()
    }

    @Provides
    fun provideRoomRepository(dao: ShopItemDao): ShopItemsRoomRepository{
        return ShopItemsRoomRepositoryImpl(dao)
    }

}