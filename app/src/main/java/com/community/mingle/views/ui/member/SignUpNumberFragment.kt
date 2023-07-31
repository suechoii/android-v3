package com.community.mingle.views.ui.member

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupNumberBinding
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui_common.ScreenUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpNumberFragment :
    BaseSignupFragment<FragmentSignupNumberBinding>(R.layout.fragment_signup_number), Chronometer.OnChronometerTickListener{
    override fun initView() {
        // 3분 타이머 시작
        binding.chronometer.base = SystemClock.elapsedRealtime() + 180000
        binding.chronometer.start()
        binding.chronometer.onChronometerTickListener = this

        // number -> email 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
//            findNavController().navigate(R.id.action_signupNumberFragment_to_signupEmailFragment)
        }

        // email -> code 이동
        binding.nextBtn.setOnClickListener {
            signupViewModel.validateCode()
            signupViewModel.isCodeVerified.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    findNavController().safeNavigate(R.id.action_signupNumberFragment_to_signupPasswordFragment)
                }
                else {
                    binding.incorrectMsg.text = it
                    binding.incorrectCross.visibility = View.VISIBLE
                }
            }
        }

        // 인증문자 재발송
        binding.touchsectionRetry.setOnClickListener {
            binding.incorrectCross.visibility = View.GONE
            binding.incorrectMsg.text = null
            signupViewModel.sendCode()
            signupViewModel.sendCodeSuccess.observe(binding.lifecycleOwner!!) {
//                requireContext().toast("인증번호가 재전송되었습니다.")
                binding.resendMsg.visibility = View.VISIBLE
                binding.chronometer.base = SystemClock.elapsedRealtime() + 180000
                binding.chronometer.start()
            }
        }
    }

    fun NavController.safeNavigate(id : Int) {
        currentDestination?.getAction(id)?.run {
            navigate(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentSignupNumberContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = ScreenUtil.getStatusBarHeight(requireContext())
        }
    }

    override fun initViewModel() {
        binding.viewModel = signupViewModel

        binding.enterCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                binding.resendMsg.visibility = View.GONE
                binding.incorrectMsg.text = null
            }

            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                binding.resendMsg.visibility = View.GONE
                binding.incorrectMsg.text = null

                if (binding.enterCode.text.toString().length == 6) {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.nextBtn.isEnabled = true
                }
                else {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
                    binding.nextBtn.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    override fun onChronometerTick(chronometer: Chronometer?) {
        if (binding.chronometer.text.toString() == "00:00") {
            binding.chronometer.stop()
        }
    }
}