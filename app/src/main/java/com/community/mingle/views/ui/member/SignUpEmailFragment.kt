package com.community.mingle.views.ui.member

import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
            //requireActivity().supportFragmentManager.popBackStack()
            requireActivity().onBackPressed()
        }

        // email -> code 이동
        binding.nextBtnEnabled.setOnClickListener {
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
                    binding.nextBtnEnabled.visibility = View.VISIBLE
                    binding.nextBtnDisabled.visibility = View.GONE
                }
                else {
                    binding.nextBtnEnabled.visibility = View.GONE
                    binding.nextBtnDisabled.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}