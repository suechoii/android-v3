package com.community.mingle.views.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.community.mingle.R
import com.community.mingle.databinding.FragmentChangepwDoneBinding
import com.community.mingle.databinding.FragmentSignupNameBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.base.BaseChangepwFragment
import com.community.mingle.utils.base.BaseSignupFragment
import com.community.mingle.views.ui.LoadingDialog
import com.community.mingle.views.ui.member.LoginActivity
import com.community.mingle.views.ui.member.StartActivity

class ChangepwDoneFragment :
    BaseChangepwFragment<FragmentChangepwDoneBinding>(R.layout.fragment_changepw_done) {

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun initView() {
        loadingDialog = LoadingDialog(this@ChangepwDoneFragment.requireContext())

        binding.nextBtn.setOnClickListener {
            loadingDialog.dismiss()
            requireActivity().finish()
            requireContext().toast("비밀빈호 재설정이 완료되었습니다.")
            val intent = Intent(mContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

}