package com.community.mingle.views.ui.settings

import android.annotation.SuppressLint
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Chronometer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentChangepwNumberBinding
import com.community.mingle.databinding.FragmentSignupNumberBinding
import com.community.mingle.utils.base.BaseChangepwFragment
import com.community.mingle.utils.base.BaseSignupFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangepwNumberFragment :
    BaseChangepwFragment<FragmentChangepwNumberBinding>(R.layout.fragment_changepw_number), Chronometer.OnChronometerTickListener{
    override fun initView() {
        // 3분 타이머 시작
        binding.chronometer.base = SystemClock.elapsedRealtime() + 180000
        binding.chronometer.start()
        binding.chronometer.onChronometerTickListener = this

        // number -> email 이동
        binding.returnIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // email -> code 이동
        binding.nextBtn.setOnClickListener {

            changepwViewModel.validateCode()

            changepwViewModel.isCodeVerified.observe(viewLifecycleOwner) {
                binding.incorrectMsg.text = it
                if (it.isNullOrEmpty()) {
                    findNavController().safeNavigate(R.id.action_changepwNumberFragment_to_changepwPasswordFragment)
                }
                else {
                    binding.incorrectCross.visibility = View.VISIBLE
                }
            }
        }

        // 인증문자 재발송
        binding.touchsectionRetry.setOnClickListener {
            binding.incorrectCross.visibility = View.GONE
            binding.incorrectMsg.text = null
            changepwViewModel.sendCode()
            changepwViewModel.sendCodeSuccess.observe(binding.lifecycleOwner!!) {
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

    override fun initViewModel() {
        binding.viewModel = changepwViewModel

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
                if (binding.enterCode.text.toString().length == 6) {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.isEnabled = true
                }
                else {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
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