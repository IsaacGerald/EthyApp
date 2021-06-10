package com.example.ethysell.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.example.ethyscore.login.LoginViewModel
import com.example.ethysell.Injection
import com.example.ethysell.MainActivity
import com.example.ethysell.R
import com.example.ethysell.const.CURRENT_USER
import com.example.ethysell.databinding.FragmentLoginUserBinding
import com.example.ethysell.model.CurrentUser
import com.example.ethysell.productUi.USER_KEY
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class LoginFragment : Fragment() {
    private val TAG = LoginFragment::class.java.simpleName
    private lateinit var binding: FragmentLoginUserBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = (activity as MainActivity)
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.blue_wave)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_user, container, false)

        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.LoginBtn.setOnClickListener {
            loginUser()
        }

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {

            if (it.isSuccessful) {
                val user = it.body()
                if (user != null){
                    showToast(user.message)
                    clearText()
                    changeStatusBarColor()
                    saveUserToSharedPref(user)
                    findNavController().navigate(R.id.action_loginFragment_to_productFragment)
                }

            } else {
                showSnackBar(it.message())
            }

        })

        viewModel.exceptionResponse.observe(viewLifecycleOwner, Observer {
            showSnackBar(it)
        })



        return binding.root
    }

    private fun saveUserToSharedPref(user: CurrentUser) {
        val sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val cUser = gson.toJson(user)
        with(sharedPref.edit()) {
            putString(USER_KEY, cUser)
            apply()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun clearText() {
        binding.emailLoginEditText.text?.clear()
        binding.pswLoginEditText.text?.clear()
    }

    private fun loginUser() {
        val txtEmail = binding.emailLoginEditText
        val txtPassword = binding.pswLoginEditText

        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        val validation = AwesomeValidation(ValidationStyle.BASIC)

        validation.addValidation(
            requireActivity(),
            R.id.email_loginEditText,
            Patterns.EMAIL_ADDRESS,
            R.string.email_error_string
        )

        validation.addValidation(
            requireActivity(),
            R.id.psw_loginEditText,
            ".{6,}",
            R.string.invalid_password
        )
        if (validation.validate()) {
            viewModel.loginUser(email, password)
            // binding.progressBar.visibility = View.VISIBLE
            binding.LoginBtn.isEnabled = false
        }




        viewModel.loginUser(email, password)
        //binding.progressBar.visibility = View.VISIBLE
        binding.LoginBtn.isEnabled = false
    }

    private fun changeStatusBarColor() {
        val activity = (activity as MainActivity)
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.white_variation)
    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}