package com.community.mingle.utils.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.viewmodel.SignupViewModel
import com.community.mingle.views.ui.member.SignupActivity
import com.community.mingle.views.ui.settings.QuitActivity

abstract class BaseQuitFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : Fragment() {

    private var mBinding: T? = null
    protected val binding get() = mBinding!!
    protected val quitViewModel: MyPageViewModel by activityViewModels()
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is QuitActivity) {
            this.mContext = context
        } else {
            throw RuntimeException("$context error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        initViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    open fun initView() {}
    open fun initViewModel() {}

}