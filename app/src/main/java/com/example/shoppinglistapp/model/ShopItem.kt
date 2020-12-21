package com.example.shoppinglistapp.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Timestamp

@Entity(tableName = "shop_items")
class ShopItem: Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int
        set(value) {
            updateDate()
            field = value
        }

    var imageUri: String
        set(value) {
            updateDate()
            field = value
        }

    var content: String
        set(value) {
            updateDate()
            field = value
        }

    var isBought: Boolean
        set(value) {
            updateDate()
            field = value
        }

    var date: Long

    constructor(id: Int, imageUri: String, content: String, isBought: Boolean){
        this.id = id
        this.imageUri = imageUri
        this.content = content
        this.isBought = isBought

        date = System.currentTimeMillis()
    }

    @Ignore
    constructor(imageUri: String, content: String, isBought: Boolean){
        this.id = 0
        this.imageUri = imageUri
        this.content = content
        this.isBought = isBought

        date = System.currentTimeMillis()
    }

    override fun toString(): String {
        return "ShopItem(id=$id, imageUri='$imageUri', content='$content')"
    }

    private fun updateDate(){ this.date = System.currentTimeMillis()}

    fun hasSameContentWith(item: ShopItem): Boolean{

        val isContentSame = this.content == item.content
        val isStatusSame = this.isBought == item.isBought
        val isImageSame = this.imageUri == item.imageUri

        return isContentSame && isStatusSame && isImageSame
    }

}
