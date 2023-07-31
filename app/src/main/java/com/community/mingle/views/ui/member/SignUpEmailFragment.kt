package com.community.mingle.views.ui.member

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupEmailBinding
import com.community.mingle.utils.base.BaseSignupFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpEmailFragment :
    BaseSignupFragment<FragmentSignupEmailBinding>(R.layout.fragment_signup_email) {

    override fun initView() {
        // email -> school 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // email -> code 이동
        binding.requestAuthBtn.setOnClickListener {
            signupViewModel.validateEmail()

            signupViewModel.isEmailVerified.observe(viewLifecycleOwner) {
                binding.alreadyExistTv.text = it
                if (it.isNullOrEmpty()) {
                    signupViewModel.sendCode()
                    signupViewModel.sendCodeSuccess.observe(binding.lifecycleOwner!!) {
                        // 이때 로딩 다이어로그 추가하는게 좋을듯
                        findNavController().safeNavigate(R.id.action_signupEmailFragment_to_signupNumberFragment)
                    }
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
        binding.viewModel = signupViewModel

        /* 이메일 파트 */
//        signupViewModel.getDomain(signupViewModel.getUnivId())
//
//        signupViewModel.getDomainSuccess.observe(binding.lifecycleOwner!!) {
//            binding.domainTv.text = signupViewModel.domain
//        }

        signupViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }
        binding.domainTv.text = signupViewModel.domain

        binding.enterIdTv.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {}

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                if (binding.enterIdTv.text.toString().isNotEmpty()) {
                    binding.requestAuthBtn.isClickable = true
                    binding.requestAuthBtn.isEnabled = true
                    binding.requestAuthBtn.isFocusable = true
                    binding.requestAuthBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.requestButtonIv.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.requestButtonTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                else {
                    binding.requestAuthBtn.isClickable = false
                    binding.requestAuthBtn.isEnabled = false
                    binding.requestAuthBtn.isFocusable = false
                    binding.requestAuthBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.requestButtonIv.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_04))
                    binding.requestButtonTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}