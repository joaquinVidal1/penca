package com.example.penca.authentication

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.penca.R
import com.example.penca.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private val args: RegisterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputEmail = binding.emailInput
        val inputPassword = binding.passwordInput
        binding.logInText.setOnClickListener {
            var email: String? = inputEmail.text.toString()
            email = if (email == "") null else email
            var password: String? = inputPassword.text.toString()
            password = if (password == "") null else password
            this.findNavController()
                .navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLogInFragment(
                        email,
                        password
                    )
                )
        }
        inputEmail.setText(args.email)
        inputPassword.setText(args.password)
        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it) {
                binding.contentLoading.show()
            } else {
                binding.contentLoading.hide()
            }
        }
        binding.registerButton.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.error = getString(R.string.invalid_email)
                inputEmail.requestFocus()
            } else {
                if (inputPassword.text.toString().length <8) {
                    inputPassword.error = getString(R.string.must_enter_password)
                    inputPassword.requestFocus()
                } else {
                    viewModel.register(email, password)
                }
            }
        }


        viewModel.registerResult.observe(viewLifecycleOwner) {
            viewModel.resultArrived()
            if (it == null) {
                this.findNavController()
                    .navigate(RegisterFragmentDirections.actionRegisterFragmentToFragmentMainScreen())
            }else{
                if (it.contains("password")) {
                    inputPassword.error = it
                    inputPassword.requestFocus()
                }else{
                    inputEmail.error = it
                    inputEmail.requestFocus()
                }
            }
        }
    }

    override fun onStart() {
        super.onResume()
        binding.emailInput.setText(args.email)
        binding.passwordInput.setText(args.password)
    }

}