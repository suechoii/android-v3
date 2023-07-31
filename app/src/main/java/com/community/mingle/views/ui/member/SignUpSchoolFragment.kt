package com.community.mingle.views.ui.member

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupSchoolinfoBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui_common.ScreenUtil
import com.google.android.material.textfield.TextInputLayout

class SignUpSchoolFragment :
    BaseSignupFragment<FragmentSignupSchoolinfoBinding>(R.layout.fragment_signup_schoolinfo) {

    private lateinit var callback: OnBackPressedCallback
    private var isDropdown = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onResume() {
        super.onResume()
        setButtonEnabled()
    }

    override fun initView() {
        binding.closeIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // school -> email 이동
        binding.nextBtn.setOnClickListener {
            signupViewModel.getDomain(signupViewModel.getUnivId())
            signupViewModel.getDomainSuccess.observe(binding.lifecycleOwner!!) {
                if (it) {
                    Log.d("true","yes")
                    findNavController().safeNavigate(R.id.action_signupSchoolFragment_to_signupEmailFragment)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.schoolLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = ScreenUtil.getStatusBarHeight(requireContext())
        }
    }
    private fun NavController.safeNavigate(id : Int) {
        currentDestination?.getAction(id)?.run {
            navigate(id)
        }
    }

    override fun initViewModel() {
        binding.viewModel = signupViewModel

        signupViewModel.getUnivList()
        signupViewModel.getUnivListSuccess.observe(binding.lifecycleOwner!!) {
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_school,
                signupViewModel.univs.keys.toTypedArray()
            )

            Log.d("check",signupViewModel.univs.toString())

            binding.typesFilter.setAdapter(arrayAdapter)

            binding.typesFilter.setOnItemClickListener { parent, view, position, id ->
                binding.dropDownLayout.setEndIconDrawable(R.drawable.ic_down)
                binding.dropDownLayout.setBoxBackgroundColorResource(R.color.white)
                isDropdown = false
                binding.nextBtn.isEnabled = true
                binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                signupViewModel.setUnivId(findUniIdx(signupViewModel.univs,arrayAdapter.getItem(position).toString()))

            }

            setButtonEnabled()
        }

        binding.dropDownLayout.setOnClickListener {
            showUnivList()
        }

        binding.typesFilter.setOnClickListener {
            showUnivList()
        }
    }

    private fun setButtonEnabled() {
        if (binding.typesFilter.text.toString() != "학교 선택") {
            binding.nextBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
            binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
            binding.nextBtn.isEnabled = true
        }
    }

    private fun findUniIdx(map: MutableMap<String,Int>, name: String): Int {
        for (i in map) {
            if (i.key == name) {
                return i.value
            }
        }
        return -1000
    }

    private fun showUnivList() {
        binding.dropDownLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
        isDropdown = if (!isDropdown) {
            binding.dropDownLayout.setEndIconDrawable(R.drawable.ic_up)
            binding.dropDownLayout.setBoxBackgroundColorResource(R.color.gray_02)
            true
        } else {
            binding.dropDownLayout.setEndIconDrawable(R.drawable.ic_down)
            binding.dropDownLayout.setBoxBackgroundColorResource(R.color.white)
            false
        }
    }

}

