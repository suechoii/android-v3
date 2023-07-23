package com.community.mingle.views.ui.board

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.MainActivity
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.ActivityPost2Binding
import com.community.mingle.databinding.BottomDialogReportBinding
import com.community.mingle.service.models.Comment2
import com.community.mingle.service.models.PostDetail
import com.community.mingle.service.models.Reply
import com.community.mingle.utils.Constants.DELETE_COMMENT
import com.community.mingle.utils.Constants.REPORT_COMMENT
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.DateUtils.formatTo
import com.community.mingle.utils.DateUtils.toDate
import com.community.mingle.utils.DialogUtils.showYesNoDialog
import com.community.mingle.utils.KeyboardUtils
import com.community.mingle.utils.KeyboardUtils.hideKeyboard
import com.community.mingle.utils.KeyboardUtils.requestFocusAndShowKeyboard
import com.community.mingle.utils.RecyclerViewUtils
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.views.adapter.CommentListAdapter
import com.community.mingle.views.adapter.PostImageAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import kotlin.properties.Delegates

@AndroidEntryPoint
class PostActivity : BaseActivity<ActivityPost2Binding>(R.layout.activity_post2) {

    private val viewModel: PostViewModel by viewModels()
    private lateinit var keyboardVisibilityUtils: KeyboardUtils.KeyboardVisibilityUtils
    private lateinit var commentListAdapter: CommentListAdapter
    private lateinit var currentCommentList: Array<Comment2>
    private lateinit var postImageListAdapter: PostImageAdapter
    private var postId by Delegates.notNull<Int>()
    var boardType: String = ""
    var categoryType: String = ""
    private var isBlind: Boolean = false
    private var isReported: Boolean = false
    private var reportText: String? = null
    private var post: PostDetail? = null
    private var postPosition: Int = 0
    private var commentPosition: Int = 0
    private var selectedCommentPosition: Int? = null
    private var replyPosition: Int = 0
    private lateinit var parentComment: Comment2
    private var parentReplyId: Int? = null
    private var commentMentionId: Int = 0
    private lateinit var commentMentionNickname: String
    private var requestUpdate: Boolean? = false
    private var isFirst: Boolean = true
    private var isAnon: Boolean = true
    private var myPost: Boolean = false

    // 게시물 수정 액티비티 요청 및 결과 처리
    private val requestEditPost = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val resultCode = activityResult.resultCode // 결과 코드

        if (resultCode == Activity.RESULT_OK) {
            viewModel.getPost(boardType, postId, false)
            viewModel.getComments(boardType, postId, false)
            requestUpdate = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MingleApplication.pref.isBlind = false

        processIntent()
        initViewModel()
        initView()
        initRV()
    }

    private fun processIntent() {
        postId = intent.getIntExtra("postId", -1000)
        postPosition = intent.getIntExtra("position", 0)
        boardType = intent.getStringExtra(IntentConstants.BoardType)
            ?.let { if (it == "UnivPost") "잔디밭" else if (it == "TotalPost") "광장" else it }
            ?: intent.getStringExtra("type")
                    ?: intent.getStringExtra("board")
                .let {
                    when (it) {
                        "UnivPost" -> "잔디밭"
                        "TotalPost" -> {
                            "광장"
                        }

                        else -> it.toString()
                    }
                }
        categoryType = intent.getStringExtra(IntentConstants.CategoryType) ?: ""
        isBlind = intent.getBooleanExtra("isBlind", false)
        isReported = intent.getBooleanExtra("isReported", false)
        reportText = intent.getStringExtra("reportText")


        if (isBlind) {
            MingleApplication.pref.isBlind = true
            binding.unhiddenLayout.visibility = View.GONE
            binding.hiddenLayout.visibility = View.VISIBLE
            val toolbar2: Toolbar = binding.postDetailToolbar2
            toolbar2.overflowIcon = null
            setSupportActionBar(toolbar2)
            supportActionBar?.apply {
                binding.boardNameTv2.text = boardType
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
                setHomeAsUpIndicator(R.drawable.ic_back)
            }

            binding.cancelBlindTv.setOnClickListener {
                viewModel.unblindPost(boardType, postId)
            }
        } else if (isReported) {
            binding.unhiddenLayout.visibility = View.GONE
            binding.hiddenLayout.visibility = View.VISIBLE
            binding.cancelBlindTv.visibility = View.VISIBLE
            binding.cancelBlindTv.setLinkTextColor(ContextCompat.getColor(this, R.color.gray_04))
            binding.hiddenText.text = reportText
            val toolbar2: Toolbar = binding.postDetailToolbar2
            toolbar2.overflowIcon = null
            setSupportActionBar(toolbar2)
            supportActionBar?.apply {
                binding.boardNameTv2.text = boardType
                binding.categoryNameTv2.text = categoryType
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
                setHomeAsUpIndicator(R.drawable.ic_back)
            }
        } else {
            initToolbar()
        }
    }

    private fun initViewModel() {
        binding.activity = this
        binding.viewModel = viewModel
        // 포스트 & 댓글 요청
        viewModel.getPost(boardType, postId, false)
        viewModel.getComments(boardType, postId, false)
        // 로딩 화면 가시화 여부
        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.post.observe(binding.lifecycleOwner!!) {
            if (it == null) return@observe
            binding.dateTimeTv.text = it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()
            binding.dateTime2Tv.text = it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()
            binding.post = it
            Log.d("date", it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString())

            if (isReported) {
                binding.hiddenText.text = it.title
                binding.cancelBlindTv.text = it.content
            }

            if (it.fileAttached) {
                postImageListAdapter.addPostImageList(it.postImgUrl.toMutableList())
                Log.d("tag_imageLIst", it.postImgUrl.toMutableList().toString())
            }
            Color.parseColor("#FF7663")
            this.post = it
            binding.swipeRefresh.isRefreshing = false

            if (it.myPost) {
                myPost = true
                binding.anonTv.text = it.nickname + " (나)"
                binding.anonTv.setTextColor(ContextCompat.getColor(this, R.color.orange_02))
            } else {
                if (categoryType == "학생회" || categoryType == "밍글소식") {
                    binding.anonSpecialTv.text = it.nickname
                    binding.specialIcon.visibility = View.VISIBLE
                    binding.viewTv.visibility = View.VISIBLE
                    binding.eyeTv.visibility = View.VISIBLE
                    binding.dateTimeTv.visibility = View.VISIBLE
                    binding.ellipseIv.visibility = View.VISIBLE
                    binding.view2Tv.visibility = View.GONE
                    binding.eye2Tv.visibility = View.GONE
                    binding.dateTime2Tv.visibility = View.GONE
                    binding.ellipse2Iv.visibility = View.GONE
                } else
                    binding.anonTv.text = it.nickname
            }

            if (it.liked) {
                binding.btnLikePost.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.orange_02
                    )
                )
            }

            if (it.scraped) {
                binding.btnScrap.setColorFilter(ContextCompat.getColor(this, R.color.orange_02))
            }

            if (it.commentCount.toInt() == 0) {
                binding.emptyFrame.visibility = View.VISIBLE
            }

            if (isFirst) {
                invalidateOptionsMenu()
            }
        }

        binding.btnLikePost.setOnClickListener {
            viewModel.likePost(boardType, postId)
        }
        // 게시글 좋아요 처리
        viewModel.isLikedPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    val likesNum = binding.likeCountTv.text.toString()
                    binding.likeCountTv.text = (likesNum.toInt() + 1).toString()
                    binding.btnLikePost.setColorFilter(ResUtils.getColor(R.color.orange_02))
                    requestUpdate = true
                }
            }
        }
        // 게시글 좋아요 취소 처리
        viewModel.isUnlikePost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    val likesNum = binding.likeCountTv.text.toString()
                    binding.likeCountTv.text = (likesNum.toInt() - 1).toString()
                    binding.btnLikePost.setColorFilter(ResUtils.getColor(R.color.gray_02))
                    requestUpdate = true
                }
            }
        }

        binding.btnScrap.setOnClickListener {
            viewModel.scrapPost(boardType, postId)
        }
        // 게시글 스크랩 처리
        viewModel.isScrapPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.btnScrap.setColorFilter(ResUtils.getColor(R.color.orange_02))
                    requestUpdate = true
                }
            }
        }

        viewModel.isReportedPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    toast("게시글을 신고했습니다.")
                } else {
                    toast("이미 신고한 게시글입니다.")
                }
            }
        }

        viewModel.isReportedComment.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    toast("댓글을 신고했습니다.")
                } else {
                    toast("이미 신고한 댓글입니다.")
                }
            }
        }

        viewModel.isBlindedPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    toast("게시물을 가렸습니다.")
                    binding.unhiddenLayout.visibility = View.GONE
                    binding.hiddenLayout.visibility = View.VISIBLE
                    val toolbar2: Toolbar = binding.postDetailToolbar2
                    toolbar2.overflowIcon = null
                    setSupportActionBar(toolbar2)
                    supportActionBar?.apply {
                        binding.boardNameTv2.text = boardType
                        binding.categoryNameTv2.text = categoryType
                        setDisplayShowTitleEnabled(false)
                        setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
                        setHomeAsUpIndicator(R.drawable.ic_back)
                    }
                    MingleApplication.pref.isBlind = true
                    binding.cancelBlindTv.setOnClickListener {
                        viewModel.unblindPost(boardType, postId)
                    }
                } else {
                    toast("이미 게시물을 가렸습니다.")
                }
            }
        }

        viewModel.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    toast("게시물 가리기를 취소했습니다.")
                    binding.hiddenLayout.visibility = View.GONE
                    binding.unhiddenLayout.visibility = View.VISIBLE
                    MingleApplication.pref.isBlind = false
                    initToolbar()
                }
            }
        }
        // 게시글 스크랩 취소 처리
        viewModel.isDelScrapPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.btnScrap.setColorFilter(ResUtils.getColor(R.color.gray_02))
                    requestUpdate = true
                }
            }
        }
        // 익명 처리
        binding.btnAnonymous.setOnClickListener {
            if (isAnon) {
                isAnon = false
                binding.btnAnonTv.setTextColor(ResUtils.getColor(R.color.gray_03))
                binding.btnAnonTick.setColorFilter(ResUtils.getColor(R.color.gray_03))
            } else {
                isAnon = true
                binding.btnAnonTv.setTextColor(ResUtils.getColor(R.color.orange_02))
                binding.btnAnonTick.setColorFilter(ResUtils.getColor(R.color.orange_02))
            }
        }
        // 댓글 요청 처리
        viewModel.commentList.observe(binding.lifecycleOwner!!) {
            with(commentListAdapter) { addCommentList(it) }
            currentCommentList = it.toTypedArray()

            if (!isFirst)
                calculateCommentNum(it)

            if (it.isNotEmpty()) {
                binding.emptyFrame.visibility = View.GONE
            } else {
                binding.emptyFrame.visibility = View.VISIBLE
            }

            isFirst = false
            binding.swipeRefresh.isRefreshing = false
        }
        // 새로 작성된 댓글 처리
        viewModel.newCommentList.observe(binding.lifecycleOwner!!) {
            with(commentListAdapter) { addCommentList(it.toMutableList()) }
            currentCommentList = it.toTypedArray()

            if (!isFirst)
                calculateCommentNum(it)

            isFirst = false
            binding.swipeRefresh.isRefreshing = false

            if (it.isNotEmpty()) {
                binding.emptyFrame.visibility = View.GONE
            } else {
                binding.emptyFrame.visibility = View.VISIBLE
            }
            // 해당 댓글로 스크롤 이동
            binding.commentRv.post {
                binding.commentRv.smoothScrollToPosition(commentListAdapter.itemCount - 1)
            }
        }
        // 새로 작성된 대댓글 처리
        viewModel.replyList.observe(binding.lifecycleOwner!!) {
            with(commentListAdapter) { addCommentList(it.toMutableList()) }
            currentCommentList = it.toTypedArray()

            if (!isFirst)
                calculateCommentNum(it)

            isFirst = false
            binding.swipeRefresh.isRefreshing = false
            // 해당 댓글로 스크롤 이동
            if (commentPosition >= commentListAdapter.itemCount - 1) {
                binding.commentRv.post {
                    binding.commentRv.smoothScrollToPosition(commentListAdapter.itemCount - 1)
                }
            } else {
                binding.commentRv.post {
                    binding.commentRv.smoothScrollToPosition(commentPosition)
                }
            }
        }
        // 댓글 좋아요 처리
        viewModel.comment.observe(binding.lifecycleOwner!!) {
            currentCommentList[commentPosition] = it
            //            viewModel.update(currentCommentList)
            commentListAdapter.updateItem(it, commentPosition)
        }
        // 대댓글 좋아요 처리
        viewModel.reply.observe(binding.lifecycleOwner!!) {
            //             commentListAdapter.notifyDataSetChanged()
            commentListAdapter.updateItem(parentComment, commentPosition)
        }
        // 대댓글 작성 성공 처리
        viewModel.replySuccessEvent.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                parentReplyId = null
                selectedCommentPosition = null
            }
        }
        // 대댓글 옵션 다이얼로그 처리
        viewModel.showReplyOptionDialog.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                showReplyOptionDialog(it, isMine = false)
            }
        }
        // 내 대댓글 옵션 다이얼로그 처리
        viewModel.showMyReplyOptionDialog.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                showReplyOptionDialog(it, isMine = true)
            }
        }

        binding.sendIv.setOnClickListener {
            if (!binding.writeCommentEt.text.isNullOrBlank()) {
                if (parentReplyId == null) {
                    isFirst = false
                    viewModel.writeComment(boardType, postId, isAnon)
                } else {
                    viewModel.writeReply(boardType, postId, commentMentionId, parentReplyId!!, isAnon)
                }

                binding.writeCommentEt.text = null
                hideKeyboard()
                requestUpdate = true
            }
        }

        viewModel.content.observe(binding.lifecycleOwner!!) {
            if (parentReplyId == null) {
                if (it.trimmedLength() > 0) {
                    binding.commentMsgTv.text = "댓글 쓰는 중..."
                    binding.commentMsgTv.visibility = View.VISIBLE
                    binding.sendIv.setColorFilter(resources.getColor(R.color.orange_02))
                    binding.sendIv.isEnabled = true
                } else {
                    binding.commentMsgTv.visibility = View.GONE
                    binding.sendIv.setColorFilter(resources.getColor(R.color.gray_02))
                    binding.sendIv.isEnabled = false
                }
            } else {
                if (it.trimmedLength() > 0) {
                    binding.commentMsgTv.text = commentMentionNickname + " 에게 대댓글 쓰는 중..."
                    binding.commentMsgTv.visibility = View.VISIBLE
                    binding.sendIv.setColorFilter(resources.getColor(R.color.orange_02))
                    binding.sendIv.isEnabled = true
                } else {
                    binding.commentMsgTv.visibility = View.GONE
                    binding.sendIv.setColorFilter(resources.getColor(R.color.gray_02))
                    binding.sendIv.isEnabled = false
                }
            }
        }

    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.postDetailToolbar
        toolbar.overflowIcon = ResUtils.getDrawable(R.drawable.ic_more)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            binding.boardNameTv.text = boardType
            binding.categoryNameTv.text = categoryType
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initView() {
        binding.swipeRefresh.isRefreshing = false

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPost(boardType, postId, true)
            viewModel.getComments(boardType, postId, true)
            isFirst = true
        }

        keyboardVisibilityUtils = KeyboardUtils.KeyboardVisibilityUtils(window,
            onHideKeyboard = {
                binding.layout.run {
                    //키보드 내려갔을때 원하는 동작
                    //commentListAdapter.notifyDataSetChanged()
                    if (selectedCommentPosition != null) {
                        Log.d("tag_reply", selectedCommentPosition.toString())
                        commentListAdapter.notifyItemChanged(selectedCommentPosition!!)
                        parentReplyId = null
                    }
                }
            }
        )
    }

    private fun initRV() {
        commentListAdapter = CommentListAdapter(this, viewModel)
        postImageListAdapter = PostImageAdapter()
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ResUtils.getDrawable(R.drawable.divider_comment)?.let { divider.setDrawable(it) }

        binding.commentRv.apply {
            adapter = commentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        binding.imageRv.apply {
            adapter = postImageListAdapter
            hasFixedSize()
        }

        commentListAdapter.setOnCommentClickListener(object :
            CommentListAdapter.OnCommentClickListener {
            override fun onClickCommentOption(comment: Comment2) {
                if (comment.myComment) {
                    showCommentOptionDialog(comment, true)
                } else {
                    showCommentOptionDialog(comment, false)
                }
            }

            override fun onLikeComment(position: Int, comment: Comment2) {
                commentPosition = position
                viewModel.likeComment(boardType, comment.commentId, comment)
            }

            override fun onWriteReply(position: Int, parentPosition: Int?, parentCommentId: Int, mentionNickname: String, mentionId: Int) {
                if (selectedCommentPosition != null) {
                    commentListAdapter.notifyItemChanged(selectedCommentPosition!!)
                }

                parentReplyId = parentCommentId
                commentMentionId = mentionId
                if (parentPosition != null) {
                    commentPosition = parentPosition
                } else {
                    commentPosition = position
                }
                //replyPosition = position
                commentMentionNickname = mentionNickname
                selectedCommentPosition = position
                binding.writeCommentEt.isFocusableInTouchMode = true
                binding.writeCommentEt.requestFocusAndShowKeyboard(this@PostActivity)
                //commentPosition, replyPosition 연구해보기
                // 선택한 댓글이 키보드 위로 보이도록
                binding.commentRv.post {
                    binding.commentRv.smoothScrollToPosition(commentPosition)
                }
            }

            override fun onLikeReply(position: Int, parentPosition: Int, reply: Reply, comment: Comment2) {
                commentPosition = parentPosition
                replyPosition = position
                parentComment = comment
                viewModel.likeReply(boardType, reply.commentId, reply)
            }
        })

        postImageListAdapter.setOnPostImageClickListener(object :
            PostImageAdapter.OnPostImageClickListener {
            override fun onClickPostImage(item: URL, position: Int) {
                val intent = Intent(this@PostActivity, ImageSlideActivity::class.java)
                intent.putExtra("type", boardType)
                intent.putExtra("position", position)
                intent.putExtra("postId", postId) //postId 필요하나요?
                startActivity(intent)
            }
        })
    }

    // 댓글 개수 계산
    private fun calculateCommentNum(list: List<Comment2>) {
        // for문을 돌아 대댓글이 있는지 확인 후 replyCount 에 더해주기
        var replyCount = 0

        for (i in list) {
            if (i.coCommentsList != null)
                replyCount += i.coCommentsList.size
        }

        post?.commentCount = (list.size + replyCount).toString()
        binding.commentCountTv.text = post?.commentCount
    }

    // 게시물 삭제 다이얼로그
    private fun showDeletePostDialog(message: String) {
        showYesNoDialog(this, message, onPositiveClick = { dialog, which ->
            viewModel.deletePost(boardType, postId)
            // 우선은 다 홈으로 돌아가는걸로 설정, 나중에 잔디밭이나 광장에서 클릭한 경우라면 어떻게 해야할지도 생각
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        },
            onNegativeClick = { _, _ ->
                //do Nothing
            })
    }

    // 게시물 가리기 다이얼로그
    private fun showBlindPostDialog(message: String) {
        showYesNoDialog(this, message, onPositiveClick = { dialog, which ->
            viewModel.blindPost(boardType, postId)
            // 우선은 다 홈으로 돌아가는걸로 설정, 나중에 잔디밭이나 광장에서 클릭한 경우라면 어떻게 해야할지도 생각
            //            val intent = Intent(applicationContext, MainActivity::class.java)
            //            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //            startActivity(intent)
            //            finish()
        },
            onNegativeClick = { _, _ ->
                //                do nothing
            })
    }

    // 게시물 신고 다이얼로그
    private fun showReportPostDialog() {
        val bottomDialogBinding = BottomDialogReportBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.DialogCustomTheme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(bottomDialogBinding.root)
        dialog.show()

        bottomDialogBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.report_one -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 1)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog.dismiss()
                }

                R.id.report_two -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 2)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_three -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 3)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_four -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 4)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_five -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 5)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_six -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(boardType, postId, 6)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }
            }
            false
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        val focusView = binding.commentBox
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = motionEvent!!.x.toInt()
            val y = motionEvent.y.toInt()
            if (!rect.contains(x, y)) {
                hideKeyboard()
                focusView.clearFocus()
                parentReplyId = null
            }
        }

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_detail_menu, menu)

        menu!!.findItem(R.id.post_detail_change_status).isVisible = false

        if (!myPost) {
            menu!!.findItem(R.id.post_detail_delete).isVisible = false
            menu.findItem(R.id.post_detail_edit).isVisible = false
        } else {
            menu!!.findItem(R.id.post_detail_report).isVisible = false
            menu!!.findItem(R.id.post_detail_blind).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.post_detail_edit -> {
                // 수정 버튼 누를 시
                val intent = Intent(this, PostEditActivity::class.java).apply {
                    putExtra("type", boardType)
                    putExtra("postId", postId)
                    putExtra("title", binding.titleTv.text.toString())
                    putExtra("content", binding.contentTv.text.toString())
                    putExtra(IntentConstants.CategoryType, categoryType)
                }

                requestEditPost.launch(intent)
                true
            }

            R.id.post_detail_delete -> {
                // 삭제 버튼 누를 시
                showDeletePostDialog("게시글을 삭제하시겠습니까?")
                true
            }

            R.id.post_detail_report -> {
                // 신고 버튼 누를 시
                showReportPostDialog()
                true
            }

            R.id.post_detail_blind -> {
                showBlindPostDialog("게시글을 가리겠습니까?")
                true
            }

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        MingleApplication.pref.isUpdate = isBlind != MingleApplication.pref.isBlind
        super.onBackPressed()
    }

    // 댓글 옵션 다이얼로그
    private fun showCommentOptionDialog(comment: Comment2, isMine: Boolean) {
        val cw = ContextThemeWrapper(this, R.style.AlertDialogTheme)
        val builder = AlertDialog.Builder(cw)
        val array = if (isMine) arrayMine else arrayNotMine

        builder.setItems(array) { _, which ->
            val selected = array[which]

            try {
                when (selected) {
                    DELETE_COMMENT -> {
                        showYesNoDialog(this,
                            "댓글을 삭제하시겠습니까?",
                            onPositiveClick = { dialog, which ->
                                viewModel.deleteComment(boardType, comment, postId)
                            },
                            onNegativeClick = { dialog, which ->
                                null
                            })
                        requestUpdate = true
                        true
                    }

                    REPORT_COMMENT -> {
                        showReportCommentDialog(comment.commentId)
                        true
                    }
                }

            } catch (e: IllegalArgumentException) {
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    // 대댓글 옵션 다이얼로그
    private fun showReplyOptionDialog(reply: Reply, isMine: Boolean) {
        val cw = ContextThemeWrapper(this, R.style.AlertDialogTheme)
        val builder = AlertDialog.Builder(cw)
        val array = if (isMine) arrayMine else arrayNotMine

        builder.setItems(array) { _, which ->
            val selected = array[which]

            try {
                when (selected) {
                    DELETE_COMMENT -> {
                        showYesNoDialog(this,
                            "대댓글을 삭제하시겠습니까?",
                            onPositiveClick = { dialog, which ->
                                viewModel.deleteReply(
                                    boardType, reply.commentId, postId
                                )
                            },
                            onNegativeClick = { dialog, which ->
                                null
                            })
                        requestUpdate = true
                        true
                    }

                    REPORT_COMMENT -> {
                        showReportCommentDialog(reply.commentId)
                        true
                    }
                }

            } catch (e: IllegalArgumentException) {
            }
        }

        builder.create().show()
    }

    companion object {

        val arrayMine = arrayOf(
            DELETE_COMMENT
        )
        val arrayNotMine = arrayOf(
            REPORT_COMMENT
        )
    }

    private fun showReportCommentDialog(commentId: Int) {
        val bottomDialogBinding = BottomDialogReportBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.DialogCustomTheme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(bottomDialogBinding.root)
        dialog.show()
        lateinit var reportType: String
        if (boardType == "잔디밭") {
            reportType = "UnivComment"
        } else {
            reportType = "TotalComment"
        }

        bottomDialogBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.report_one -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 1)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog.dismiss()
                }

                R.id.report_two -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 2)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_three -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 3)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_four -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 4)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_five -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 5)
                    },
                        onNegativeClick = { _, _ ->
                        })
                    dialog.dismiss()
                }

                R.id.report_six -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 6)
                    },
                        onNegativeClick = { _, _ ->
                        })
                    dialog.dismiss()
                }
            }
            false
        }
    }
}