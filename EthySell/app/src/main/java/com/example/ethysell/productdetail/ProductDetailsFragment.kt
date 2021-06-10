package com.example.ethysell.productdetail

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ethysell.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ethysell.Injection
import com.example.ethysell.review.ReviewsAdapter
import com.example.ethysell.MainActivity
import com.example.ethysell.databinding.FragmentProductDetailsBinding
import com.example.ethysell.model.Comment
import com.example.ethysell.model.CurrentUser
import com.example.ethysell.model.Data
import com.example.ethysell.network.asDomainData
import com.example.ethysell.review.OnCommentClickListener
import com.google.gson.Gson


class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var data: Data
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var adapter: ReviewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)

        val args = ProductDetailsFragmentArgs.fromBundle(requireArguments()).data
        data = args.copy()
        binding.data = data
        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = ProductDetailViewModelFactory(repository, args)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductDetailViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.recievedData(data)

        moveToReviews()
        initRecylerview()
        setHasOptionsMenu(true)

        setUpToolBar()
        binding.productImage.clipToOutline = true

        binding.txtAddComment.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToReviewFragment(
                    data
                )
            )
        }
        viewModel.data.observe(viewLifecycleOwner, Observer {
            data = it
        })

        viewModel.deleteCommentResponse.observe(viewLifecycleOwner, Observer {

            showToast(it)

        })

        viewModel.deleteItemResponse.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })

        binding.detailSwipeRefresh.setOnRefreshListener {
            viewModel.refreshProducts()
            initRecylerview()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable {
                binding.detailSwipeRefresh.isRefreshing = false
            }, 3000)
        }


        return binding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToReviews() {
        binding.seeReviews.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToReviewFragment(
                    data
                )
            )
        }
    }

    private fun initRecylerview() {
        adapter = ReviewsAdapter(OnCommentClickListener {
            deleteCommentDialog(it)
        })

        binding.reviewRv.layoutManager = LinearLayoutManager(this.requireContext())
        binding.reviewRv.adapter = adapter
        viewModel.getDetailProduct(data.id).observe(viewLifecycleOwner, Observer {
            val comment = it.asDomainData().comments
            if (!comment.isNullOrEmpty()){
                val newComments = viewModel.getVisibleComments(comment)

                adapter.submitList(newComments)
            }

        })
    }

    private fun deleteProductDialog() {
        val opString = arrayOf(
            "Delete", "No, don't delete"
        )
        val dbuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dbuilder.setTitle("Delete Item")
        dbuilder.setItems(opString, DialogInterface.OnClickListener { dialog, which ->
            when {
                opString[which] == "Delete" -> {
                    deleteItem()
                }
                opString[which] == "No, don't delete" -> {
                    dialog.dismiss()
                }
                else -> {
                    dialog.dismiss();
                }
            }
        })
        dbuilder.show()
    }






    private fun deleteCommentDialog(comment: Comment) {
        val opString = arrayOf(
            "Delete", "Don't delete"
        )
        val dbuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dbuilder.setTitle("Delete Comment")
        dbuilder.setItems(opString, DialogInterface.OnClickListener { dialog, which ->
            if (opString[which] == "Delete") {
                deleteComment(comment)
            } else if (opString[which] == "Don't delete") {
                dialog.dismiss()
            } else {
                dialog.dismiss();
            }
        })
        dbuilder.show()
    }

    private fun deleteComment(comment: Comment) {
        //Toast.makeText(context, "Deleting ${comment.comment}", Toast.LENGTH_SHORT).show()
        val currentUser = getCurrentUser()
        val mbr = currentUser?.data?.userInfo?.role
        if (mbr == "staff") {

           val newData = handleDelete(comment.id, data)

            viewModel.deleteComment(comment.id, true, newData)
            initRecylerview()

        } else {
            showToast("Can't delete, only staffs are allowed to delete")
        }

    }

    private fun handleDelete(id: Int, data: Data): Data {
        val listComments = mutableListOf<Comment>()
        val comments = data.comments
        if (comments != null){
            listComments.addAll(comments)
            val iterator = listComments.iterator()
            while (iterator.hasNext()){
                val item = iterator.next()
                if (item.id == id){
                    iterator.remove()
                }
            }
        }

        data.comments = listComments
        return data



    }

    private fun getCurrentUser(): CurrentUser? {
        val gson = Gson()
        val sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val user = sharedPref.getString(com.example.ethysell.productUi.USER_KEY, "")
        return gson.fromJson(user, CurrentUser::class.java)
    }


    private fun setUpToolBar() {
        val activity = activity as MainActivity
        activity.setSupportActionBar(binding.toolBar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(binding.toolBar, navController)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_detail_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_delete ->
                deleteProductDialog()
            R.id.action_update ->
                  updateProduct()
            else ->
                return NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                ) || super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun deleteItem() {
        viewModel.deleteItem(data.id)
    }

    private fun updateProduct() {
        findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToUpdateProductFragment(data))
    }


}