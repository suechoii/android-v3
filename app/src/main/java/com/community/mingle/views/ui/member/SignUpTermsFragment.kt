package com.community.mingle.views.ui.member

import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupTermsBinding
import com.community.mingle.utils.base.BaseSignupFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class SignUpTermsFragment :
    BaseSignupFragment<FragmentSignupTermsBinding>(R.layout.fragment_signup_terms){

    override fun initView() {
        // terms -> password 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // terms -> name 이동
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signupTermsFragment_to_signupNameFragment)
        }
    }

    override fun initViewModel() {
        binding.viewModel = signupViewModel

        binding.checkboxOne.setOnClickListener { onCheckedChanged(binding.checkboxOne) }
        binding.checkboxTwo.setOnClickListener { onCheckedChanged(binding.checkboxTwo) }
        binding.checkboxThree.setOnClickListener { onCheckedChanged(binding.checkboxThree) }
        binding.checkboxFour.setOnClickListener { onCheckedChanged(binding.checkboxFour) }


        val dialog = BottomSheetDialog(mContext, R.style.DialogCustomTheme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.mustUseAgree.setOnClickListener {
            signupViewModel.getTerms(true)
            signupViewModel.getTermsSuccess.observe(binding.lifecycleOwner!!) { event ->
                event.getContentIfNotHandled()?.let {
                    if (it) {
                        val bottomSheetFragment = TermsDialog()
                        activity?.supportFragmentManager?.let { it1 ->
                            bottomSheetFragment.show(
                                it1,
                                bottomSheetFragment.tag
                            )
                        }
                    }
                }
            }
        }


        binding.mustPrivateAgree.setOnClickListener {
            signupViewModel.getTerms(false)
            signupViewModel.getTermsSuccess.observe(binding.lifecycleOwner!!) { event ->
                event.getContentIfNotHandled()?.let {
                    if (it) {
                        val bottomSheetFragment = TermsDialog()
                        activity?.supportFragmentManager?.let { it1 ->
                            bottomSheetFragment.show(
                                it1,
                                bottomSheetFragment.tag
                            )
                        }
                    }
                }
            }
        }

    }

    private fun onCheckedChanged(compoundButton: CompoundButton) { when (compoundButton.id) {
        R.id.checkbox_one -> {
            if (binding.checkboxOne.isChecked) {
                binding.checkboxTwo.isChecked = true
                binding.checkboxThree.isChecked = true
                binding.checkboxFour.isChecked = true
            } else {
                binding.checkboxTwo.isChecked = false
                binding.checkboxThree.isChecked = false
                binding.checkboxFour.isChecked = false
            }
        }

        else -> {
            binding.checkboxOne.isChecked =(
                    binding.checkboxTwo.isChecked
                            && binding.checkboxThree.isChecked
                            && binding.checkboxFour.isChecked)
        }
    }
        if (binding.checkboxOne.isChecked) {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
            binding.nextBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.nextBtn.isEnabled = true
        } else {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
            binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
            binding.nextBtn.isEnabled = false
        }
    }

}