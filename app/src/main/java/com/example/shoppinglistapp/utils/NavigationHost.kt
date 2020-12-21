package com.example.shoppinglistapp.utils

import androidx.fragment.app.Fragment

interface NavigationHost {

    companion object{
        const val TAG = "tag"
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean)
}