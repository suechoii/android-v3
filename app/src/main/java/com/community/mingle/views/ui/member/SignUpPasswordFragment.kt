package com.community.mingle.views.ui.member

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupPasswordBinding
import com.community.mingle.utils.base.BaseSignupFragment

class SignUpPasswordFragment :
    BaseSignupFragment<FragmentSignupPasswordBinding>(R.layout.fragment_signup_password){

    override fun initView() {
        // password -> number 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
            //findNavController().navigate(R.id.action_signupPasswordFragment_to_signupNumberFragment)
        }

        // password -> terms 이동
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signupPasswordFragment_to_signupTermsFragment)
        }
    }

    override fun initViewModel() {
        binding.viewModel = signupViewModel

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
        signupViewModel.password.observe(viewLifecycleOwner) {
            signupViewModel.validatePw()
        }

        signupViewModel.isPwVerified.observe(viewLifecycleOwner) {
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
                if (!binding.reEnter.text.isNullOrEmpty() && !signupViewModel.comparePw()) {
                    signupViewModel.confirmPw.value = ""
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
                    signupViewModel.validatePwConfirm()
                }
            }
            else {
                binding.textfieldline2Red.visibility = View.GONE
                binding.textfieldline2Grey.visibility = View.VISIBLE
            }
        }

        signupViewModel.confirmPw.observe(viewLifecycleOwner) {
            signupViewModel.validatePwConfirm()
        }

        signupViewModel.isPwConfirmVerified.observe(viewLifecycleOwner) {
            if (!binding.pwErrorTv.text.isNullOrEmpty()) {
                binding.sameerrTv.text = it
                if (it.isNullOrEmpty()) {
                    binding.sameerrTv.text = "비밀번호가 일치합니다."
                    binding.reEnterPwCorrectIv.visibility = View.VISIBLE
                    binding.reEnterPwWrongIv.visibility = View.INVISIBLE
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.nextBtn.isEnabled = true
                }
                else {
                    binding.reEnterPwCorrectIv.visibility = View.INVISIBLE
                    binding.reEnterPwWrongIv.visibility = View.VISIBLE
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
                    binding.nextBtn.isEnabled = false
                }
            }
        }

    }


}