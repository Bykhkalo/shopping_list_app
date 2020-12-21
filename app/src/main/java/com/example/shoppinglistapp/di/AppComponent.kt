package com.example.shoppinglistapp.di

import com.example.shoppinglistapp.presenters.ShopItemDetailPresenter
import com.example.shoppinglistapp.presenters.ShopItemListPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class])
interface AppComponent {

    fun inject(presenter: ShopItemDetailPresenter)
    fun inject(presenter: ShopItemListPresenter)

}