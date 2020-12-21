package com.example.shoppinglistapp.ui.recycler

import android.net.Uri
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppinglistapp.R
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.utils.DateUtils
import com.example.shoppinglistapp.utils.SparseBooleanArraySerializable
import com.mikhaellopez.circularimageview.CircularImageView
import java.sql.Timestamp
import java.util.*


class ShopItemListAdapter(
    val itemList: MutableList<ShopItem>,
    val selectedItems: SparseBooleanArraySerializable,
    private val itemListener: RecyclerViewClickListener
) : RecyclerView.Adapter<ShopItemListAdapter.ShopItemViewHolder>() {


    var isImagePermitted: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val layoutView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item,
            parent,
            false
        )
        return ShopItemViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        private var image: CircularImageView = itemView.findViewById(R.id.list_item_image)
        private var isBoughtImage: ImageView = itemView.findViewById(R.id.is_bought_image)
        private var description: TextView = itemView.findViewById(R.id.list_item_description_edit_text)

        private var date: TextView = itemView.findViewById(R.id.date_text)


        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(shopItem: ShopItem) {
            if (shopItem.isBought) isBoughtImage.setImageResource(R.drawable.ic_bought)
            else isBoughtImage.setImageResource(android.R.color.transparent)

            description.text = shopItem.content
            date.text = DateUtils.getStringFromTimestamp(Timestamp(shopItem.date))

            bindImage()
        }

        override fun onClick(v: View?) {
            itemListener.onRecyclerViewItemClicked(v, adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            itemListener.onRecyclerViewItemLongClick(v, adapterPosition)
            return true
        }

        private fun bindImage() {

            if (selectedItems.get(adapterPosition, false)) {

                Glide.with(itemView.context)
                    .load(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_selected
                        )
                    )
                    .into(image)

            } else {
                val shopItem = itemList[adapterPosition]

                if (isImagePermitted) {
                    setupImage(shopItem)
                } else {
                    Glide.with(itemView.context)
                        .load(
                            getDrawable(
                                itemView.context,
                                R.drawable.ic_shop_item
                            )
                        )
                        .into(image)
                }
            }


        }

        private fun setupImage(shopItem: ShopItem) {

            val imageUri = Uri.parse(shopItem.imageUri)

            Glide.with(itemView.context)
                .load(imageUri)
                .error(
                    getDrawable(
                        itemView.context,
                        R.drawable.ic_shop_item
                    )
                )
                .into(image)
        }


    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList<Int>(selectedItems.size())

        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }


    fun toggleSelection(position: Int) {
        //currentSelectedIndex = position

        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }

        notifyItemChanged(position)
    }


    interface RecyclerViewClickListener {

        fun onRecyclerViewItemClicked(v: View?, position: Int)

        fun onRecyclerViewItemLongClick(v: View?, position: Int)
    }
}