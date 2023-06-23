package com.community.mingle.views.ui.member

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupNameBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui.LoadingDialog

class SignUpNameFragment :
    BaseSignupFragment<FragmentSignupNameBinding>(R.layout.fragment_signup_name) {

    private lateinit var loadingDialog: LoadingDialog

    override fun initView() {
        loadingDialog = LoadingDialog(this@SignUpNameFragment.requireContext())

        // name -> terms 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun initViewModel() {
        binding.viewModel = signupViewModel

        signupViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }

        // name -> finish 이동
        binding.nextBtn.setOnClickListener {

            signupViewModel.signup()

            signupViewModel.isNicknameError.observe(viewLifecycleOwner) {
                loadingDialog.dismiss()
                if (it.isNullOrEmpty()) {
                    binding.existIdTv.text = null
                } else {
                    binding.existIdTv.text = it
                }
            }

            signupViewModel.signupSuccess.observe(binding.lifecycleOwner!!) {
                loadingDialog.dismiss()
                requireActivity().finish()
                requireContext().toast("가입이 완료되었습니다.")
            }
        }

        binding.nameEdit.addTextChangedListener(object : TextWatcher {

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
                var count: Int = binding.nameEdit.text.length
                binding.countTv.text = count.toString() + "/10"
                if (binding.nameEdit.text.isNotEmpty()) {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.isEnabled = true
                } else {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }


}