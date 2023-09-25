package com.sunnyhapidtest.fragments

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sunnyhapidtest.R
import com.sunnyhapidtest.databinding.FragmentVerificationCodeBinding
import com.sunnyhapidtest.utils.getFirstAndLastTwo
import com.sunnyhapidtest.utils.showSnackBar

class VerificationCodeFragment :
    BaseFragment<FragmentVerificationCodeBinding>(FragmentVerificationCodeBinding::inflate) {
    private var phone = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phone = requireArguments().getString("phone", "")
        binding.tvPhone.text = "+91 $phone"
        val spannableString =
            SpannableString(resources.getString(R.string.don_t_receive_otp_resend))
        val resend = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Handle click action for "Terms & Conditions"
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(), R.color.clickable_text)
                ds.isUnderlineText = false
            }
        }
        // Set the ClickableSpans for the specific parts of the text
        spannableString.setSpan(
            resend,
            resources.getString(R.string.don_t_receive_otp_resend).indexOf("Resend"),
            resources.getString(R.string.don_t_receive_otp_resend)
                .indexOf("Resend") + "Resend".length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvResend.text = spannableString
        binding.tvResend.movementMethod = LinkMovementMethod.getInstance()

        /*Text watcher*/
        binding.etOne.addTextChangedListener(GenericTextWatcher(binding.etOne))
        binding.etTwo.addTextChangedListener(GenericTextWatcher(binding.etTwo))
        binding.etThree.addTextChangedListener(GenericTextWatcher(binding.etThree))
        binding.etFour.addTextChangedListener(GenericTextWatcher(binding.etFour))
        /*Submit click*/
        binding.btnSubmit.setOnClickListener {
            val otp = binding.etOne.text.toString().trim() + binding.etTwo.text.toString()
                .trim() + binding.etThree.text.toString().trim() + binding.etFour.text.toString()
                .trim()
            if (otp.isEmpty())
                binding.root.showSnackBar("Please Enter Valid OTP")
            else if (otp.toInt() != phone.getFirstAndLastTwo()
                    .toInt()
            ) binding.root.showSnackBar("Please Enter Valid OTP")
            else {
                val action = VerificationCodeFragmentDirections.navigateToCreateProfile(phone)
                findNavController().navigate(action)
            }
        }
        binding.rlToolbar.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString().trim()

            when (view.id) {
                R.id.etOne -> if (text.length == 1) binding.etTwo.requestFocus()
                R.id.etTwo -> if (text.length == 1) binding.etThree.requestFocus() else if (text.isEmpty()) binding.etOne.requestFocus()
                R.id.etThree -> if (text.length == 1) binding.etFour.requestFocus() else if (text.isEmpty()) binding.etTwo.requestFocus()
                R.id.etFour -> if (text.length == 1) else if (text.isEmpty()) binding.etThree.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int
        ) {


        }

        override fun onTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int
        ) {

        }

    }
}