package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentQuitCheckBinding
import com.community.mingle.databinding.FragmentSignupTermsBinding
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.utils.base.BaseQuitFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.viewmodel.MyPageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitCheckFragment :
    BaseQuitFragment<FragmentQuitCheckBinding>(R.layout.fragment_quit_check){

    override fun initView() {
        // terms -> password 이동
        binding.returnIv.setOnClickListener {
            activity?.finish()
        }

        binding.checkbox.setOnClickListener { onCheckedChanged(binding.checkbox) }

        // terms -> name 이동
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_quitCheckFragment_to_quitUserFragment)
        }
    }

    private fun onCheckedChanged(compoundButton: CompoundButton) {
        if (binding.checkbox.isChecked) {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
            binding.nextBtn.isEnabled = true
        } else {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
            binding.nextBtn.isEnabled = false
        }
    }

}