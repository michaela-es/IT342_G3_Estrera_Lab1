package com.systems.mobileauth.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.systems.mobileauth.R
import com.systems.mobileauth.ui.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvUsername = view.findViewById<TextView>(R.id.tv_username)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvEnabled = view.findViewById<TextView>(R.id.tv_enabled)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

        viewModel.getProfile()

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                tvUsername.text = "Username: ${it.username ?: "N/A"}"
                tvEmail.text = "Email: ${it.email ?: "N/A"}"
                tvEnabled.text = "Account Status: ${if (it.enabled == true) "Active" else "Inactive"}"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
}