package com.community.mingle.views.ui.board

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.core.text.trimmedLength
import com.community.mingle.R
import com.community.mingle.databinding.ActivityPostRewriteBinding
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.PostWriteViewModel
import com.community.mingle.views.adapter.PostWriteImageAdapter
import com.community.mingle.views.ui.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates


@AndroidEntryPoint
class PostEditActivity: BaseActivity<ActivityPostRewriteBinding>(R.layout.activity_post_rewrite) {
    private val viewModel: PostWriteViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var boardType: String // 잔디밭 or 광장
    private var postId by Delegates.notNull<Int>()
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var tabName :String

    private var postTitleFilled: Boolean = true
    private var postContentFilled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initView()
        processIntent()
    }

    private fun processIntent() {
        // 이 부분은 이제 어떤 탭에서 넘어오는지에 따라 정해짐
        postId = intent.getIntExtra("postId",-1)
        boardType = intent.getStringExtra("type").toString()
        title = intent.getStringExtra("title").toString()
        content = intent.getStringExtra("content").toString()
        tabName = intent.getStringExtra("tabName").toString()

        Log.d("title",title)
        Log.d("content",content)

        binding.postTitleEditEt.setText(title)
        binding.postContentEditEt.setText(content)

        binding.postTitleEditEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       count: Int, after: Int) {
                // 게시글 제목이 한글자 이상일때
                if(s.toString().trim().isNotEmpty()){
                    postTitleFilled = true
                    // 게시글 본문도 한글자 이상이면 게시 버튼 컬러 #FF5530
                    if (postContentFilled) {
                        binding.postSendTv.setTextColor(Color.parseColor("#FF5530"))
                    }
                    // 게시글 본문이 널값일때 게시 버튼 컬러 #959595
                }else{
                    postTitleFilled = false
                    binding.postSendTv.setTextColor(Color.parseColor("#959595"))
                }
            }
        })

        binding.postContentEditEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       count: Int, after: Int) {
                // 게시글 본문이 한글자 이상일때
                if(s.toString().trim().length > 0 ){
                    postContentFilled = true
                    // 게시글 제목도 한글자 이상이면 게시 버튼 컬러 #FF5530
                    if (postTitleFilled) {
                        binding.postSendTv.setTextColor(Color.parseColor("#FF5530"))
                    }
                    // 게시글 제목이 널값일때 게시 버튼 컬러 #959595
                } else{
                    postContentFilled = false
                    binding.postSendTv.setTextColor(Color.parseColor("#959595"))
                }
            }
        })

    }

    private fun initView() {
        loadingDialog = LoadingDialog(this@PostEditActivity)

        binding.postReturnIv.setOnClickListener {
            // 게시글 임시 저장할지, 삭제할지
            finish()
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        viewModel.loading.observe(binding.lifecycleOwner!!) {
            loadingDialog.show()
        }

        binding.postSendTv.setOnClickListener {
            hideKeyboard()
            viewModel.editPost(boardType,postId,binding.postTitleEditEt.text.toString(),binding.postContentEditEt.text.toString())
        }

        viewModel.successEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(this@PostEditActivity, PostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("postId", it)
                intent.putExtra("type", boardType)
                intent.putExtra("tabName", tabName)
                startActivity(intent)
                loadingDialog.dismiss()

                finish()
            }
        }
    }

}