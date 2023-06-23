package com.community.mingle.viewmodel

import android.media.Image
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.service.models.Edit
import com.community.mingle.service.repository.PostRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PostWriteViewModel
@Inject
constructor(
    private val repository: PostRepository,
) : ViewModel() {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    var returnInt : Int? = null

    var categoryName = MutableLiveData("")
    var categoryInt = MutableLiveData<Int>()
    var isAnon = MutableLiveData<Boolean>()

    private val _getCategorySuccess = MutableLiveData<Event<Boolean>>()
    val getCategorySuccess: LiveData<Event<Boolean>> = _getCategorySuccess

    private val _alertMsg = MutableLiveData<Event<String>>()
    val alertMsg: LiveData<Event<String>> = _alertMsg

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    private val _successEvent = MutableLiveData<Event<Int>>()
    val successEvent: LiveData<Event<Int>> = _successEvent

    private fun check(): Boolean {

        var checkValue = true

        if (title.value.isNullOrBlank()) {
            _alertMsg.value = Event("제목을 입력해주세요")
            checkValue = false
        } else if (content.value.isNullOrBlank()) {
            _alertMsg.value = Event("내용을 입력해주세요")
            checkValue = false
        }

        return checkValue
    }

    fun getCategoryCount() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPostCategory().let { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _getCategorySuccess.postValue(Event(true))
                    returnInt = response.body()!!.result.size
                }
            }
        }
    }

    // boardType: 게시판 이름: 자유 등등,, imageList : 안되면 List<Uri>도 고려...
    fun writePost(boardType: String, imageList: ArrayList<MultipartBody.Part>?) {
        if (!check()) return

        _loading.postValue(Event(true))

        val postTitle = title.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        val postBody = content.value!!.toRequestBody("text/plain".toMediaTypeOrNull())
        Log.d("postTitle",title.value.toString())
        Log.d("postBody",content.value.toString())
        Log.d("categoryInt",categoryInt.value.toString())
        Log.d("isAnon",isAnon.value.toString())
        Log.d("imageList",imageList.toString())

        viewModelScope.launch(Dispatchers.IO) {
            if (boardType == "잔디밭") {
                repository.writeUnivPost(categoryInt.value!!,postTitle,postBody,isAnon.value!!,imageList).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        _successEvent.postValue(Event(response.body()!!.result.postId))
                        Log.d("tag_success", "writePost: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writePost Error: ${response.code()}")
                    }
                }
            } else {
                repository.writeTotalPost(categoryInt.value!!,postTitle,postBody,isAnon.value!!,imageList).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        _successEvent.postValue(Event(response.body()!!.result.postId))
                        Log.d("tag_success", "writePost: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "writePost Error: ${response.code()}")
                    }
                }
            }
        }
    }

    fun editPost(boardType: String, postId: Int,title: String, content: String) {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO){

            if (boardType == "잔디밭") {
                repository.editUnivPost(postId, Edit(title, content)).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        _successEvent.postValue(Event(postId))
                        Log.d("tag_success", "editUnivPost: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "editPost Error: ${response.code()}")
                    }
                }
            } else {
                repository.editTotalPost(postId, Edit(title, content)).let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) {
                        _successEvent.postValue(Event(postId))
                        Log.d("tag_success", "editTotalPost: ${response.body()}")
                    } else {
                        Log.d("tag_fail", "editPost Error: ${response.code()}")
                    }
                }
            }
        }

    }
}