package com.community.mingle.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.community.mingle.MainActivity
import com.community.mingle.MingleApplication
import com.community.mingle.utils.Constants.toast
import com.community.mingle.viewmodel.SplashViewModel
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.member.StartActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        Log.d("extras",extras.toString())
        val hi = extras?.getString("postId")
        val bye = extras?.getString("tableId")
        val bye2 = intent.getStringExtra("tableId")
        Log.d("hi",hi.toString())
        Log.d("bye",bye.toString())
        Log.d("bye2",bye2.toString())

//        val s = intent.getStringExtra("postId")
//        val u = intent.getStringExtra("type")
//        Log.d("s,u",s+u)

        // 토큰이 저장된 자동로그인 상태라면 메인 화면으로 이동
        splashViewModel.autoLogin.observe(this) {
            if (it) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                if (hi != null && bye != null ) {
                    intent.putExtra("postId",hi.toInt())
                    intent.putExtra("type",bye)
                }
                startActivity(intent)
            }
        }

        // 토큰이 없거나 유효하지 않은 상태라면 로그인 화면으로 이동
        splashViewModel.moveLogin.observe(this) {
            if (it) {
                val intent = Intent(this@SplashActivity, StartActivity::class.java)
                startActivity(intent)
            }
        }

        splashViewModel.refreshExpire.observe(this) {
            if (it) {
                MingleApplication.pref.deleteToken() // 저장된 토큰 삭제
                val intent = Intent(this, StartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                toast("로그아웃되었습니다.")
            }
        }
    }
}