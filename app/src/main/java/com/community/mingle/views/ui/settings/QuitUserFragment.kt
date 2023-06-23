package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.MainActivity
import com.community.mingle.R
import com.community.mingle.databinding.FragmentQuitCheckBinding
import com.community.mingle.databinding.FragmentQuitUserBinding
import com.community.mingle.databinding.FragmentSignupTermsBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.utils.base.BaseQuitFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.viewmodel.LoginViewModel
import com.community.mingle.viewmodel.MyPageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitUserFragment :
    BaseQuitFragment<FragmentQuitUserBinding>(R.layout.fragment_quit_user){

    override fun initView() {
        //  -> check 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // user -> done 이동
        binding.nextBtn.setOnClickListener {
            quitViewModel.delAccount()

            quitViewModel.isDeleteAccount.observe(viewLifecycleOwner) {
                binding.errorTv.text = it
                binding.layoutProgress.root.visibility = View.GONE
                if (it.isNullOrEmpty()) {
                    findNavController().safeNavigate(R.id.action_quitUserFragment_to_quitCompleteFragment)
                }
                else {
                    binding.errorTv.visibility = View.VISIBLE
                }

            }
        }
    }

    fun NavController.safeNavigate(id : Int) {
        currentDestination?.getAction(id)?.run {
            navigate(id)
        }
    }

    override fun initViewModel() {
        binding.viewModel = quitViewModel

        quitViewModel.loading.observe(binding.lifecycleOwner!!) {
            hideKeyboard()
            binding.layoutProgress.root.visibility = View.VISIBLE
        }

        val loginWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.errorTv.visibility = View.INVISIBLE
                if (binding.uniEmail.text.toString() != "" && binding.password.text.toString() != "" ) {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.isEnabled = true
                }
                else {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.isEnabled = false
                }
            }
        }


        binding.uniEmail.addTextChangedListener(loginWatcher)
        binding.password.addTextChangedListener(loginWatcher)
    }

}