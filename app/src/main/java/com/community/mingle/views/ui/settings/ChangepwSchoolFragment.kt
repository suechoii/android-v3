package com.community.mingle.views.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentChangepwSchoolinfoBinding
import com.community.mingle.databinding.FragmentSignupSchoolinfoBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseChangepwFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui_common.ScreenUtil
import com.google.android.material.textfield.TextInputLayout

class ChangepwSchoolFragment :
    BaseChangepwFragment<FragmentChangepwSchoolinfoBinding>(R.layout.fragment_changepw_schoolinfo) {

    private lateinit var callback: OnBackPressedCallback
    private var isDropdown = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.schoolLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = ScreenUtil.getStatusBarHeight(requireContext())
        }
        binding.closeIv.setImageResource(R.drawable.ic_close)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initView() {
        binding.closeIv.setOnClickListener {
            requireActivity().finish()
        }

        // school -> email 이동
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_changepwSchoolFragment_to_changepwEmailFragment)
        }
    }

    override fun initViewModel() {
        binding.viewModel = changepwViewModel

        changepwViewModel.getUnivList()
        changepwViewModel.getUnivListSuccess.observe(binding.lifecycleOwner!!) {
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_school,
                changepwViewModel.univs.keys.toTypedArray()
            )

            Log.d("check",changepwViewModel.univs.toString())

            binding.typesFilter.setAdapter(arrayAdapter)

            binding.typesFilter.setOnItemClickListener { parent, view, position, id ->
                binding.dropDownLayout.setEndIconDrawable(R.drawable.ic_down)
                binding.dropDownLayout.setBoxBackgroundColorResource(R.color.white)
                isDropdown = false
                binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                binding.nextBtn.isEnabled = true
                changepwViewModel.setUnivId(findUniIdx(changepwViewModel.univs,arrayAdapter.getItem(position).toString()))
            }

            if (binding.typesFilter.text.toString() != "학교 선택") {
                binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                binding.nextBtn.isEnabled = true
            }
        }

        binding.dropDownLayout.setOnClickListener {
            showUnivList()
        }

        binding.typesFilter.setOnClickListener {
            showUnivList()
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

