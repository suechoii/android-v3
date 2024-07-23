package com.community.mingle.views.ui.member

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.community.mingle.MainActivity
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityLoginBinding
import com.community.mingle.databinding.BottomDialogTermsBinding
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.LoginViewModel
import com.community.mingle.views.ui.settings.ChangePasswordActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.fcmtoken.value = null
        getFcmToken()

        initView()
        initViewModel()
    }

    private fun initView() {
        binding.createAccount.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPw.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
       }
    }

    private fun initViewModel() {

        binding.viewModel = viewModel

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
                hideKeyboard()
            }
        }

        viewModel.alertMsg.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutProgress.root.visibility = View.GONE
                binding.errorTv.visibility = View.VISIBLE
                Log.d("error",it)
            }
        }

        viewModel.loginSuccess.observe(this) { event ->
            event.getContentIfNotHandled()?.let {

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("isLogin",true)
                startActivity(intent)
                finish()

                // 로그인 환영 메시지 출력
                applicationContext.toast("로그인되었습니다.")
            }
        }

        val loginWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.errorTv.visibility = View.INVISIBLE
                if (binding.uniEmail.text.toString() != "" && binding.password.text.toString() != "" ) {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_enabled)
                    binding.nextBtn.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.gray_04))
                }
                else {
                    binding.nextBtn.setBackgroundResource(R.drawable.bg_btn_signup_next_disabled)
                    binding.nextBtn.setTextColor(Color.BLACK)
                }
            }
        }


        binding.uniEmail.addTextChangedListener(loginWatcher)
        binding.password.addTextChangedListener(loginWatcher)

//        viewModel.fcmRefreshSuccess.observe(this) { event ->
//            event.getContentIfNotHandled()?.let {
//                if (it) {
//                    Log.d("new Fcm: ", MingleApplication.pref.fcmToken.toString())
//                }
//            }
//        }
    }

    private fun getFcmToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        "fcmToken",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }
                val token = task.result
                viewModel.fcmtoken.value = token
                MingleApplication.pref.fcmToken = token
                //viewModel.fcmRefresh(token)
                Log.d("fcmToken", token)
            })

    }
}