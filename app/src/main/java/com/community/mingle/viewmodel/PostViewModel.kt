package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.HotPost
import com.community.mingle.model.post.PostType
import com.community.mingle.model.post.TotalBoardType
import com.community.mingle.model.post.UnivBoardType
import com.community.mingle.service.models.*
import com.community.mingle.service.repository.PostRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class PostViewModel
@Inject
constructor(
    private val repository: PostRepository,
) : ViewModel() {

    val content = MutableLiveData("")
    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading
    private val _post = MutableLiveData<PostDetail>()
    val post: LiveData<PostDetail> get() = _post
    private val _newPost = MutableLiveData<PostDetail>()
    val newPost: LiveData<PostDetail> get() = _newPost

    /*이 녀석들은 댓글 / 대댓글 작성 후 getComments해서 새로운 리스트를 불러올 필요 있음*/
    private val _commentList = MutableLiveData<List<Comment2>>()
    val commentList: LiveData<List<Comment2>> get() = _commentList
    private val _newCommentList = MutableLiveData<List<Comment2>>()
    val newCommentList: LiveData<List<Comment2>> get() = _newCommentList
    private val _replyList = MutableLiveData<List<Comment2>>()
    val replyList: LiveData<List<Comment2>> get() = _replyList
    private val _comment = MutableLiveData<Comment2>()
    val comment: LiveData<Comment2> get() = _comment
    private val _reply = MutableLiveData<Reply>()
    val reply: LiveData<Reply> get() = _reply
    private val _imageList = MutableLiveData<List<URL>>()
    val imageList: LiveData<List<URL>> get() = _imageList

    // 게시글 좋아요 완료 여부
    private val _isLikedPost = MutableLiveData<Event<Boolean>>()
    val isLikedPost: LiveData<Event<Boolean>> = _isLikedPost

    // 게시글 좋아요 취소 완료 여부
    private val _isUnlikePost = MutableLiveData<Event<Boolean>>()
    val isUnlikePost: LiveData<Event<Boolean>> = _isUnlikePost

    // 게시글 스크랩 완료 여부
    private val _isScrapPost = MutableLiveData<Event<Boolean>>()
    val isScrapPost: LiveData<Event<Boolean>> = _isScrapPost

    // 게시글 스크랩 취소 완료 여부
    private val _isDelScrapPost = MutableLiveData<Event<Boolean>>()
    val isDelScrapPost: LiveData<Event<Boolean>> = _isDelScrapPost

    // 게시글 신고 완료 여부
    private val _isReportedPost = MutableLiveData<Event<Boolean>>()
    val isReportedPost: LiveData<Event<Boolean>> = _isReportedPost

    // 댓글 신고 완료 여부
    private val _isReportedComment = MutableLiveData<Event<Boolean>>()
    val isReportedComment: LiveData<Event<Boolean>> = _isReportedComment

    // 게시글 가리기 완료 여부
    private val _isBlindedPost = MutableLiveData<Event<Boolean>>()
    val isBlindedPost: LiveData<Event<Boolean>> = _isBlindedPost

    // 게시글 가리기 취소 완료 여부
    private val _isUnblindPost = MutableLiveData<Event<Boolean>>()
    val isUnblindPost: LiveData<Event<Boolean>> = _isUnblindPost

    // 대댓글 작성 이벤트
    private val _commentSuccessEvent = MutableLiveData<Event<Boolean>>()
    val commentSuccessEvent: LiveData<Event<Boolean>> = _commentSuccessEvent

    // 대댓글 작성 이벤트
    private val _replySuccessEvent = MutableLiveData<Event<Boolean>>()
    val replySuccessEvent: LiveData<Event<Boolean>> = _replySuccessEvent

    // 댓글 좋아요 완료 여부
    private val _isLikedComment = MutableLiveData<Event<Boolean>>()
    val isLikedComment: LiveData<Event<Boolean>> = _isLikedComment
    private val _showReplyOptionDialog = MutableLiveData<Event<Reply>>()
    val showReplyOptionDialog: LiveData<Event<Reply>> = _showReplyOptionDialog
    private val _showMyReplyOptionDialog = MutableLiveData<Event<Reply>>()
    val showMyReplyOptionDialog: LiveData<Event<Reply>> = _showMyReplyOptionDialog

    fun getPost(boardType: String, postId: Int, isRefreshing: Boolean) {
        if (!isRefreshing)
            _loading.postValue(Event(false))
        // need to check whether it's univ or total post
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.getUnivPostDetail(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _post.postValue(response.body()!!.result)
                            _imageList.postValue(response.body()!!.result.postImgUrl)
                            _loading.postValue(Event(false))
                            Log.d("tag_success", response.body().toString())
                        } else {
                            Log.d("tag_fail", "getPostDetail Error: ${response.code()}")
                        }
                    }
            } else {
                repository.getTotalPostDetail(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _post.postValue(response.body()!!.result)
                            _imageList.postValue(response.body()!!.result.postImgUrl)
                            _loading.postValue(Event(false))
                            Log.d("tag_success", response.body().toString())
                        } else {
                            Log.d("tag_fail", "getPostDetail Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun deletePost(boardType: String, postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.deleteUnivPost(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            Log.d("tag_success", response.body().toString())
                        } else {
                            Log.d("tag_fail", "deletePost Error: ${response.code()}")
                        }
                    }
            } else {
                repository.deleteTotalPost(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            Log.d("tag_success", response.body().toString())
                        } else {
                            Log.d("tag_fail", "deletePost Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun likePost(boardType: String, postIdx: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.likeUnivPost(postIdx)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            _isLikedPost.postValue(Event(true))
                            Log.d("tag_success", "likePost: ${response.body()}")
                        } else if (response.body()!!.code == DUP_LIKE) {
                            _isLikedPost.postValue(Event(false))
                            unlikePost(boardType, postIdx)
                            Log.d("tag_fail", "likePost Error: ${response.code()}")
                        } else {
                            Log.d("tag_fail", "likePost Error: ${response.code()}")
                        }
                    }
            } else {
                repository.likeTotalPost(postIdx)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            _isLikedPost.postValue(Event(true))
                            Log.d("tag_success", "likePost: ${response.body()}")
                        } else if (response.body()!!.code == DUP_LIKE) {
                            _isLikedPost.postValue(Event(false))
                            unlikePost(boardType, postIdx)
                            Log.d("tag_fail", "likePost Error: ${response.code()}")
                        } else {
                            Log.d("tag_fail", "likePost Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun unlikePost(boardType: String, postIdx: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.unlikeUnivPost(postIdx)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            _isUnlikePost.postValue(Event(true))
                            Log.d("tag_success", "unlikePost: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikePost Error: ${response.code()}")
                        }
                    }
            } else {
                repository.unlikeTotalPost(postIdx)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            _isUnlikePost.postValue(Event(true))
                            Log.d("tag_success", "unlikePost: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikePost Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun scrapPost(boardType: String, postId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.scrapUniv(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            when (response.body()!!.code) {
                                OK -> {
                                    _loading.postValue(Event(false))
                                    _isScrapPost.postValue(Event(true))
                                    Log.d("tag_success", "bookmarkPost: ${response.body()}")
                                }

                                DUP_SCRAP -> {
                                    _isScrapPost.postValue(Event(false))
                                    delScrap(boardType, postId)
                                }

                                else -> {
                                    Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                                }
                            }
                        } else {
                            Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                        }
                    }
            } else {
                repository.scrapTotal(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            when (response.body()!!.code) {
                                OK -> {
                                    _loading.postValue(Event(false))
                                    _isScrapPost.postValue(Event(true))
                                    Log.d("tag_success", "bookmarkPost: ${response.body()}")
                                }

                                DUP_SCRAP -> {
                                    _isScrapPost.postValue(Event(false))
                                    delScrap(boardType, postId)
                                }

                                else -> {
                                    Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                                }
                            }
                        } else {
                            Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun delScrap(boardType: String, postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.delscrapUniv(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            when (response.body()!!.code) {
                                OK -> {
                                    _loading.postValue(Event(false))
                                    _isDelScrapPost.postValue(Event(true))
                                    Log.d("tag_success", "bookmarkPost: ${response.body()}")
                                }

                                else -> {
                                    Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                                }
                            }
                        } else {
                            Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                        }
                    }
            } else {
                repository.delscrapTotal(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            when (response.body()!!.code) {
                                OK -> {
                                    _loading.postValue(Event(false))
                                    _isDelScrapPost.postValue(Event(true))
                                    Log.d("tag_success", "bookmarkPost: ${response.body()}")
                                }

                                else -> {
                                    Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                                }
                            }
                        } else {
                            Log.d("tag_fail", "bookmarkPost Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun reportPost(boardType: String, postId: Int, reportTypeId: Int) {
        lateinit var tableType: String
        if (boardType == "잔디밭")
            tableType = "UnivPost"
        else if (boardType == "광장")
            tableType = "TotalPost"
        else tableType = boardType

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.createReport(ReportPost(tableType, postId, reportTypeId))
                .let { response ->
                    if (response.isSuccessful) {
                        _loading.postValue(Event(false))

                        when (response.body()!!.code) {
                            OK -> {
                                if (tableType == "UnivPost" || tableType == "TotalPost")
                                    _isReportedPost.postValue(Event(true))
                                else
                                    _isReportedComment.postValue(Event(true))
                                Log.d("tag_success", "reportPost: ${response.body()}")
                            }

                            DUP_REPORT -> {
                                if (tableType == "UnivPost" || tableType == "TotalPost")
                                    _isReportedPost.postValue(Event(false))
                                else
                                    _isReportedComment.postValue(Event(false))
                            }
                        }
                    } else {
                        Log.d("tag_fail", "reportPost Error: ${response.code()}")
                    }
                }
        }
    }

    fun blindPost(boardType: String, postId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            var req = repository.blindUnivPost(postId)
            if (boardType == "광장")
                req = repository.blindTotalPost(postId)

            req.let { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))

                    when (response.body()!!.code) {
                        OK -> {
                            _isBlindedPost.postValue(Event(true))
                            Log.d("tag_success", "blindPost: ${response.body()}")
                        }
                    }
                } else {
                    if (response.code() == 500) {
                        _isBlindedPost.postValue(Event(false))
                    }
                    Log.d("tag_fail", "blindPost Error: ${response.code()}")
                }
            }
        }
    }

    fun unblindPost(boardType: String, postId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            var req = repository.unblindUnivPost(postId)
            if (boardType == "광장")
                req = repository.unblindTotalPost(postId)

            req.let { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))

                    when (response.body()!!.code) {
                        OK -> {
                            _isUnblindPost.postValue(Event(true))
                            Log.d("tag_success", "unblindPost: ${response.body()}")
                        }
                    }
                } else {
                    Log.d("tag_fail", "unblindPost Error: ${response.code()}")
                }
            }
        }
    }

    fun getComments(boardType: String, postId: Int, isRefreshing: Boolean) {
        _loading.postValue(Event(true))
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.getUnivComment(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            if (!isRefreshing) {
                                _loading.postValue(Event(false))
                            }

                            Log.d("tag_success", "getComments: ${response.body()}")

                            if (response.body()!!.code == 1000) {
                                _loading.postValue(Event(false))
                                _commentList.postValue(response.body()!!.result)
                            } else {
                            }
                        } else {
                            Log.d("tag_fail", "getComments Error: ${response.code()}")
                        }
                    }
            } else {
                repository.getTotalComment(postId)
                    .let { response ->
                        if (response.isSuccessful) {
                            if (!isRefreshing)
                                _loading.postValue(Event(false))

                            Log.d("tag_success", "getComments: ${response.body()}")

                            if (response.body()!!.code == 1000) {
                                _commentList.postValue(response.body()!!.result)
                            } else {
                            }
                        } else {
                            Log.d("tag_fail", "getComments Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun writeComment(boardType: String, postId: Int, isAnonymous: Boolean) {
        //if (!check()) return
        val comment = CommentSend(
            isAnonymous = isAnonymous,
            content = content.value!!,
            postId = postId
        )

        Log.d("tag_comment", comment.toString())

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.postUnivComment(comment).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        var result: List<Comment2>
                        runBlocking {
                            result = repository.getUnivComment(postId).body()!!.result
                        }
                        _newCommentList.postValue(result)
                        Log.d("tag_success", "writeComment: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writeComment Error: $response")
                    }
                }
            } else {
                repository.postTotalComment(comment).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        var result: List<Comment2>
                        runBlocking {
                            result = repository.getTotalComment(postId).body()!!.result
                        }
                        _newCommentList.postValue(result)
                        Log.d("tag_success", "writeComment: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writeComment Error: $response")
                    }
                }
            }
        }
    }

    fun likeComment(boardType: String, commentId: Int, comment: Comment2) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.Main) {
            if (boardType == "잔디밭") {
                repository.commentLikeUniv(commentId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            Log.d("tag_success", "likeComment: ${response.body()}")

                            if (response.body()!!.code == 1000) {
                                comment.likeCount = ((comment.likeCount.toInt()) + 1).toString()
                                comment.liked = true
                                _comment.postValue(comment)
                                _isLikedComment.postValue(Event(true))
                            } else if (response.body()?.code == DUP_LIKE) {
                                unlikeComment(boardType, commentId, comment)
                            } else {
                            }
                        } else {
                            Log.d("tag_fail", "likeComment Error: ${response.code()}")
                        }
                    }
            } else {
                repository.commentLikeTotal(commentId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            Log.d("tag_success", "likeComment: ${response.body()}")

                            if (response.body()!!.code == 1000) {
                                comment.likeCount = ((comment.likeCount.toInt()) + 1).toString()
                                comment.liked = true
                                _comment.postValue(comment)
                                _isLikedComment.postValue(Event(true))
                            } else if (response.body()?.code == DUP_LIKE) {
                                unlikeComment(boardType, commentId, comment)
                            } else {
                            }
                        } else {
                            Log.d("tag_fail", "likeComment Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun unlikeComment(boardType: String, commentId: Int, comment: Comment2) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.commentUnlikeUniv(commentId)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            comment.likeCount = ((comment.likeCount.toInt()) - 1).toString()
                            comment.liked = false
                            _comment.postValue(comment)
                            _isLikedComment.postValue(Event(false))
                            Log.d("tag_success", "unlikeComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikeComment Error: ${response.code()}")
                        }
                    }
            } else {
                repository.commentUnlikeTotal(commentId)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            comment.likeCount = ((comment.likeCount.toInt()) - 1).toString()
                            comment.liked = false
                            _comment.postValue(comment)
                            _isLikedComment.postValue(Event(false))
                            Log.d("tag_success", "unlikeComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikeComment Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun deleteComment(boardType: String, comment: Comment2, postId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.deleteUnivComment(comment.commentId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            if (response.body()!!.code == 1000) {
                                //                                if (comment.coCommentsList != null) {
                                //                                    comment.content = "삭제된 댓글입니다"
                                //                                    comment.nickname = "(비공개됨)"
                                //                                    comment.commentDeleted = true
                                //                                    _comment.postValue(comment)
                                //                                }
                                //                                else {
                                //                                    getComments(boardType,postId,false)
                                //                                }
                                getComments(boardType, postId, false)
                            }
                            Log.d("tag_success", "deleteComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "deleteComment Error: ${response.code()}")
                        }
                    }
            } else {
                repository.deleteTotalComment(comment.commentId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            if (response.body()!!.code == 1000) {
                                getComments(boardType, postId, false)
                            }
                            Log.d("tag_success", "deleteComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "deleteComment Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun writeReply(boardType: String, postId: Int, mentionId: Int, parentReplyId: Int, isAnonymous: Boolean) {
        val reply = ReplySend(
            isAnonymous = isAnonymous,
            content = content.value!!,
            mentionId = mentionId,
            parentCommentId = parentReplyId,
            postId = postId
        )

        Log.d("tag_comment", comment.toString())

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.postUnivReply(reply).let { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000) {
                            var result: List<Comment2>
                            runBlocking {
                                result = repository.getUnivComment(postId).body()!!.result
                            }
                            // if this doesn't work, it must be
                            //result = repository.getUnivComment(postId).body()!!.result[position].coCommentsList
                            _replyList.postValue(result)
                            _replySuccessEvent.postValue(Event(true))
                        }
                        Log.d("tag_success", "writeReply: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writeReply Error: $response")
                    }
                }
            } else {
                repository.postTotalReply(reply).let { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000) {
                            var result: List<Comment2>
                            runBlocking {
                                result = repository.getTotalComment(postId).body()!!.result
                            }
                            // if this doesn't work, it must be
                            //result = repository.getUnivComment(postId).body()!!.result[position].coCommentsList
                            _replyList.postValue(result)
                            _replySuccessEvent.postValue(Event(true))
                        }
                        Log.d("tag_success", "writeReply: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writeReply Error: $response")
                    }
                }
            }
        }
    }

    fun likeReply(boardType: String, replyId: Int, reply: Reply) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.Main) {
            if (boardType == "잔디밭") {
                repository.commentLikeUniv(replyId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            Log.d("tag_success", "likeReply: ${response.body()}")
                            if (response.body()!!.code == 1000) {
                                reply.likeCount = (reply.likeCount.toInt() + 1).toString()
                                reply.liked = true
                                _reply.postValue(reply)
                                _isLikedComment.postValue(Event(true))
                            } else if (response.body()?.code == DUP_LIKE)
                                unlikeReply(boardType, replyId, reply)
                            else {
                            }
                        } else {
                            Log.d("tag_fail", "likeReply Error: ${response.code()}")
                        }
                    }

            } else {
                repository.commentLikeTotal(replyId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            Log.d("tag_success", "likeReply: ${response.body()}")
                            if (response.body()!!.code == 1000) {
                                reply.likeCount = (reply.likeCount.toInt() + 1).toString()
                                _reply.postValue(reply)
                                _isLikedComment.postValue(Event(true))
                            } else {
                                _isLikedComment.postValue(Event(false))
                            }
                        } else {
                            Log.d("tag_fail", "likeReply Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun unlikeReply(boardType: String, replyId: Int, reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.commentUnlikeUniv(replyId)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            reply.likeCount = ((reply.likeCount.toInt()) - 1).toString()
                            reply.liked = false
                            _reply.postValue(reply)
                            _isLikedComment.postValue(Event(false))
                            Log.d("tag_success", "unlikeComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikeComment Error: ${response.code()}")
                        }
                    }
            } else {
                repository.commentUnlikeTotal(replyId)
                    .let { response ->
                        if (response.isSuccessful && response.body()!!.code == OK) {
                            _loading.postValue(Event(false))
                            reply.likeCount = ((reply.likeCount.toInt()) - 1).toString()
                            reply.liked = false
                            _reply.postValue(reply)
                            _isLikedComment.postValue(Event(false))
                            Log.d("tag_success", "unlikeComment: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "unlikeComment Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun deleteReply(boardType: String, replyId: Int, postId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.deleteUnivComment(replyId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            if (response.body()!!.code == 1000) {
                                getComments(boardType, postId, true)
                            }
                            // if it doesn't work, then result = repository.getUnivComment(postId).body()!!.result[position].cocomments
                            Log.d("tag_success", "deleteReply: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "deleteReply Error: ${response.code()}")
                        }
                    }
            } else {
                repository.deleteTotalComment(replyId)
                    .let { response ->
                        if (response.isSuccessful) {
                            _loading.postValue(Event(false))
                            getComments(boardType, postId, true)
                            //                            var result: List<Comment2>
                            //                            runBlocking {
                            //                                result = repository.getTotalComment(postId).body()!!.result
                            //                            }
                            //                            _commentList.postValue(result)
                            // if it doesn't work, then result = repository.getUnivComment(postId).body()!!.result[position].cocomments
                            Log.d("tag_success", "deleteReply: ${response.body()}")
                        } else {
                            Log.d("tag_fail", "deleteReply Error: ${response.code()}")
                        }
                    }
            }
        }
    }

    fun showReplyOptionDialog(reply: Reply) {
        _showReplyOptionDialog.postValue(Event(reply))
    }

    fun showMyReplyOptionDialog(reply: Reply) {
        _showMyReplyOptionDialog.postValue(Event(reply))
    }

    companion object {

        const val OK = 1000
        const val DUP_SCRAP = 3061
        const val DUP_LIKE = 3060
        const val DUP_REPORT = 2021
    }


}