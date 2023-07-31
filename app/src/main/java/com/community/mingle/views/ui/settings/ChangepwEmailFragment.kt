package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.FragmentChangepwEmailBinding
import com.community.mingle.utils.base.BaseChangepwFragment
import com.community.mingle.views.ui_common.ScreenUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangepwEmailFragment :
    BaseChangepwFragment<FragmentChangepwEmailBinding>(R.layout.fragment_changepw_email) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.passwordChangeEmailContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = ScreenUtil.getStatusBarHeight(requireContext())
        }
    }

    override fun initView() {
        // email -> school 이동
        binding.returnIv.setOnClickListener {
            //requireActivity().supportFragmentManager.popBackStack()
            requireActivity().onBackPressed()
        }
        // email -> code 이동
        binding.nextBtnEnabled.setOnClickListener {
            changepwViewModel.validateEmail()

            changepwViewModel.isEmailVerified.observe(viewLifecycleOwner) {
                binding.alreadyExistTv.text = it
                if (it.isNullOrEmpty()) {
                    changepwViewModel.sendCode()
                    changepwViewModel.sendCodeSuccess.observe(binding.lifecycleOwner!!) {
                        // 이때 로딩 다이어로그 추가하는게 좋을듯
                        findNavController().safeNavigate(R.id.action_changepwEmailFragment_to_changepwNumberFragment)
                    }
                }
            }
        }
    }

    fun NavController.safeNavigate(id: Int) {
        currentDestination?.getAction(id)?.run {
            navigate(id)
        }
    }

    override fun initViewModel() {
        binding.viewModel = changepwViewModel

        changepwViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                }
                else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }
        /* 이메일 파트 */
        if (MingleApplication.pref.accessToken == null) {
            changepwViewModel.getDomain(changepwViewModel.getUnivId())
        }
        else {
            changepwViewModel.getMemberDomain()
        }

        changepwViewModel.getDomainSuccess.observe(binding.lifecycleOwner!!) {
            binding.domainTv.text = changepwViewModel.domain
        }

        binding.nextBtnDisabled.isEnabled = false

        binding.enterIdTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int,
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int,
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