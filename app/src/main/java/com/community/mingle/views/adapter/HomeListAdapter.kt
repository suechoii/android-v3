package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ItemHomeBinding
import com.community.mingle.service.models.HomeResult
import com.community.mingle.BR
import com.community.mingle.service.models.Comment2
import com.community.mingle.service.models.PostResult

class HomeListAdapter : RecyclerView.Adapter<HomeListAdapter.HomeViewHolder>() {
    private var first = ArrayList<HomeResult>()

    // 추가
    private val differCallback = object : DiffUtil.ItemCallback<HomeResult>() {
        override fun areItemsTheSame(oldItem: HomeResult, newItem: HomeResult): Boolean {
            // User의 id를 비교해서 같으면 areContentsTheSame으로 이동
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: HomeResult, newItem: HomeResult): Boolean {
            // User의 내용을 비교해서 같으면 true -> UI 변경 없음
            // User의 내용을 비교해서 다르면 false -> UI 변경
            return oldItem == newItem
        }
    }

    // 리스트가 많으면 백그라운드에서 실행하는 게 좋은데 AsyncListDiffer은 자동으로 백그라운드에서 실행
    val differ = AsyncListDiffer(this, differCallback)

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(post: HomeResult, pos: Int, isReported: Boolean, reportText: String?)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): HomeListAdapter.HomeViewHolder {
        val binding : ItemHomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_home,
            viewGroup,
            false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        // 수정 후
        if (first.isNotEmpty()) {
            holder.bind(first[position], position)
        } else {
            val updatedList = differ.currentList[position]
            holder.bind(updatedList, position)
        }
    }

    inner class HomeViewHolder(val binding : ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeResult: HomeResult, position: Int) {
            binding.setVariable(BR.item, homeResult)

             if (homeResult.reported) {
                binding.likeimg.visibility = View.INVISIBLE
                binding.commentimg.visibility = View.INVISIBLE
                binding.photo.visibility = View.INVISIBLE
                binding.contenttext.visibility = View.INVISIBLE
                binding.titletext.visibility = View.INVISIBLE
                binding.liketext.visibility = View.INVISIBLE
                binding.commenttext.visibility = View.INVISIBLE
                binding.anonymous.visibility = View.INVISIBLE
                binding.time2Tv.visibility = View.INVISIBLE
                binding.ellipse2Iv.visibility = View.INVISIBLE

                binding.blindedText.visibility = View.VISIBLE
                binding.blindedText.text = homeResult.title

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(homeResult, position, true, homeResult.title)
                }
            }

            else {
                 val fileattached = homeResult.fileAttached
                 if (fileattached) {
                     binding.photo.visibility = View.VISIBLE
                 } else {
                     binding.photo.visibility = View.GONE
                 }

                 binding.likeimg.visibility = View.VISIBLE
                 binding.commentimg.visibility = View.VISIBLE
                 binding.contenttext.visibility = View.VISIBLE
                 binding.titletext.visibility = View.VISIBLE
                 binding.liketext.visibility = View.VISIBLE
                 binding.commenttext.visibility = View.VISIBLE

                 if (homeResult.categoryType == "한인회" || homeResult.categoryType == "밍글소식") {
                     binding.anonSpecialTv.visibility = View.VISIBLE
                     binding.anonymous.visibility = View.GONE
                     binding.specialIcon.visibility = View.VISIBLE
                     binding.ellipseIv.visibility = View.VISIBLE
                     binding.ellipse2Iv.visibility = View.GONE
                     binding.timeTv.visibility = View.VISIBLE
                     binding.time2Tv.visibility = View.GONE
                 } else {
                     binding.anonymous.visibility = View.VISIBLE
                     binding.ellipse2Iv.visibility = View.VISIBLE
                     binding.time2Tv.visibility = View.VISIBLE
                     binding.timeTv.visibility = View.GONE
                     binding.ellipseIv.visibility = View.GONE
                     binding.anonSpecialTv.visibility = View.GONE
                     binding.specialIcon.visibility = View.GONE
                 }

                binding.blindedText.visibility = View.GONE

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(homeResult, position,  false, null)
                }
            }

            binding.executePendingBindings()

        }
    }

//    fun updateItem(homeResult: HomeResult, position: Int) {
//        homeList[position] = homeResult
//        notifyItemChanged(position)
//    }


    fun addFirstHomeList(boardList: List<HomeResult>) {
        this.first = boardList as ArrayList<HomeResult>
    }
}