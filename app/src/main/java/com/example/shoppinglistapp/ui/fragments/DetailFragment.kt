package com.example.shoppinglistapp.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.*
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.shoppinglistapp.R
import com.example.shoppinglistapp.model.ShopItem
import com.example.shoppinglistapp.presenters.ShopItemDetailPresenter
import com.example.shoppinglistapp.utils.DateUtils
import com.example.shoppinglistapp.viewstates.ShopItemView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_detail.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.File
import java.sql.Timestamp

private const val IMAGE_PICK_CODE = 123
private const val IMAGE_CAPTURE_CODE = 42

class DetailFragment : MvpAppCompatFragment(), ShopItemView {


    @InjectPresenter
    lateinit var presenter: ShopItemDetailPresenter

    //Variable for requesting permissions
    private lateinit var galleryPermReqLauncher: ActivityResultLauncher<String>

    private var photoFile: File? = null

    private var currentItem: ShopItem? = null
    private var imageUriString: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        galleryPermReqLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    pickImageFromLocalStorage()
                } else
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.images_require_permissions),
                        Toast.LENGTH_SHORT
                    ).show()
            }


        if (savedInstanceState != null) {
            photoFile = savedInstanceState.getSerializable("file") as File?

            currentItem = savedInstanceState.getSerializable("item") as ShopItem?

            is_bought_checkbox.isChecked = savedInstanceState.getBoolean("isBought", false)
            list_item_description_edit_text.setText(savedInstanceState.getString("content", ""))

            imageUriString = savedInstanceState.getString("uri", "")

            if (imageUriString.isEmpty()) {
                list_item_image.setImageDrawable(
                    getDrawable(
                        requireContext(),
                        R.drawable.ic_shop_item
                    )
                )
            } else {
                val imageUri = Uri.parse(imageUriString)
                list_item_image.setImageURI(imageUri)
            }
        }

        init()
    }

    private fun init() {


        val action = arguments?.getString("action", "create")
        when {
            action.equals("create") -> {
                save_item_btn.setOnClickListener {
                    presenter.createItem(getShopItemFromFields())
                }
            }

            action.equals("edit") -> {
                val itemId = arguments?.getInt("id", 0) as Int

                if (currentItem == null)
                    presenter.loadItem(itemId)


                save_item_btn.setOnClickListener {
                    val itemToUpdate = getShopItemFromFields()

                    if (itemToUpdate.hasSameContentWith(currentItem!!)) {
                        finishWork()
                    } else {
                        itemToUpdate.id = itemId
                        presenter.updateItem(itemToUpdate)
                    }

                }
            }
        }

        list_item_image.setOnClickListener {
            showSetImageDialog()
        }


    }

    override fun showItem(item: ShopItem) {
        currentItem = item

        imageUriString = item.imageUri

        if (imageUriString.isEmpty()) {
            list_item_image.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_shop_item))
        } else {
            val imageUri = Uri.parse(imageUriString)
            Glide.with(requireContext())
                .load(imageUri)
                .error(getDrawable(requireContext(), R.drawable.ic_shop_item))
                .into(list_item_image)

        }

        is_bought_checkbox.isChecked = item.isBought
        list_item_description_edit_text.setText(item.content)
    }

    override fun showError(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun finishWork() {
        parentFragmentManager.popBackStack()
    }

    private fun getShopItemFromFields(): ShopItem {
        return ShopItem(
            imageUriString,
            list_item_description_edit_text.text.toString(),
            is_bought_checkbox.isChecked
        )
    }

    private fun showSetImageDialog() {
        val listItems = arrayOf(
            getString(R.string.get_from_camera),
            getString(R.string.get_from_gallery),
            getString(R.string.set_default)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.set_image))
            .setItems(
                listItems
            ) { _, which ->
                when (which) {
                    0 -> setUpImageFromCamera()
                    1 -> setUpImageFromGallery()
                    2 -> setUpDefaultImage()
                }
            }
            .show()
    }

    private fun setUpImageFromCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile()

        val fileProvider = FileProvider.getUriForFile(requireContext(), "com.example.shoppinglistapp.fileprovider", photoFile!!)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)


        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
        }else{
            Toast.makeText(requireContext(), "Unable to open camera!", Toast.LENGTH_SHORT).show()
        }


    }

    private fun getPhotoFile(): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(DateUtils.getStringFromTimestamp(Timestamp(System.currentTimeMillis())), ".jpg", storageDirectory)

    }

    private fun setUpImageFromGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                galleryPermReqLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                pickImageFromLocalStorage()
            }

        } else {
            pickImageFromLocalStorage()
        }
    }

    private fun setUpDefaultImage() {
        list_item_image.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_shop_item))
        imageUriString = ""
    }

    private fun pickImageFromLocalStorage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK){
            when (requestCode){
                IMAGE_PICK_CODE -> {
                    if (data?.data != null) {
                        imageUriString = data.data.toString()

                        Glide.with(requireContext())
                            .load(Uri.parse(imageUriString))
                            .error(getDrawable(requireContext(), R.drawable.ic_shop_item))
                            .into(list_item_image)

                    }
                }
                IMAGE_CAPTURE_CODE -> {

                    val uri = Uri.fromFile(photoFile)
                    imageUriString = uri.toString()

                    Glide.with(requireContext())
                        .load(Uri.parse(imageUriString))
                        .error(getDrawable(requireContext(), R.drawable.ic_shop_item))
                        .into(list_item_image)

                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("item", currentItem)

        outState.putBoolean("isBought", is_bought_checkbox.isChecked)
        outState.putString("content", list_item_description_edit_text.text.toString())
        outState.putString("uri", imageUriString)

        outState.putSerializable("file", photoFile)
    }

}