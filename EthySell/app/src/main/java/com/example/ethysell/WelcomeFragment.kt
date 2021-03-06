package com.example.ethysell

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.ethysell.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
   private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)

        moveToLogin()
        moveToSignUp()


        return binding.root
    }

    private fun moveToSignUp() {
        binding.LoginBtn.setOnClickListener{
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
    }

    private fun moveToLogin() {
        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }


}