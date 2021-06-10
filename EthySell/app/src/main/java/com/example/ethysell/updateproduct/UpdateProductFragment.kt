package com.example.ethysell.updateproduct

import android.app.Activity
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.ethysell.const.FILE_AUTHORITY
import com.example.ethysell.const.GALLERY_REQUEST_CODE
import com.example.ethysell.const.REQUEST_CODE
import com.example.ethysell.databinding.FragmentUpdateProductBinding
import com.example.ethysell.databindingutils.bindProductImageView
import com.example.ethysell.model.Data
import okhttp3.ResponseBody
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class UpdateProductFragment : Fragment() {
    private val TAG = UpdateProductFragment::class.java.simpleName
    private lateinit var binding: FragmentUpdateProductBinding
    private lateinit var data: Data
    private lateinit var viewModel: UpdateViewModel
    private lateinit var photoFile: File
    private lateinit var galleryFile: File
    private var isCamera = true
    private lateinit var uri: Uri
    private val listOfTypes = arrayOf("Brand", "Product")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_update_product, container, false
        )
        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = UpdateViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UpdateViewModel::class.java)
        val args = UpdateProductFragmentArgs.fromBundle(requireArguments()).data
        data = args.copy()
        initialize(args)

        val imageUrl = data.imageUrl
        photoFile = File(imageUrl.toString())
        galleryFile = File(imageUrl.toString())







        viewModel.isCamera.observe(viewLifecycleOwner, Observer {
            isCamera = it
        })

        binding.productCameraImage.setOnClickListener {
            cameraAndGalleryPicture()
        }
        binding.submitButton.setOnClickListener {
            sendProduct()
        }

        viewModel.bitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            binding.productCameraImage.setImageBitmap(bitmap)
        })
        setSpinner()
        productResponse()
        setUpToolBar()

        return binding.root
    }

    private fun setSpinner() {

        val aA = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, listOfTypes)
        //set layout to use when the list of choices appear
        aA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //set adapter to Spinner
        binding.productSpinner.adapter = aA

    }

    private fun saveImage(image: Bitmap): String {
       var saveImagePath: String = ""
       val imageFilename = System.currentTimeMillis().toString() + ".jpg"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Folder Name")
        var success = true
        if (!storageDir.exists()){
            success = storageDir.mkdir()
        }
        if (success){
            val imageFile = File(storageDir,imageFilename)
            saveImagePath =imageFile.absolutePath
            try{
                val fout =FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                fout.close()

            }catch (e: Exception){
                e.printStackTrace()
            }


        }
        return saveImagePath

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
        uri = data?.data!!


        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
            viewModel.setBitmapImage(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    private fun initialize(data: Data) {
        val imageView = binding.productCameraImage
        val name = binding.productName
        val barcode = binding.productBarCode
        val description = binding.productDescription
        val ethyscore = binding.productEthyScore

        bindProductImageView(imageView, data)
        name.setText(data.name)
        barcode.setText(data.barcodeNo)
        description.setText(data.description)
        ethyscore.setText(data.ethicalScore)
    }

    private fun productResponse() {
        viewModel.updateResponse.observe(viewLifecycleOwner, Observer { response ->
            showToast(response)
        })
    }

    private fun showToast(warnings: String) {
        Toast.makeText(context, warnings, Toast.LENGTH_SHORT).show()
    }

    private fun sendProduct() {
        val txtName = binding.productName
        val txtDescription = binding.productDescription
        val txtScore = binding.productEthyScore
        val txtBarCodeNo = binding.productBarCode

        val name = txtName.text.toString().trim()
        val score = txtScore.text.toString().trim()
        val description = txtDescription.text.toString().trim()
        val barcode = txtBarCodeNo.text.toString().trim()

        if (name.isEmpty()) {
            txtName.error = "Enter name of Product"
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


        submitProduct(
            name = name,
            barCode = barcode,
            score = score,
            description = description,
            brand = "brand"
        )


    }

    private fun submitProduct(
        name: String,
        barCode: String,
        score: String,
        brand: String,
        description: String
    ) {

        val products = Data(
            id = data.id,
            name = name,
            type = brand,
            imageUrl = data.imageUrl,
            barcodeNo = barCode,
            ethicalScore = score,
            comments = null,
            description = description
        )

        if (isCamera) {
            viewModel.updateProduct(products, photoFile)
        } else {
            val parcelFileDescriptor =
                context?.contentResolver?.openAssetFileDescriptor(uri, "r", null) ?: return
            val file = File(activity?.cacheDir, requireContext().contentResolver.getFilename(uri))
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            galleryFile = file
            viewModel.updateProduct(products, galleryFile)
        }


    }

    private fun ContentResolver.getFilename(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }

    private fun cameraAndGalleryPicture() {
        val opString = arrayOf(
            "Take Photo", "Choose From Gallery",
            "Cancel"
        )
        val dbuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dbuilder.setTitle("Add Photo")
        dbuilder.setItems(opString, DialogInterface.OnClickListener { dialog, which ->
            if (opString[which] == "Take Photo") {
                getCameraImage()
            } else if (opString[which] == "Choose From Gallery") {
                getGalleryImage()
            } else {
                dialog.dismiss();
            }
        })
        dbuilder.show()
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

    }

    fun DownloadImageFromPath(path: String?): Bitmap? {
        var `in`: InputStream? = null
        var bmp: Bitmap? = null
        var responseCode = -1
        try {
            val url = URL(path) //"http://192.xx.xx.xx/mypath/img1.jpg
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.doInput = true
            con.connect()
            responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //download
                `in` = con.inputStream
                bmp = BitmapFactory.decodeStream(`in`)
                `in`.close()
            }
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }finally {
            return bmp
        }
    }



}