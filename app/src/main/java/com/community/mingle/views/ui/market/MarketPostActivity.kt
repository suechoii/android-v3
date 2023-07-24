package com.community.mingle.views.ui.market

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.community.mingle.MainActivity
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivityPostMarketBinding
import com.community.mingle.databinding.BottomDialogChangeStatusBinding
import com.community.mingle.databinding.BottomDialogReportBinding
import com.community.mingle.service.models.*
import com.community.mingle.service.models.market.ItemDetail
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
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.views.adapter.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


@AndroidEntryPoint
class MarketPostActivity : BaseActivity<ActivityPostMarketBinding>(R.layout.activity_post_market) {
    private var isRunning = true

    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var keyboardVisibilityUtils: KeyboardUtils.KeyboardVisibilityUtils

    private lateinit var commentListAdapter: MarketCommentListAdapter
    private lateinit var currentCommentList: Array<Comment2>

    private lateinit var imageList: ArrayList<URL>

    private lateinit var postImageListAdapter: MarketImageVPAdapter

    private var itemId by Delegates.notNull<Int>()
    private var isBlind: Boolean = false
    private var isReported: Boolean = false
    private var reportText: String? = null
    private var item: ItemDetail? = null
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
            viewModel.getMarketPost(itemId, false)
            viewModel.getComments(itemId, false)
            requestUpdate = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MingleApplication.pref.isBlind = false
        imageList = ArrayList<URL>()

        processIntent()
        initViewModel()
        initView()
        initRV()
    }

    private fun processIntent() {
        itemId = intent.getIntExtra("itemId", -1000)

        initToolbar()
    }

    private fun initViewModel() {
        binding.activity = this
        binding.viewModel = viewModel

        // 포스트 & 댓글 요청
        viewModel.getMarketPost(itemId, false)
        viewModel.getComments(itemId, false)

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

        binding.sliderVp.apply {
            postImageListAdapter = MarketImageVPAdapter()
            adapter = postImageListAdapter
            //페이지당 보이는 인디케이터 움직임은 viewpager를 따라간다
            binding.dotsIndicator.attachTo(binding.sliderVp)
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    isRunning = true
                }
            })

            postImageListAdapter.setMyItemClickListener(object :
                MarketImageVPAdapter.MyItemClickListener {
                override fun onItemClick(item: URL, position: Int) {
                    val intent = Intent(this@MarketPostActivity, MarketImageSlideActivity::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("itemId", itemId)
                    startActivity(intent)
                }
            })
        }

        viewModel.post.observe(binding.lifecycleOwner!!) {
            imageList = arrayListOf<URL>()
            imageList.addAll(it.postImgUrl)
            postImageListAdapter.submitlist(imageList)
            postImageListAdapter.status = it.status
            // if (it.status = "예약중") ....
            // if (it.status = "판매완료") ...

            binding.dateTimeTv.text = it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()
            binding.dateTime2Tv.text = it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()
            binding.item = it
            Log.d("date", it.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString())

            if (isReported) {
                binding.hiddenText.text = it.title
                binding.cancelBlindTv.text = it.content
            }

            //            if (it.fileAttached) {// 이게 필요한가?
            //            }
            Color.parseColor("#FF7663")
            this.item = it
            binding.swipeRefresh.isRefreshing = false

            if (it.myPost) {
                myPost = true
                binding.anonTv.text = it.nickname + " (나)"
                binding.anonTv.setTextColor(ContextCompat.getColor(this, R.color.orange_02))
                invalidateOptionsMenu()
            } else {
                if (it.admin) {
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
                binding.btnFavPost.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.orange_02
                    )
                )
            }

            if (it.commentCount.toInt() == 0) {
                binding.emptyFrame.visibility = View.VISIBLE
            }

            if (isFirst) {
                invalidateOptionsMenu()
            }
        }

        binding.btnFavPost.setOnClickListener {
            viewModel.likeMarketPost(itemId)
        }

        // 게시글 좋아요 처리
        viewModel.isLikedPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    val likesNum = binding.likeCountTv.text.toString()
                    binding.likeCountTv.text = (likesNum.toInt() + 1).toString()
                    binding.btnFavPost.setColorFilter(ResUtils.getColor(R.color.orange_02))
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
                    binding.btnFavPost.setColorFilter(ResUtils.getColor(R.color.gray_02))
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
                val y =
                    binding.commentRv.y + binding.commentRv.getChildAt(commentListAdapter.itemCount - 1).y
                binding.commentRv.smoothScrollToPosition((commentListAdapter.itemCount - 1).coerceAtLeast(0))
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
                    binding.commentRv.smoothScrollToPosition((commentListAdapter.itemCount - 1).coerceAtLeast(0))
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

        viewModel.isChangeStatus.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    // 화면 새로고침
                    viewModel.getMarketPost(itemId, true)
                    viewModel.getComments(itemId, true) //get comments 필요하나?
                    isFirst = true
                } else {
                    toast("판매 상태 변경이 되지 않았습니다. 다시 시도해주세요.")
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
                    viewModel.writeComment(itemId, isAnon)
                } else {
                    viewModel.writeReply(itemId, commentMentionId, parentReplyId!!, isAnon)
                }

                binding.writeCommentEt.text = null
                hideKeyboard()
                requestUpdate = true
            }
        }

        viewModel.write_content.observe(binding.lifecycleOwner!!) {
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
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initView() {
        binding.swipeRefresh.isRefreshing = false

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMarketPost(itemId, true)
            viewModel.getComments(itemId, true)
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
        commentListAdapter = MarketCommentListAdapter(this, menuInflater, viewModel)

        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ResUtils.getDrawable(R.drawable.divider_comment)?.let { divider.setDrawable(it) }

        binding.commentRv.apply {
            adapter = commentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(RecyclerViewUtils.DividerItemDecorator(ResUtils.getDrawable(R.drawable.divider_comment)!!))
            hasFixedSize()
            isNestedScrollingEnabled = false
        }

        commentListAdapter.setOnCommentClickListener(object :
            MarketCommentListAdapter.OnCommentClickListener {
            override fun onClickCommentOption(comment: Comment2) {
                if (comment.myComment) {
                    showCommentOptionDialog(comment, true)
                } else {
                    showCommentOptionDialog(comment, false)
                }
            }

            override fun onLikeComment(position: Int, comment: Comment2) {
                //                commentPosition = position
                //                viewModel.likeComment(boardType,comment.commentId, comment)
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
                binding.writeCommentEt.requestFocusAndShowKeyboard(this@MarketPostActivity)


                //commentPosition, replyPosition 연구해보기
                // 선택한 댓글이 키보드 위로 보이도록
                binding.commentRv.post {
                    binding.commentRv.smoothScrollToPosition(commentPosition)
                }
            }

            override fun onLikeReply(position: Int, parentPosition: Int, reply: Reply, comment: Comment2) {
                //                commentPosition = parentPosition
                //                replyPosition = position
                //                parentComment = comment
                //                viewModel.likeReply(boardType, reply.commentId, reply)
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

        item?.commentCount = (list.size + replyCount).toString()
        binding.commentCountTv.text = item?.commentCount
    }

    // 게시물 삭제 다이얼로그
    private fun showDeletePostDialog(message: String) {
        showYesNoDialog(this, message, onPositiveClick = { dialog, which ->
            viewModel.deleteMarketPost(itemId)

            // 우선은 다 홈으로 돌아가는걸로 설정, 나중에 잔디밭이나 광장에서 클릭한 경우라면 어떻게 해야할지도 생각
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        },
            onNegativeClick = { dialog, which ->
                null
            })
    }

    // 게시물 가리기 다이얼로그
    //    private fun showBlindPostDialog(message: String) {
    //        showYesNoDialog(this, message, onPositiveClick = { dialog, which ->
    //            viewModel.blindPost(boardType, postId)
    //
    //            // 우선은 다 홈으로 돌아가는걸로 설정, 나중에 잔디밭이나 광장에서 클릭한 경우라면 어떻게 해야할지도 생각
    ////            val intent = Intent(applicationContext, MainActivity::class.java)
    ////            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    ////            startActivity(intent)
    ////            finish()
    //        },
    //            onNegativeClick = { dialog, which ->
    //                null
    //            })
    //    }

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
                        viewModel.reportPost("Item", itemId, 1)

                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog.dismiss()
                }

                R.id.report_two -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost("Item", itemId, 2)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_three -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost("Item", itemId, 3)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_four -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost("Item", itemId, 4)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_five -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost("Item", itemId, 5)
                    },
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_six -> {
                    showYesNoDialog(this, "게시글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost("Item", itemId, 6)
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

    // 게시물 신고 다이얼로그
    private fun showChangeStatusDialog() {
        val bottomDialogBinding = BottomDialogChangeStatusBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.DialogCustomTheme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(bottomDialogBinding.root)
        dialog.show()

        bottomDialogBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.status_one -> {
                    viewModel.changeStatus(itemId, "SELLING")
                    dialog.dismiss()
                }

                R.id.status_two -> {
                    viewModel.changeStatus(itemId, "RESERVED")
                    dialog.dismiss()
                }

                R.id.status_three -> {
                    viewModel.changeStatus(itemId, "SOLDOUT")
                    dialog.dismiss()
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

        if (!myPost) {
            menu!!.findItem(R.id.post_detail_change_status).isVisible = false
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
                val intent = Intent(this, MarketPostEditActivity::class.java).apply {
                    putExtra("itemId", itemId)
                    putExtra("title", binding.titleTv.text.toString())
                    putExtra("content", binding.contentTv.text.toString())
                    putExtra("price", binding.priceTv.text.toString())
                    putExtra("chatUrl", binding.linkFilledTv.text.toString())
                    putExtra("place", binding.placeFilledTv.text.toString())
                    putExtra("currency", binding.priceHkdTv.text.toString())
                    putExtra("isAnon", binding.anonTv.text == "익명")
                    val urlStrings = imageList.map { it.toString() }
                    putStringArrayListExtra("imageList", urlStrings as ArrayList<String>)
                }

                requestEditPost.launch(intent)
                true
            }

            R.id.post_detail_change_status -> {
                showChangeStatusDialog()
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
                //showBlindPostDialog("게시글을 가리겠습니까?")
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
                                viewModel.deleteComment(comment, itemId)
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
                                    reply.commentId, itemId
                                )
                            },
                            onNegativeClick = { dialog, which ->
                                null
                            })
                        requestUpdate = true
                        true
                    }

                    REPORT_COMMENT -> {
                        //showReportCommentDialog(reply.commentId)
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
        val reportType = "ItemComment"
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
                        onNegativeClick = { dialog, which ->
                            null
                        })
                    dialog?.dismiss()
                }

                R.id.report_six -> {
                    showYesNoDialog(this, "댓글을 신고하시겠습니까?", onPositiveClick = { dialog, which ->
                        viewModel.reportPost(reportType, commentId, 6)
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
}