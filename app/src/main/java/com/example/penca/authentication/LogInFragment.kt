package com.example.penca.authentication

import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.penca.R
import com.example.penca.databinding.FragmentLogInBinding
import com.example.penca.network.UserNetwork
import com.example.penca.seedetails.SeeDetailsFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding
    private val viewModel: LogInViewModel by viewModels()
    private val args: LogInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_log_in, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.emailInput.setText(args.email)
        binding.passwordInput.setText(args.password)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.alreadyLogged.observe(viewLifecycleOwner) {
            if (it) {
                this.findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToFragmentMainScreen())
            }
        }

        binding.registerText.setOnClickListener {
            val email: String = binding.emailInput.text.toString()
            val password: String = binding.passwordInput.text.toString()
            this.findNavController()
                .navigate(
                    LogInFragmentDirections.actionLogInFragmentToRegisterFragment(
                        email,
                        password
                    )
                )
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
                if (inputPassword.text.toString().length < 8) {
                    inputPassword.error = getString(R.string.must_enter_password)
                    inputPassword.requestFocus()
                } else {
                    viewModel.logIn(email, password)
                }
            }
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it) {
                binding.contentLoading.show()
            } else {
                binding.contentLoading.hide()
            }
        }
        viewModel.logInResult.observe(viewLifecycleOwner) {
            viewModel.resultArrived()
            if (it == null) {
                this.findNavController()
                    .navigate(LogInFragmentDirections.actionLogInFragmentToFragmentMainScreen())
            } else {
                if (it.contains("password")) {
                    inputPassword.error = it
                    inputPassword.requestFocus()
                } else {
                    inputEmail.error = it
                    inputEmail.requestFocus()
                }
            }
        }

    }
}