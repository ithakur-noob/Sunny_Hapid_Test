package com.sunnyhapidtest.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sunnyhapidtest.R
import com.sunnyhapidtest.databinding.FragmentLoginBinding
import com.sunnyhapidtest.databinding.ItemOtpDialogBinding
import com.sunnyhapidtest.utils.getFirstAndLastTwo
import com.sunnyhapidtest.utils.showSnackBar

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private lateinit var alertDialog: androidx.appcompat.app.AlertDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannableString = SpannableString(resources.getString(R.string.terms))

        val termsConditionsClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Handle click action for "Terms & Conditions"
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(), R.color.clickable_text)
                ds.isUnderlineText = false
            }
        }

        // Create a ClickableSpan for "Privacy Policy"
        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Handle click action for "Privacy Policy"
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(), R.color.clickable_text)
                ds.isUnderlineText = false
            }
        }
        // Set the ClickableSpans for the specific parts of the text
        spannableString.setSpan(
            termsConditionsClickableSpan,
            resources.getString(R.string.terms).indexOf("Terms & Conditions"),
            resources.getString(R.string.terms)
                .indexOf("Terms & Conditions") + "Terms & Conditions".length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            resources.getString(R.string.terms).indexOf("Privacy Policy"),
            resources.getString(R.string.terms).indexOf("Privacy Policy") + "Privacy Policy".length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTermConditions.text = spannableString
        binding.tvTermConditions.movementMethod = LinkMovementMethod.getInstance()
        /*All Clicks*/
        /*Show otp pop up for 2 sec and navigate to verification fragment with phone as arg*/
        binding.btnRequestOtp.setOnClickListener {
            if (binding.etPhone.text.toString().trim().isEmpty() || binding.etPhone.text.toString()
                    .trim().length < 10
            )
                binding.root.showSnackBar("Please Enter Valid Phone Number")
            else {
                showOtpPopUp(binding.etPhone.text.toString().getFirstAndLastTwo())
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                    val navigate =
                        LoginFragmentDirections.navigateToVerificationCode(binding.etPhone.text.toString())
                    findNavController().navigate(navigate)
                }, 2000)
            }
        }
        binding.rlToolbar.ivBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showOtpPopUp(otp: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        val dialogView = ItemOtpDialogBinding.inflate(layoutInflater)
        builder.setView(dialogView.root)
        dialogView.tvOtp.text = otp
        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

}