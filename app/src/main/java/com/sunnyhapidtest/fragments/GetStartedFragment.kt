package com.sunnyhapidtest.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sunnyhapidtest.R
import com.sunnyhapidtest.databinding.FragmentGetStartedBinding

class GetStartedFragment:  BaseFragment<FragmentGetStartedBinding>(FragmentGetStartedBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGetStarted.setOnClickListener {
                findNavController().navigate(R.id.navigate_to_login)
        }
    }
}