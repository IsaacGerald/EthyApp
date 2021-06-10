package com.example.ethysell.productUi

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ethysell.Injection
import com.example.ethysell.MainActivity
import com.example.ethysell.R
import com.example.ethysell.adapters.OnProductClickListener
import com.example.ethysell.adapters.ProductAdapter
import com.example.ethysell.const.CURRENT_USER
import com.example.ethysell.const.SPAN_COUNT
import com.example.ethysell.databinding.FragmentProductBinding
import com.example.ethysell.model.CurrentUser
import com.example.ethysell.model.Data
import com.example.ethysell.network.asDomainData
import com.google.gson.Gson

const val USER_KEY = "user-key"

class ProductFragment : Fragment() {
    private val TAG = ProductFragment::class.java.simpleName
    private lateinit var binding: FragmentProductBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = getCurrentUser()
        if (currentUser?.data == null){
            findNavController().navigate(R.id.action_productFragment_to_loginFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false)
        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = ProductViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        binding.floatingActionBar.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_addProductFragment)
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshProducts()
            init()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable {
                binding.swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }

        setHasOptionsMenu(true)
        init()
        navigateToSelectedFragment()
        setUpToolBar()
        fetchResponse()

        return binding.root

    }

    private fun fetchResponse() {
        viewModel.fetchResponse.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun navigateToSelectedFragment() {
        viewModel.navigateToSelectedProduct.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(
                    ProductFragmentDirections.actionProductFragmentToProductDetailsFragment(
                        it
                    )
                )
                viewModel.displayProductCompleted()
            }


        })
    }


    private fun getCurrentUser(): CurrentUser? {
        val gson = Gson()
        val sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val user = sharedPref.getString(USER_KEY, "")
        return gson.fromJson(user, CurrentUser::class.java)
    }

    private fun init() {
        adapter = ProductAdapter(OnProductClickListener {
            viewModel.displayProduct(it)
        })
        binding.productsRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), SPAN_COUNT)
        binding.productsRecyclerView.adapter = adapter

        viewModel.dataFromDatabase().observe(viewLifecycleOwner, Observer {
            it.let {
                val data = it.asDomainData()
                adapter.submitList(data)
            }


        })

    }


    private fun setUpToolBar() {
        val activity = activity as MainActivity
        activity.setSupportActionBar(binding.toolBar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(binding.toolBar, navController)

    }

    private fun logout() {
        val sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        findNavController().navigate(R.id.action_productFragment_to_loginFragment)
    }
    private fun logoutDialog() {
        val opString = arrayOf(
            "Logout", "Cancel"
        )
        val dbuilder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        dbuilder.setTitle("Logout Account")
        dbuilder.setItems(opString, DialogInterface.OnClickListener { dialog, which ->
            when {
                opString[which] == "Logout" -> {
                    logout()
                }
                opString[which] == "Cancel" -> {
                    dialog.dismiss()
                }
                else -> {
                    dialog.dismiss();
                }
            }
        })
        dbuilder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout ->{
                logoutDialog()
            }
                else ->
                    return super.onOptionsItemSelected(item)
        }

        return true
    }
}