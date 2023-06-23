package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.community.mingle.MainActivity
import com.community.mingle.R
import com.community.mingle.databinding.FragmentQuitCheckBinding
import com.community.mingle.databinding.FragmentQuitCompleteBinding
import com.community.mingle.databinding.FragmentQuitUserBinding
import com.community.mingle.databinding.FragmentSignupTermsBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.utils.base.BaseQuitFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.viewmodel.LoginViewModel
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.ui.member.StartActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitCompleteFragment :
    BaseQuitFragment<FragmentQuitCompleteBinding>(R.layout.fragment_quit_complete){

    override fun initView() {
        // done -> start 이동
        binding.nextBtn.setOnClickListener {
            backToStartActivity()
        }
    }

    private fun backToStartActivity() {
        val intent = Intent(mContext, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}