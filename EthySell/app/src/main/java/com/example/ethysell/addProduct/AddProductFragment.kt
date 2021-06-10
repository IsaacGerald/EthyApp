package com.example.ethysell.addProduct

import android.app.Activity
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.ethysell.Injection
import com.example.ethysell.MainActivity
import com.example.ethysell.R
import com.example.ethysell.const.*
import com.example.ethysell.databinding.FragmentAddProductBinding
import com.example.ethysell.model.Data
import com.google.android.material.snackbar.Snackbar
import retrofit2.Response
import java.io.*


class AddProductFragment : Fragment() {
    private val TAG = AddProductFragment::class.java.simpleName
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var photoFile: File
    private lateinit var viewModel: AddProductViewModel
    private lateinit var mediaHelper: MediaHelper
    private lateinit var uri: Uri
    private lateinit var galleryFile: File
    private var isCamera = true
    private val listOfTypes = arrayOf("Brand", "Product")
    private var itemType = "brand"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)

        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = AddProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddProductViewModel::class.java)

        binding.productCameraImage.setOnClickListener {
            cameraAndGalleryPicture()
        }

        viewModel.isCamera.observe(viewLifecycleOwner, Observer {
            isCamera = it
        })

        viewModel.postItemResponse.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })

        setSpinner()


        val btnSendProduct = binding.submitButton
        btnSendProduct.setOnClickListener {
            sendProduct()
        }

        photoFile = photoFile(FILE_NAME)
        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        productResponse()
        setUpToolBar()


        mediaHelper = MediaHelper()

        viewModel.bitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            binding.productCameraImage.setImageBitmap(bitmap)
        })



        return binding.root

    }



        private fun setSpinner() {
            val data = arrayOf("brand", "product")

            val adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, data)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            val spinner = binding.productSpinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val  type = parent.getItemAtPosition(position).toString()
                    itemType = type
                    viewModel.setType(type)
                    //Toast.makeText(requireContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }






        val aA = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listOfTypes)
        //set layout to use when the list of choices appear
        aA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //set adapter to Spinner
        binding.productSpinner.adapter = aA

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


    private fun productResponse() {
        viewModel.myProductResponse.observe(viewLifecycleOwner, Observer { response ->
           showToast(response)
        })
    }


    private fun sendProduct() {
        val txtName = binding.brandName
        val txtDescription = binding.productDescription
        val txtScore = binding.productEthyScore
        val txtBrand = binding.brandName
        val txtBarCodeNo = binding.productBarCode

        val name = txtName.text.toString().trim()
        val type = txtBrand.text.toString().trim()
        val score = txtScore.text.toString().trim()
        val description = txtDescription.text.toString().trim()
        val barcode = txtBarCodeNo.text.toString().trim()

        if (name.isEmpty()) {
            txtName.error = "Enter name of Product"
            return
        }
        if (type.isEmpty()) {
            txtBrand.error = "Enter Brand|type"
            return
        }
        if (score.isEmpty()) {
            txtScore.error = "Enter ethicalscore"
            return
        }
        if (description.isEmpty()) {
            txtDescription.error = "Enter description"
            return
        }
        if (barcode.isEmpty()) {
            txtBarCodeNo.error = "Enter BarCode"
            return
        }


        binding.productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = parent?.getItemAtPosition(position).toString()
                itemType = text
                //Toast.makeText(context, "selected item is $text", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }





        submitProduct(
            name = name,
            barCode = barcode,
            score = score,
            description = description,
            brand = itemType.toLowerCase()
        )


    }

    private fun showToast(warnings: String) {
        Toast.makeText(context, warnings, Toast.LENGTH_SHORT).show()
    }

    private fun submitProduct(
        name: String,
        barCode: String,
        score: String,
        brand: String,
        description: String
    ) {

        val products = Data(
            id = 56,
            name = name,
            type = brand,
            imageUrl = null,
            barcodeNo = barCode,
            ethicalScore = score,
            comments = null,
            description = description
        )

        if (isCamera){
            viewModel.postProducts(products, photoFile)
        }else{
            val parcelFileDescriptor =
                context?.contentResolver?.openAssetFileDescriptor(uri, "r", null) ?: return
            val file = File(activity?.cacheDir, context!!.contentResolver.getFilename(uri) )
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            viewModel.postProducts(products, file)
        }






    }

    private fun getGalleryImage() {

        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(galleryIntent, "Select Image"),
            GALLERY_REQUEST_CODE
        )

    }


    private fun getCameraImage() {


        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val fileProvider = FileProvider.getUriForFile(
            requireContext().applicationContext,
            FILE_AUTHORITY, photoFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(cameraIntent, REQUEST_CODE)
        } else {
            Toast.makeText(context, "Unable to open Camera", Toast.LENGTH_SHORT).show()
        }

    }


    private fun setUpToolBar() {
        val activity = activity as MainActivity
        activity.setSupportActionBar(binding.toolBar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(binding.toolBar, navController)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)

    }

    private fun photoFile(fileName: String): File {
        //use 'getExternalFilesDir' on Context to access package specific directories
        val storageDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setImageFromCamera()
        }

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setImageFromGallery(data)

        }


    }

    private fun setImageFromGallery(data: Intent?) {
        isCamera = false
        viewModel.imageState(isCamera)
        val thisUri = data?.data!!
        uri = thisUri
        Log.d(TAG, "setImageFromGallery: uri $uri")
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
            viewModel.setBitmapImage(bitmap)
            val file: String = photoFile.absolutePath
            Log.d(TAG, "onActivityResult: bitmap $bitmap")
            Log.d(TAG, "onActivityResult gallery: $uri")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getImageFile(uri: Uri) {
        val parcelFileDescriptor =
            context?.contentResolver?.openAssetFileDescriptor(uri, "r", null) ?: return
        val file = File(activity?.cacheDir, requireContext().contentResolver.getFilename(uri) )
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

    }

    fun ContentResolver.getFilename(uri: Uri): String{
        var name = ""
        var cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }


    private fun setImageFromCamera() {
        isCamera = true
        viewModel.imageState(isCamera)
        try {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            viewModel.setBitmapImage(bitmap)
            Log.d(TAG, "onActivityResult camera: ${photoFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun cameraAndGalleryPicture() {
        val opString = arrayOf(
            "Take Photo", "Choose From Gallery",
            "Cancel"
        )
        val dbuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dbuilder.setTitle("Add Photo")
        dbuilder.setItems(opString, DialogInterface.OnClickListener { dialog, which ->
            when {
                opString[which] == "Take Photo" -> {
                    getCameraImage()
                }
                opString[which] == "Choose From Gallery" -> {
                    getGalleryImage()
                }
                else -> {
                    dialog.dismiss();
                }
            }
        })
        dbuilder.show()
    }


}
