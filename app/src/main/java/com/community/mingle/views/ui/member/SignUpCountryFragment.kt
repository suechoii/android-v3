package com.community.mingle.views.ui.member

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSignupCountrySelectionBinding
import com.community.mingle.extension.clicks
import com.community.mingle.extension.throttleFirst
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui_common.ScreenUtil
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpCountryFragment : BaseSignupFragment<FragmentSignupCountrySelectionBinding>(R.layout.fragment_signup_country_selection) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = signupViewModel
        setOnUiStateChangedListener()
        setOnToastMessageListener()
        binding.dropDownLayout.setOnClickListener {
            signupViewModel.toggleCountryDropDownShown()
        }

        binding.typesFilter.setOnClickListener {
            signupViewModel.toggleCountryDropDownShown()
        }
        binding.imageButtonRefreshCountryList
            .clicks()
            .throttleFirst(windowDuration = 2000L)
            .onEach { signupViewModel.refreshCountryList() }
            .launchIn(lifecycleScope)

        binding.imageButtonClose.setOnClickListener { requireActivity().finish() }

        binding.buttonNext.setOnClickListener {
            findNavController().safeNavigate(R.id.action_signupCountryFragment_to_signupSchoolFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.selectedCountry.collect {
                    if(it != null) {
                        binding.buttonNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        binding.buttonNext.isEnabled = true
                    }
                    else {
                        binding.buttonNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_04))
                        binding.buttonNext.isEnabled = false
                    }
                }
            }
        }
        binding.fragmentSignupCountrySelectionContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = ScreenUtil.getStatusBarHeight(requireContext())
        }
    }

    private fun NavController.safeNavigate(id : Int) {
        currentDestination?.getAction(id)?.run {
            navigate(id)
        }
    }

    private fun setOnUiStateChangedListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.selectableCountryList.collect { countries ->
                    val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_school, countries.map { it.name })
                    binding.typesFilter.setAdapter(arrayAdapter)
                    binding.typesFilter.setOnItemClickListener { _, _, position, _ ->
                        signupViewModel.selectCountryByName(arrayAdapter.getItem(position).toString())
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.countryDropDownListShow.collect { dropDownShow ->
                    binding.dropDownLayout.setEndIconDrawable(if (dropDownShow) R.drawable.ic_up else R.drawable.ic_down)
                    binding.dropDownLayout.setBoxBackgroundColorResource(if (dropDownShow) R.color.gray_02 else R.color.white)
                    if (dropDownShow) binding.dropDownLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.refreshCountryListVisible.collect {
                    binding.imageButtonRefreshCountryList.isVisible = it
                }
            }
        }
    }

    private fun setOnToastMessageListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.toastEventFlow.collect { message ->
                    requireContext().toast(message)
                }
            }
        }
    }
}