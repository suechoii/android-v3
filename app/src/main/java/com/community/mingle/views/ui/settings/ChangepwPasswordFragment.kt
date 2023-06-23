package com.community.mingle.views.ui.settings

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentChangepwPasswordBinding
import com.community.mingle.databinding.FragmentSignupPasswordBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseChangepwFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui.LoadingDialog

class ChangepwPasswordFragment :
    BaseChangepwFragment<FragmentChangepwPasswordBinding>(R.layout.fragment_changepw_password){

    private lateinit var loadingDialog: LoadingDialog

    override fun initView() {
        loadingDialog = LoadingDialog(this@ChangepwPasswordFragment.requireContext())
        // password -> number 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // password -> terms 이동
        binding.nextBtn.setOnClickListener {

            changepwViewModel.resetPwd()

            changepwViewModel.resetSuccess.observe(binding.lifecycleOwner!!) {
                loadingDialog.dismiss()
                findNavController().navigate(R.id.action_changepwPasswordFragment_to_changepwDoneFragment)
            }
        }
    }

    override fun initViewModel() {
        binding.viewModel = changepwViewModel

        changepwViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }

        binding.nextBtn.isEnabled = false

        binding.typePw.setOnFocusChangeListener {_, focused ->
            if(!focused)
            {
                binding.textfieldline1Red.visibility = View.GONE
                binding.textfieldline1Grey.visibility = View.VISIBLE

            } else {
                binding.textfieldline1Red.visibility = View.VISIBLE
                binding.textfieldline1Grey.visibility = View.GONE
            }
        }

        /* PW 파트 */
        changepwViewModel.password.observe(viewLifecycleOwner) {
            changepwViewModel.validatePw()
        }

        changepwViewModel.isPwVerified.observe(viewLifecycleOwner) {
            binding.pwErrorTv.text = it
            if (it.isNullOrEmpty()) {
                binding.pwErrorTv.text = "사용 가능한 비밀번호입니다."
                binding.typePwWrongIv.visibility = View.INVISIBLE
                binding.typePwCorrectIv.visibility = View.VISIBLE
            }
            else {
                binding.typePwWrongIv.visibility = View.VISIBLE
                binding.typePwCorrectIv.visibility = View.INVISIBLE
            }
        }

        binding.typePw.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!binding.reEnter.text.isNullOrEmpty() && !changepwViewModel.comparePw()) {
                    changepwViewModel.confirmPw.value = ""
                    binding.pwErrorTv.text = null
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        /* PW 확인 파트 */
        binding.reEnter.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.textfieldline2Red.visibility = View.VISIBLE
                binding.textfieldline2Grey.visibility = View.GONE

                if (!binding.pwErrorTv.text.isNullOrEmpty()) {
                    binding.sameerrTv.text = "비밀번호를 확인해주세요"
                } else {
                    changepwViewModel.validatePwConfirm()
                }
            }
            else {
                binding.textfieldline2Red.visibility = View.GONE
                binding.textfieldline2Grey.visibility = View.VISIBLE
            }
        }

        changepwViewModel.confirmPw.observe(viewLifecycleOwner) {
            changepwViewModel.validatePwConfirm()
        }

        changepwViewModel.isPwConfirmVerified.observe(viewLifecycleOwner) {
            if (!binding.pwErrorTv.text.isNullOrEmpty()) {
                binding.sameerrTv.text = it
                if (it.isNullOrEmpty()) {
                    binding.sameerrTv.text = "비밀번호가 일치합니다."
                    binding.reEnterPwCorrectIv.visibility = View.VISIBLE
                    binding.reEnterPwWrongIv.visibility = View.INVISIBLE
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.isEnabled = true
                }
                else {
                    binding.reEnterPwCorrectIv.visibility = View.INVISIBLE
                    binding.reEnterPwWrongIv.visibility = View.VISIBLE
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.isEnabled = false
                }
            }
        }

    }


}