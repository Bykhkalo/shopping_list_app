package com.example.shoppinglistapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.shoppinglistapp.R
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.presenters.ShopItemListPresenter
import com.example.shoppinglistapp.ui.recycler.ShopItemListAdapter
import com.example.shoppinglistapp.utils.DataType
import com.example.shoppinglistapp.utils.IOnBackPressed
import com.example.shoppinglistapp.utils.NavigationHost
import com.example.shoppinglistapp.utils.SparseBooleanArraySerializable
import com.example.shoppinglistapp.viewstates.ShopItemListView
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer
import kotlinx.android.synthetic.main.fragment_list.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.*

class ListFragment : MvpAppCompatFragment(), ShopItemListView, IOnBackPressed,
    ShopItemListAdapter.RecyclerViewClickListener {

    private lateinit var adapter: ShopItemListAdapter

    private lateinit var navigationDrawer: SNavigationDrawer

    private var currentDataType: DataType = DataType.ALL

    private var isSelectionModeEnabled = false
    private var selectedItems: SparseBooleanArraySerializable? = null

    //Variable for requesting permissions
    private lateinit var galleryPermReqLauncher: ActivityResultLauncher<String>

    @InjectPresenter
    lateinit var presenter: ShopItemListPresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (savedInstanceState != null){
            val savedDataType = savedInstanceState.getSerializable("dataType") as DataType
            currentDataType = savedDataType

            selectedItems = savedInstanceState.getSerializable("selected_items") as SparseBooleanArraySerializable?

        }

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryPermReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it) {
                adapter.isImagePermitted = true
                adapter.notifyDataSetChanged()
            }
            else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.images_require_permissions),
                    Toast.LENGTH_SHORT
                ).show()

                adapter.isImagePermitted = false
                adapter.notifyDataSetChanged()
            }

        }

        init()
    }


    private fun init() {
        initNavigationBar()
        add_shop_item_button.setOnClickListener {
            val args = Bundle()
            args.putString("action", "create")

            val fragment = DetailFragment()
            fragment.arguments = args

            val navigationHost = requireActivity() as NavigationHost
            navigationHost.navigateTo(fragment, true)
        }

        presenter.loadData(currentDataType)
    }

    private fun initNavigationBar() {
        navigationDrawer = nav_view

        val toolbarTitle : String

        when (currentDataType){
            DataType.ALL -> {
                toolbarTitle =  getString(R.string.shop_items)
                navigationDrawer.currentPos = 0
            }
            DataType.NOT_BOUGHT -> {
                toolbarTitle = getString(R.string.items_to_buy)
                navigationDrawer.currentPos = 1
            }
            DataType.BOUGHT -> {
                toolbarTitle = getString(R.string.bought_items)
                navigationDrawer.currentPos = 2
            }
        }

        shop_list_toolbar.title = toolbarTitle

        val menuItems: MutableList<MenuItem> = ArrayList()
        menuItems.add(MenuItem(getString(R.string.all_items), R.drawable.news_bg))
        menuItems.add(MenuItem(getString(R.string.items_to_buy), R.drawable.feed_bg))
        menuItems.add(MenuItem(getString(R.string.bought_items), R.drawable.message_bg))

        navigationDrawer.menuItemList = menuItems

        navigationDrawer.drawerListener = object : SNavigationDrawer.DrawerListener {
            override fun onDrawerOpening() {
                val stateSet =
                    intArrayOf(android.R.attr.state_checked * if (navigationDrawer.isDrawerOpen) -1 else 1)
                nav_image_view.setImageState(stateSet, true)
            }

            override fun onDrawerClosing() {
                val stateSet =
                    intArrayOf(android.R.attr.state_checked * if (navigationDrawer.isDrawerOpen) -1 else 1)
                nav_image_view.setImageState(stateSet, true)

                item_list.isEnabled = true
            }

            override fun onDrawerOpened() {}
            override fun onDrawerClosed() {}
            override fun onDrawerStateChanged(newState: Int) {}
        }

        nav_image_view.setOnClickListener { if (navigationDrawer.isDrawerOpen) navigationDrawer.closeDrawer() else navigationDrawer.openDrawer() }

        navigationDrawer.onMenuItemClickListener = SNavigationDrawer.OnMenuItemClickListener {

            val title: String

            presenter.loadData(
                when (it) {
                    0 -> {
                        title = getString(R.string.shop_items)
                        currentDataType = DataType.ALL
                        currentDataType
                    }
                    1 -> {
                        title = getString(R.string.items_to_buy)
                        currentDataType = DataType.NOT_BOUGHT
                        currentDataType
                    }
                    2 -> {
                        title = getString(R.string.bought_items)
                        currentDataType = DataType.BOUGHT
                        currentDataType
                    }
                    else -> {
                        title = getString(R.string.shop_items)
                        currentDataType = DataType.ALL
                        currentDataType
                    }
                }
            )

            shop_list_toolbar.title = title
        }

    }

    override fun onRecyclerViewItemClicked(v: View?, position: Int) {

        if (adapter.getSelectedItemCount() > 0) {
            enableSelectionMode(position)

        } else {
            val id = adapter.itemList[position].id

            val args = Bundle()
            args.putString("action", "edit")
            args.putInt("id", id)

            val fragment = DetailFragment()
            fragment.arguments = args

            val navigationHost = requireActivity() as NavigationHost
            navigationHost.navigateTo(fragment, true)
        }

    }


    override fun onRecyclerViewItemLongClick(v: View?, position: Int) {
        if (!navigationDrawer.isDrawerOpen){
            enableSelectionMode(position)
        }

    }



    private fun enableSelectionMode() {
        if (!isSelectionModeEnabled) {

            enableSelectionModeToolbar()
            navigationDrawer.isSwipeEnabled = false
            isSelectionModeEnabled = true

            val count = adapter.getSelectedItemCount()

            if (count == 0) {
                disableSelectionMode()

            } else {
                shop_list_toolbar.title = count.toString()
            }
        }

    }

    private fun enableSelectionMode(position: Int) {
        if (!isSelectionModeEnabled) {

            enableSelectionModeToolbar()
            navigationDrawer.isSwipeEnabled = false
            isSelectionModeEnabled = true
        }

        toggleSelection(position)

    }


    private fun toggleSelection(position: Int) {
        adapter.toggleSelection(position)

        val count = adapter.getSelectedItemCount()

        if (count == 0) {
            disableSelectionMode()

        } else {
            shop_list_toolbar.title = count.toString()
        }

    }

    private fun disableSelectionMode() {
        //DisableSelectionMode
        adapter.clearSelections()
        disableSelectionModeToolbar()
        navigationDrawer.isSwipeEnabled = true

        isSelectionModeEnabled = false
    }

    override fun showData(items: List<ShopItem>) {

        val selectedItemsArray = if (selectedItems == null) SparseBooleanArraySerializable() else selectedItems

        adapter = ShopItemListAdapter(items as MutableList<ShopItem>, selectedItemsArray!!,this)
        selectedItems = adapter.selectedItems

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                galleryPermReqLauncher.launch( Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                adapter.isImagePermitted = true
            }

        } else {
            adapter.isImagePermitted = true
        }

        item_list.adapter = adapter

        if (adapter.getSelectedItemCount() > 0) enableSelectionMode()
        else disableSelectionMode()

    }

    override fun showError(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun removeRecyclerViewItem(position: Int) {
        adapter.itemList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun notifyRecyclerViewItemUpdated(newItem: ShopItem, position: Int, dataType: DataType) {

        if (dataType == currentDataType || currentDataType == DataType.ALL) {
            adapter.notifyItemChanged(position)
            disableSelectionMode()
        } else {
            disableSelectionMode()
            presenter.loadData(currentDataType)
        }

    }

    override fun notifyRecyclerViewItemsUpdated(
        newItems: List<ShopItem>,
        dataType: DataType
    ) {

        if (dataType == currentDataType || currentDataType == DataType.ALL){
            adapter.notifyDataSetChanged()
            disableSelectionMode()
        }else{
            disableSelectionMode()
            presenter.loadData(currentDataType)
        }

    }

    override fun removeRecyclerViewItems(items: List<ShopItem>) {
        items.forEach {
            adapter.itemList.remove(it)
        }

        disableSelectionMode()
        adapter.notifyDataSetChanged()
    }


    private fun enableSelectionModeToolbar() {
        shop_list_toolbar.menu.clear()

        nav_image_view.visibility = View.INVISIBLE
        shop_list_toolbar.inflateMenu(R.menu.selected_item_menu)

        shop_list_toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_make_bought -> {
                    val itemPositions = adapter.getSelectedItems()

                    if (itemPositions.size == 1) {
                        val itemToUpdate = adapter.itemList[itemPositions[0]]
                        itemToUpdate.isBought = true
                        presenter.updateItem(itemToUpdate, itemPositions[0], DataType.BOUGHT)
                    } else {
                        val itemsToUpdate: MutableList<ShopItem> = mutableListOf()

                        var bufferItem: ShopItem

                        for (i in itemPositions.indices) {

                            bufferItem = adapter.itemList[itemPositions[i]]
                            bufferItem.isBought = true

                            itemsToUpdate.add(adapter.itemList[itemPositions[i]])
                        }

                        presenter.updateItems(itemsToUpdate, DataType.BOUGHT)
                    }

                }

                R.id.action_make_not_bought -> {
                    val itemPositions = adapter.getSelectedItems()

                    if (itemPositions.size == 1) {
                        val itemToUpdate = adapter.itemList[itemPositions[0]]
                        itemToUpdate.isBought = false
                        presenter.updateItem(itemToUpdate, itemPositions[0], DataType.NOT_BOUGHT)
                    } else {
                        val itemsToUpdate: MutableList<ShopItem> = mutableListOf()

                        var bufferItem: ShopItem

                        for (i in itemPositions.indices) {

                            bufferItem = adapter.itemList[itemPositions[i]]
                            bufferItem.isBought = false

                            itemsToUpdate.add(adapter.itemList[itemPositions[i]])
                        }

                        presenter.updateItems(itemsToUpdate, DataType.NOT_BOUGHT)
                    }
                }

                R.id.action_select_all -> {
                    adapter.clearSelections()

                    for (i in 0 until adapter.itemCount) {
                        toggleSelection(i)
                    }
                }

                R.id.action_cancel -> {
                    disableSelectionMode()
                }

                R.id.action_delete -> {
                    removeItems()
                }
            }

            true
        }


    }

    private fun disableSelectionModeToolbar() {
        nav_image_view.visibility = View.VISIBLE
        shop_list_toolbar.title = getString(when (currentDataType){
            DataType.ALL -> R.string.shop_items
            DataType.BOUGHT -> R.string.bought_items
            DataType.NOT_BOUGHT -> R.string.items_to_buy
        })
        shop_list_toolbar.menu.clear()
    }

    private fun removeItems() {
        val itemPositions = adapter.getSelectedItems()
        val itemsToRemove: MutableList<ShopItem> = mutableListOf()

        for (i in itemPositions.indices.reversed()) {
            itemsToRemove.add(adapter.itemList[itemPositions[i]])
        }
        presenter.removeItems(itemsToRemove)
    }

    override fun onBackPressed(): Boolean {
        return if (isSelectionModeEnabled) {
            disableSelectionMode()
            true
        } else {
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("dataType", currentDataType)
        outState.putSerializable("selected_items", selectedItems)
    }

}