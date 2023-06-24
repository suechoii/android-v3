package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ItemHomeBinding
import com.community.mingle.service.models.HomeResult
import com.community.mingle.BR
import com.community.mingle.service.models.Comment2
import com.community.mingle.service.models.PostResult

class HomeListAdapter : RecyclerView.Adapter<HomeListAdapter.HomeViewHolder>() {
    private var homeList = ArrayList<HomeResult>()

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(post: HomeResult, pos: Int, isBlind: Boolean, isReported: Boolean, reportText: String?)
        fun onCancelClick(post: HomeResult, pos: Int)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = homeList.size

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
        holder.bind(homeList[position], position)
    }

    inner class HomeViewHolder(val binding : ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeResult: HomeResult, position: Int) {
            binding.setVariable(BR.item, homeResult)

            if (homeResult.blinded) {

                binding.likeimg.visibility = View.INVISIBLE
                binding.commentimg.visibility = View.INVISIBLE
                binding.photo.visibility = View.INVISIBLE
                binding.contenttext.visibility = View.INVISIBLE
                binding.titletext.visibility = View.INVISIBLE
                binding.liketext.visibility = View.INVISIBLE
                binding.commenttext.visibility = View.INVISIBLE
                binding.anonymous.visibility = View.INVISIBLE
                binding.time.visibility = View.INVISIBLE
                binding.dot.visibility = View.INVISIBLE

                binding.blindedText.visibility = View.VISIBLE
                binding.cancelBlindTv.visibility = View.VISIBLE

                binding.cancelBlindTv.setOnClickListener {
                    mItemClickListener.onCancelClick(homeResult, position)
                }

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(homeResult, position, true, false, null)

                }
            }

            else if (homeResult.reported) {
                binding.likeimg.visibility = View.INVISIBLE
                binding.commentimg.visibility = View.INVISIBLE
                binding.photo.visibility = View.INVISIBLE
                binding.contenttext.visibility = View.INVISIBLE
                binding.titletext.visibility = View.INVISIBLE
                binding.liketext.visibility = View.INVISIBLE
                binding.commenttext.visibility = View.INVISIBLE
                binding.anonymous.visibility = View.INVISIBLE
                binding.time.visibility = View.INVISIBLE
                binding.dot.visibility = View.INVISIBLE

                binding.blindedText.visibility = View.VISIBLE
                binding.blindedText.text = homeResult.title

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(homeResult, position, false, true, homeResult.title)
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
                binding.anonymous.visibility = View.VISIBLE
                binding.time.visibility = View.VISIBLE
                binding.dot.visibility = View.VISIBLE

                binding.blindedText.visibility = View.GONE
                binding.cancelBlindTv.visibility = View.GONE

                binding.cancelBlindTv.setOnClickListener {
                    mItemClickListener.onCancelClick(homeResult, position)
                }

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(homeResult, position, false, false, null)
                }
            }

            binding.executePendingBindings()

        }
    }

    fun updateItem(homeResult: HomeResult, position: Int) {
        homeList[position] = homeResult
        notifyItemChanged(position)
    }


    fun addHomeList(boardList: List<HomeResult>) {
        this.homeList.clear()
        this.homeList.addAll(boardList)
        notifyDataSetChanged()
    }
}