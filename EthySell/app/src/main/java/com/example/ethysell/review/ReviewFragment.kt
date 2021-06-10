package com.example.ethysell.review

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ethyscore.review.ReviewViewModel
import com.example.ethysell.Injection
import com.example.ethysell.MainActivity
import com.example.ethysell.R
import com.example.ethysell.const.USER_KEY
import com.example.ethysell.databinding.FragmentReviewBinding
import com.example.ethysell.model.Comment
import com.example.ethysell.model.CurrentUser
import com.example.ethysell.model.Data
import com.example.ethysell.model.User
import com.google.gson.Gson
import kotlin.random.Random


class ReviewFragment : Fragment() {
    private val TAG = ReviewFragment::class.java.simpleName
   private lateinit var binding: FragmentReviewBinding
   private lateinit var adapter: ReviewsAdapter
   private lateinit var viewModel: ReviewViewModel
   private lateinit var data: Data


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review, container, false)
        val args = ReviewFragmentArgs.fromBundle(requireArguments()).data
        data = args.copy()
        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = ReviewViewModelFactory(repository, args)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReviewViewModel::class.java)

        viewModel.getRecievedData(args)

        viewModel.commentResponse.observe(viewLifecycleOwner, Observer {
            binding.send.isEnabled = true
            showToast(it)
        })

        binding.send.setOnClickListener {
            postComment(data)
        }

        binding.reviewSwipeRefresh.setOnRefreshListener {
            viewModel.refreshProducts()
            initData()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable {
                binding.reviewSwipeRefresh.isRefreshing = false
            }, 3000)
        }

        initView()
        initData()



        return binding.root
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
        val currentUser = getCurrentUser()
        val mbr = currentUser.data.userInfo.role
        if (mbr == "staff") {
            val data = handleDelete(comment.id, data)
            viewModel.deleteComment(comment.id, true, data)
            initData()
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

    private fun showToast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }



    private fun initData() {

        viewModel.getDetailProduct(data.id).observe(viewLifecycleOwner, Observer {

               val comments = it.comments
            if (!comments.isNullOrEmpty()){
                val newComment = viewModel.getVisibleComments(comments)
                adapter.submitList(newComment)
            }


        })
    }

    private fun initView() {
        adapter = ReviewsAdapter(OnCommentClickListener {
            deleteCommentDialog(it)
        })
        binding.reviewRv.layoutManager = LinearLayoutManager(this.requireContext())
        binding.reviewRv.adapter = adapter
    }


    private fun postComment(data: Data) {
        val txtComment = binding.txtComment
        val currentUser: CurrentUser = getCurrentUser()
        val user = getUser(currentUser)


        val productComment = data.comments
        val listOfComments = mutableListOf<Comment>()
        listOfComments.clear()
        if (!productComment.isNullOrEmpty()) {
            listOfComments.addAll(productComment)
        }

        val comment = txtComment.text.toString().trim()
        if (comment.isEmpty()) {
            return
        } else {
            val commentId = getRandomNumber(20, 100)
            val itemId = data.id.toString()
            val myComment = Comment(comment, commentId, false, itemId , user)
            listOfComments.add(myComment)
            data.comments = listOfComments
            viewModel.postComment(data.id, comment, currentUser, data)
            binding.send.isEnabled = false
        }

        initData()

    }

    private fun getUser(currentUser: CurrentUser): User {
        val userId = currentUser.data.userInfo.id
        val role = currentUser.data.userInfo.role
        val name = currentUser.data.userInfo.name
        val user = User(userId, name, role)
        return user
    }



    private fun getCurrentUser(): CurrentUser {
        val gson = Gson()
        val sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val user = sharedPref.getString(USER_KEY, "")
        return gson.fromJson(user, CurrentUser::class.java)

    }

    private fun getRandomNumber(from: Int, to: Int): Int {
        val random = Random
        return random.nextInt(to - from) + from
    }


}