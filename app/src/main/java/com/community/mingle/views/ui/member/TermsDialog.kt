package com.community.mingle.views.ui.member

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.activityViewModels
import com.community.mingle.R
import com.community.mingle.viewmodel.SignupViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsDialog() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  = inflater.inflate(R.layout.bottom_dialog_terms,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeBtn : ImageView = view.findViewById(R.id.close_terms_iv)
        val termsTv : TextView = view.findViewById(R.id.terms_detail)
        val termTitle : TextView = view.findViewById(R.id.terms_title)

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val signupViewModel: SignupViewModel by activityViewModels()

        isCancelable = false

        termsTv.text = signupViewModel.terms

        termTitle.text = signupViewModel.termsTitle

        closeBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }
}