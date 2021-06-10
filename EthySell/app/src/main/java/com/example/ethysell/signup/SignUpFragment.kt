package com.example.ethysell.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.ethysell.Injection
import com.example.ethysell.MainActivity
import com.example.ethysell.R
import com.example.ethysell.const.SELECT_USER
import com.example.ethysell.const.STAFF
import com.example.ethysell.const.USER
import com.example.ethysell.databinding.FragmentSignupUserBinding
import com.google.android.material.snackbar.Snackbar


class SignUpFragment : Fragment() {
    private val TAG = SignUpFragment::class.java.simpleName
    private lateinit var binding: FragmentSignupUserBinding
    private lateinit var viewModel: RegisterViewModel
    private var user = USER

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_user, container, false)
        val repository = Injection.provideProductRepository(this.requireContext())
        val viewModelFactory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterViewModel::class.java)
        binding.signUpButton.setOnClickListener {
            registerUser()
        }
        viewModel.userResponse.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                //binding.progressBar.visibility = View.INVISIBLE
                binding.signUpButton.isEnabled = true
                if (it.isSuccessful) {
                    val body = it.body()
                    if (body != null) {
                        showToast(body.message)
                        clearTexts()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }

                } else {
                    showSnackBar(it.message())
                }
            }


        })

        viewModel.exceptionAuth.observe(viewLifecycleOwner, Observer {
            showSnackBar(it)
        })

        binding.LoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        viewModel.newUser.observe(viewLifecycleOwner, Observer {
            user = it
        })

        setSpinner()


        return binding.root
    }

    private fun setSpinner() {
        val data = arrayOf(SELECT_USER, STAFF, USER)

        val adapter = ArrayAdapter(this.requireContext(), R.layout.spinner_item_selected, data)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val spinner = binding.spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newUser = parent.getItemAtPosition(position).toString()
                user = newUser
                viewModel.setUser(newUser)
                //Toast.makeText(requireContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun clearTexts() {
        binding.emailEditTextLayout.text?.clear()
        binding.nameEditTextLayout.text?.clear()
        binding.pswEditTextLayout.text?.clear()

    }

    private fun registerUser() {
        val txtEmail = binding.emailEditTextLayout
        val txtName = binding.nameEditTextLayout
        val txtPassword = binding.pswEditTextLayout
        //val txtRole = binding.roleTxtInputLayout

//        txtEmail.doAfterTextChanged { this.isValidateEmail() }
//        txtName.doAfterTextChanged { this.isValidName() }
//        txtPassword.doAfterTextChanged { this.isValidatePassword()}
//        txtEmail.doAfterTextChanged { this. isValidateEmailPattern()}
//        txtPassword.doAfterTextChanged { this.isValidatePswd() }

        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()
        val name = txtName.text.toString().trim()

        val validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.addValidation(
            requireActivity(),
            R.id.name_editTextLayout,
            RegexTemplate.NOT_EMPTY,
            R.string.name_error_string
        )

        validation.addValidation(
            requireActivity(),
            R.id.email_editTextLayout,
            Patterns.EMAIL_ADDRESS,
            R.string.email_error_string
        )

        validation.addValidation(
            requireActivity(),
            R.id.psw_editTextLayout,
            ".{6,}",
            R.string.invalid_password
        )

        if (!isUserSelected()) return


        if (validation.validate()) {
            viewModel.createUser(name, email, password, user)
            //binding.progressBar.visibility = View.VISIBLE
            binding.signUpButton.isEnabled = false
        }

        viewModel.createUser(name, email, password, user)
        // binding.progressBar.visibility = View.VISIBLE
        binding.signUpButton.isEnabled = false
    }

    private fun isUserSelected(): Boolean {
        return if (user == SELECT_USER) {
            Toast.makeText(requireContext(), "Select User !!", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }

    }

    fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }


}

