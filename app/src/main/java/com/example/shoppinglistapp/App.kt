package com.example.shoppinglistapp

import android.app.Application
import com.example.shoppinglistapp.di.AppComponent
import com.example.shoppinglistapp.di.DaggerAppComponent
import com.example.shoppinglistapp.di.RoomModule

class App : Application() {

    companion object{
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .roomModule(RoomModule(this))
            .build()
    }
}