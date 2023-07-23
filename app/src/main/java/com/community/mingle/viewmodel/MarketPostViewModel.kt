package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.service.models.Comment2
import com.community.mingle.service.models.ItemDetail
import com.community.mingle.service.models.MarketCommentSend
import com.community.mingle.service.models.MarketPostResult
import com.community.mingle.service.models.MarketReplySend
import com.community.mingle.service.models.Reply
import com.community.mingle.service.models.ReportPost
import com.community.mingle.service.repository.MarketRepository
import com.community.mingle.utils.Event
import com.community.mingle.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MarketPostViewModel
@Inject
constructor(
    private val repository: MarketRepository,
) : ViewModel() {

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    /* 거래게시판 리스트 불러오기 */
    private val _marketList = MutableLiveData<List<MarketPostResult>>()
    val marketList: LiveData<List<MarketPostResult>> get() = _marketList
    private val _lastMarketPostId = MutableLiveData<Int>()
    val lastMarketPostId: LiveData<Int> get() = _lastMarketPostId
    private val _newMarketList = MutableLiveData<List<MarketPostResult>>()
    val newMarketList: LiveData<List<MarketPostResult>> get() = _newMarketList
    private val _clearMarketList = MutableLiveData<Event<Boolean>>()
    val clearMarketList: LiveData<Event<Boolean>> = _clearMarketList

    // 게시글 좋아요 완료 여부
    private val _isLikedPost = MutableLiveData<Event<Boolean>>()
    val isLikedPost: LiveData<Event<Boolean>> = _isLikedPost

    // 게시글 좋아요 취소 완료 여부
    private val _isUnlikePost = MutableLiveData<Event<Boolean>>()
    val isUnlikePost: LiveData<Event<Boolean>> = _isUnlikePost

    // 판매 상태 변경 완료 여부
    private val _isChangeStatus = MutableLiveData<Event<Boolean>>()
    val isChangeStatus: LiveData<Event<Boolean>> = _isChangeStatus

    // 게시글 신고 완료 여부
    private val _isReportedPost = MutableLiveData<Event<Boolean>>()
    val isReportedPost: LiveData<Event<Boolean>> = _isReportedPost
    private val _post = MutableLiveData<ItemDetail>()
    val post: LiveData<ItemDetail> get() = _post
    private val _imageList = MutableLiveData<List<URL>>()
    val imageList: LiveData<List<URL>> get() = _imageList
    private val _commentList = MutableLiveData<List<Comment2>>()
    val commentList: LiveData<List<Comment2>> get() = _commentList
    private val _newCommentList = MutableLiveData<List<Comment2>>()
    val newCommentList: LiveData<List<Comment2>> get() = _newCommentList
    private val _replyList = MutableLiveData<List<Comment2>>()
    val replyList: LiveData<List<Comment2>> get() = _replyList

    // 대댓글 작성 이벤트
    private val _replySuccessEvent = MutableLiveData<Event<Boolean>>()
    val replySuccessEvent: LiveData<Event<Boolean>> = _replySuccessEvent
    private val _comment = MutableLiveData<Comment2>()
    val comment: LiveData<Comment2> get() = _comment
    private val _reply = MutableLiveData<Reply>()
    val reply: LiveData<Reply> get() = _reply

    /* 거래게시판 마이페이지 리스트 불러오기 */
    /* 판매내역 */
    private val _marketSellingList = MutableLiveData<List<MarketPostResult>>()
    val marketSellingList: LiveData<List<MarketPostResult>> get() = _marketSellingList
    private val _marketReservedList = MutableLiveData<List<MarketPostResult>>()
    val marketReservedList: LiveData<List<MarketPostResult>> get() = _marketReservedList
    private val _marketSoldoutList = MutableLiveData<List<MarketPostResult>>()
    val marketSoldoutList: LiveData<List<MarketPostResult>> get() = _marketSoldoutList
    private val _lastMarketSellingPostId = MutableLiveData<Int>()
    val lastMarketSellingPostId: LiveData<Int> get() = _lastMarketSellingPostId
    private val _lastMarketReservedPostId = MutableLiveData<Int>()
    val lastMarketReservedPostId: LiveData<Int> get() = _lastMarketReservedPostId
    private val _lastMarketSoldoutPostId = MutableLiveData<Int>()
    val lastMarketSoldoutPostId: LiveData<Int> get() = _lastMarketSoldoutPostId

    /* 찜한내역 */
    private val _marketMyPageList = MutableLiveData<List<MarketPostResult>>()
    val marketMyPageList: LiveData<List<MarketPostResult>> get() = _marketMyPageList
    private val _lastMarketMyPagePostId = MutableLiveData<Int>()
    val lastMarketMyPagePostId: LiveData<Int> get() = _lastMarketMyPagePostId
    private val _newMarketMyPageList = MutableLiveData<List<MarketPostResult>>()
    val newMarketMyPageList: LiveData<List<MarketPostResult>> get() = _newMarketMyPageList
    private val _showReplyOptionDialog = MutableLiveData<Event<Reply>>()
    val showReplyOptionDialog: LiveData<Event<Reply>> = _showReplyOptionDialog
    private val _showMyReplyOptionDialog = MutableLiveData<Event<Reply>>()
    val showMyReplyOptionDialog: LiveData<Event<Reply>> = _showMyReplyOptionDialog
    val write_title = MutableLiveData("")
    val write_price = MutableLiveData("")
    val write_content = MutableLiveData("")
    val write_location = MutableLiveData("")
    val write_chatUrl = MutableLiveData("")
    var returnInt: Int? = null
    var isFree = MutableLiveData<Boolean>()
    var isAnon = MutableLiveData<Boolean>()
    private val _alertMsg = MutableLiveData<Event<String>>()
    val alertMsg: LiveData<Event<String>> = _alertMsg
    private val _successEvent = MutableLiveData<Event<Int>>()
    val successEvent: LiveData<Event<Int>> = _successEvent
    private val _searchMarketList = MutableLiveData<List<MarketPostResult>>()
    val searchMarketList: LiveData<List<MarketPostResult>> get() = _searchMarketList
    private val _newMarketSearchList = MutableLiveData<List<MarketPostResult>>()
    val newMarketSearchList: LiveData<List<MarketPostResult>> get() = _newMarketSearchList

    fun isNewMarketList() {
        _newMarketSearchList.postValue(emptyList())
    }

    private fun check(): Boolean {
        var checkValue = true

        if (write_title.value.isNullOrBlank()) {
            _alertMsg.value = Event("제목을 입력해주세요")
            checkValue = false
        } else if (write_price.value.isNullOrBlank() || write_price.value!!.toDoubleOrNull() == null) {
            _alertMsg.value = Event("가격을 입력해주세요. 반드시 숫자만 입력해주세요.")
        } else if (write_content.value.isNullOrBlank()) {
            _alertMsg.value = Event("내용을 입력해주세요")
            checkValue = false
        }

        return checkValue
    }

    fun writeMarketPost(imageList: ArrayList<MultipartBody.Part>?) {
        if (!check()) return

        _loading.postValue(Event(true))
        val postTitle = write_title.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        var postPrice = write_price.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val postContent = write_content.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val postLocation = write_location.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val postChatUrl = write_chatUrl.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        if (isFree.value == true) {
            postPrice = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        }

        Log.d("isFree", isFree.value.toString())
        Log.d("isAnon", isAnon.value.toString())
        Log.d("imageList", imageList.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.createItemPost(postTitle, postPrice, postContent, postLocation, postChatUrl, isAnon.value!!, imageList).onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _successEvent.postValue(Event(response.body()!!.result.itemId))
                    Log.d("tag_success", "writePost: ${response.body()}")
                } else {
                    _loading.postValue(Event(false))
                    Log.d("tag_fail", "writePost Error: ${response.code()}")
                    when (response.body()?.code) {
                        null -> _alertMsg.postValue(Event("네트워크 오류가 발생했습니다."))
                        4000 -> _alertMsg.postValue(Event("서버에서 오류가 발생했습니다. 다시 시도해주세요."))
                        else -> _alertMsg.postValue(Event(response.body()!!.message))
                    }
                }
            }.onFailure {
                _loading.postValue(Event(false))
                _alertMsg.postValue(Event("네트워크 오류가 발생했습니다."))
                Log.d("tag_fail", "writePost Error: $it")
            }
        }
    }

    private fun stringsToRequestBody(strings: List<String>): RequestBody {
        val body = strings.joinToString("\n")
        return RequestBody.create("text/plain".toMediaType(), body)
    }

    fun editPost(itemId: Int, itemImageUrlsToDelete: List<String>?, itemImagesToAdd: ArrayList<MultipartBody.Part>?) {
        _loading.postValue(Event(true))
        val title = write_title.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        var price = write_price.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val content = write_content.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val location = write_location.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val chatUrl = write_chatUrl.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        if (isFree.value == true) {
            price = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        }
        Log.d("itemImagesToDelete", itemImageUrlsToDelete.toString())
        val newImageList: RequestBody? = itemImageUrlsToDelete?.let { stringsToRequestBody(it) }
        Log.d("requestbodyDelete", newImageList.toString())
        //        if (itemImageUrlsToDelete != null) {
        //            for (url in itemImageUrlsToDelete) {
        //                url.toRequestBody("text/plain".toMediaTypeOrNull())
        //                newImageList.add(url)
        //            }
        //        }
        //        if (itemImageUrlsToDelete != null) {
        //            for (url in itemImageUrlsToDelete) {
        //                val urlString = url.replaceFirst("^\"|\"$", "")
        //                newImageList.add(urlString)
        //            }
        //        }
        //        Log.d("moripain",newImageList.toString())
        Log.d("moji", itemImagesToAdd.toString())

        viewModelScope.launch(Dispatchers.IO) {
            if (newImageList != null) {
                repository.modifyItemPost(itemId, title, content, price, location, chatUrl, newImageList, itemImagesToAdd).onSuccess { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        _successEvent.postValue(Event(itemId))
                        Log.d("tag_success", "editUnivPost: ${response.body()}")
                    } else {
                        _successEvent.postValue(Event(-1))
                        Log.d("tag_fail", "editPost Error: ${response.code()}")
                    }
                }
            }
            _loading.postValue(Event(false))
        }
    }

    fun getMarketList(isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getItemList(100000000)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                            _marketList.postValue(response.body()!!.result.itemListDTO)
                            val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                            _lastMarketPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                            Log.d("tag_success", response.body().toString())
                        } else if (response.body()!!.code == 3031) {
                            _marketList.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getUnivList Error: ${response.code()}")
                    }
                }

            if (isRefreshing)
                _loading.postValue(Event(false))
        }
    }

    fun getMarketNextPosts(lastPostId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getItemList(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    Log.d("tag_success", "getNextMarketPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                        _marketList.postValue(response.body()!!.result.itemListDTO)
                        val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                        _lastMarketPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                    } else if (response.body()!!.code == 3031) {
                        _lastMarketPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMarketNextPosts Error: ${response.code()}")
                }
            }
            _loading.postValue(Event(false))
        }
    }

    fun deleteMarketPost(itemId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItemPost(itemId)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        Log.d("tag_success", response.body().toString())
                    } else {
                        Log.d("tag_fail", "deletePost Error: ${response.code()}")
                    }
                }
        }
    }

    fun reportPost(tableType: String, itemId: Int, reportTypeId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.createReport(ReportPost(tableType, itemId, reportTypeId))
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        when (response.body()!!.code) {
                            1000 -> {
                                _isReportedPost.postValue(Event(true))
                                Log.d("tag_success", "reportPost: ${response.body()}")
                            }

                            2021 -> {
                                _isReportedPost.postValue(Event(false))
                            }
                        }
                    } else {
                        Log.d("tag_fail", "reportPost Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun likeMarketPost(itemId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.createItemLike(itemId)
                .onSuccess { response ->
                    if (response.isSuccessful && response.body()!!.code == PostViewModel.OK) {
                        _isLikedPost.postValue(Event(true))
                        Log.d("tag_success", "likePost: ${response.body()}")
                    } else if (response.body()!!.code == PostViewModel.DUP_LIKE) {
                        _isLikedPost.postValue(Event(false))
                        unlikeMarketPost(itemId)
                        Log.d("tag_fail", "likePost Error: ${response.code()}")
                    } else {
                        Log.d("tag_fail", "likePost Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun unlikeMarketPost(itemId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.itemUnlike(itemId)
                .onSuccess { response ->
                    if (response.isSuccessful && response.body()!!.code == PostViewModel.OK) {
                        _isUnlikePost.postValue(Event(true))
                        Log.d("tag_success", "unlikePost: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "unlikePost Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun getMarketPost(itemId: Int, isRefreshing: Boolean) {
        if (!isRefreshing)
            _loading.postValue(Event(false))
        // need to check whether it's univ or total post
        viewModelScope.launch(Dispatchers.IO) {
            repository.getItemPostDetail(itemId)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _post.postValue(response.body()!!.result)
                        _imageList.postValue(response.body()!!.result.postImgUrl)
                        Log.d("tag_success", response.body().toString())
                    } else {
                        Log.d("tag_fail", "getMarketPostDetail Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun getComments(itemId: Int, isRefreshing: Boolean) {
        _loading.postValue(Event(true))
        viewModelScope.launch(Dispatchers.IO) {
            repository.getComment(itemId)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        Log.d("tag_success", "getComments: ${response.body()}")

                        if (response.body()!!.code == 1000) {
                            _loading.postValue(Event(false))
                            _commentList.postValue(response.body()!!.result)
                        }
                    } else {
                        Log.d("tag_fail", "getComments Error: ${response.code()}")
                    }
                }
            if (!isRefreshing) {
                _loading.postValue(Event(false))
            }
        }
    }

    fun writeComment(itemId: Int, anonymous: Boolean) {
        val comment = MarketCommentSend(
            isAnonymous = anonymous,
            content = write_content.value!!,
            itemId = itemId
        )

        Log.d("tag_comment", comment.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.commentPost(comment).onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    val result: List<Comment2> = repository.getComment(itemId).getOrNull()?.body()?.result ?: emptyList()
                    _newCommentList.postValue(result)
                    Log.d("tag_success", "writeComment: ${response.body()}")
                } else {
                    Log.d("tag_fail", "writeComment Error: $response")
                }
            }
        }
    }

    fun deleteComment(comment: Comment2, itemId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.commentDelete(comment.commentId)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000) {
                            getComments(itemId, false)
                        }
                        Log.d("tag_success", "deleteComment: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "deleteComment Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun writeReply(itemId: Int, mentionId: Int, parentReplyId: Int, anonymous: Boolean) {
        val reply = MarketReplySend(
            isAnonymous = anonymous,
            content = write_content.value!!,
            mentionId = mentionId,
            parentCommentId = parentReplyId,
            itemId = itemId
        )

        Log.d("tag_comment", comment.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.replyPost(reply).onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()!!.code == 1000) {
                        val result: List<Comment2> = repository.getComment(itemId).getOrNull()?.body()?.result ?: emptyList()
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

    fun deleteReply(replyId: Int, itemId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.commentDelete(replyId)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000) {
                            getComments(itemId, true)
                        }
                        // if it doesn't work, then result = repository.getUnivComment(postId).body()!!.result[position].cocomments
                        Log.d("tag_success", "deleteReply: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "deleteReply Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun changeStatus(itemId: Int, itemStatus: String) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.modifyItemStatus(itemId, itemStatus)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        when (response.body()!!.code) {
                            PostViewModel.OK -> {
                                _isChangeStatus.postValue(Event(true))
                            }

                            else -> {
                                _isChangeStatus.postValue(Event(false))
                            }
                        }
                    } else {
                        Log.d("tag_fail", "changeStatus Error: ${response.code()}")
                    }
                }
            _loading.postValue(Event(false))
        }
    }

    fun getMarketItemList(isRefreshing: Boolean, itemStatus: String) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.myItemList(Int.MAX_VALUE, itemStatus)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                            when (itemStatus) {
                                "SELLING" -> {
                                    val list = _marketSellingList.value?.plus(response.body()!!.result.itemListDTO)
                                        ?: response.body()!!.result.itemListDTO
                                    _marketSellingList.postValue(list)
                                    val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                    _lastMarketSellingPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                                    Log.d("tag_success", response.body().toString())
                                }

                                "RESERVED" -> {
                                    val list = _marketReservedList.value?.plus(response.body()!!.result.itemListDTO)
                                        ?: response.body()!!.result.itemListDTO
                                    _marketReservedList.postValue(list)
                                    val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                    _lastMarketReservedPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                                    Log.d("tag_success", response.body().toString())
                                }

                                else -> {
                                    val list = _marketSoldoutList.value?.plus(response.body()!!.result.itemListDTO)
                                        ?: response.body()!!.result.itemListDTO
                                    _marketSoldoutList.postValue(list)
                                    val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                    _lastMarketSoldoutPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                                    Log.d("tag_success", response.body().toString())
                                }
                            }
                        } else if (response.body()!!.code == 3034) {
                            when (itemStatus) {
                                "SELLING" -> {
                                    _marketSellingList.postValue(emptyList())
                                }

                                "RESERVED" -> {
                                    _marketReservedList.postValue(emptyList())
                                }

                                else -> {
                                    _marketSoldoutList.postValue(emptyList())
                                }
                            }
                        }
                    } else {
                        Log.d("tag_fail", "getMarketMyPageList Error: ${response.code()}")
                    }
                }
            if (isRefreshing)
                _loading.postValue(Event(false))
        }
    }

    fun getMarketItemNextPosts(lastPostId: Int, itemStatus: String) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.myItemList(lastPostId, itemStatus).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextMarketMyPagePosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                        when (itemStatus) {
                            "SELLING" -> {
                                val list = _marketSellingList.value?.plus(response.body()!!.result.itemListDTO)
                                    ?: response.body()!!.result.itemListDTO
                                _marketSellingList.postValue(list)
                                val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                _lastMarketSellingPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                            }

                            "RESERVED" -> {
                                val list = _marketReservedList.value?.plus(response.body()!!.result.itemListDTO)
                                    ?: response.body()!!.result.itemListDTO
                                _marketReservedList.postValue(list)
                                val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                _lastMarketReservedPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                            }

                            else -> {
                                val list = _marketSoldoutList.value?.plus(response.body()!!.result.itemListDTO)
                                    ?: response.body()!!.result.itemListDTO
                                _marketSoldoutList.postValue(list)
                                val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                                _lastMarketSoldoutPostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                            }
                        }
                    } else if (response.body()!!.code == 3034) {
                        when (itemStatus) {
                            "SELLING" -> {
                                _lastMarketSellingPostId.postValue(-1)
                            }

                            "RESERVED" -> {
                                _lastMarketReservedPostId.postValue(-1)
                            }

                            else -> {
                                _lastMarketSoldoutPostId.postValue(-1)
                            }
                        }
                    }
                } else {
                    Log.d("tag_fail", "getMarketMyPageNextPosts Error: ${response.code()}")
                }
            }
            _loading.postValue(Event(false))
        }
    }

    fun getMarketLikedItemList(isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.myLikedItems(100000000)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                            _marketMyPageList.postValue(response.body()!!.result.itemListDTO)
                            val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                            _lastMarketMyPagePostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                            Log.d("tag_success", response.body().toString())
                        } else if (response.body()!!.code == 3031) {
                            _marketMyPageList.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getMarketLikedList Error: ${response.code()}")
                    }
                }
        }
    }

    fun getMarketLikedItemNextPosts(lastPostId: Int) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.myLikedItems(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextMarketMyPagePosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                        _marketMyPageList.postValue(response.body()!!.result.itemListDTO)
                        val lastIdx = response.body()!!.result.itemListDTO.lastIndex
                        _lastMarketMyPagePostId.postValue(response.body()!!.result.itemListDTO[lastIdx].id)
                    } else if (response.body()!!.code == 3031) {
                        _lastMarketMyPagePostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMarketLikedNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMarketSearchList(keyword: String, isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.itemSearch(keyword)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.itemListDTO.isNotEmpty()) {
                            _searchMarketList.postValue(response.body()!!.result.itemListDTO)
                        } else if (response.body()!!.code == 3035) {
                            _searchMarketList.postValue(emptyList())
                        } else {
                        }
                    } else {
                        Log.d("tag_fail", "getMarketSearchList Error: ${response.code()}")
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
}




