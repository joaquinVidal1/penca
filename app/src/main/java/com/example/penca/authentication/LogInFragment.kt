package com.example.penca.authentication

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.penca.R
import com.example.penca.databinding.FragmentLogInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding
    private val viewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_log_in, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerText.setOnClickListener {
            this.findNavController()
                .navigate(LogInFragmentDirections.actionLogInFragmentToRegisterFragment())
        }
        val inputEmail = binding.emailInput
        val inputPassword = binding.passwordInput
        binding.logInButton.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.error = getString(R.string.invalid_email)
                inputEmail.requestFocus()
            } else {
                if (inputPassword.text.toString() == "") {
                    inputPassword.error = getString(R.string.must_enter_password)
                    inputPassword.requestFocus()
                } else {
                    lifecycleScope.launch {
                        viewModel.logIn(email, password).await()
                    }
                }
            }
        }

        viewModel.logInResult.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController()
                    .navigate(LogInFragmentDirections.actionLogInFragmentToFragmentMainScreen())
            }
        }

    }
}