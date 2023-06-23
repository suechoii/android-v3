package com.community.mingle.views.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ItemNotificationBinding
import com.community.mingle.service.models.NotiData

class NotiRVAdapter(private val notilist : ArrayList<NotiData> ): RecyclerView.Adapter<NotiRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(noti: NotiData) {
            Log.d("RVAdapter",noti.toString())
            val notitype = noti.notificationType
            if (notitype == "댓글") {
                binding.notiTitle.text="새로운 댓글이 달렸어요."
                binding.notiIv.setImageResource(R.drawable.ic_comment)
            } else if (notitype == "인기"){
                binding.notiTitle.text="내가 쓴 글이 인기게시글이 되었어요!"
                binding.notiIv.setImageResource(R.drawable.ic_thumbsup)
            } else if (notitype == "대댓글"){
                binding.notiTitle.text="새로운 대댓글이 달렸어요."
                binding.notiIv.setImageResource(R.drawable.ic_comment)
            }
            binding.notiContent.text = noti.content.toString()
            binding.tabName.text = noti.boardType.toString()
            binding.tabId.text = noti.category.toString()
            if (noti.read == false) {
                binding.notiLayout.setBackgroundResource(R.drawable.unreadnoti)
            } else {
                binding.notiLayout.setBackgroundResource(R.color.transparent)
            }
        }
    }

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(noti: NotiData)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): NotiRVAdapter.ViewHolder {
        val binding: ItemNotificationBinding =
            ItemNotificationBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }
//        val binding : ItemNotificationBinding = ItemNotificationBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
//        return ViewHolder(binding)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notilist[position])
        Log.d("bind",notilist[position].toString())

        holder.itemView.setOnClickListener { mItemClickListener.onItemClick(notilist[position]) }
    }

    override fun getItemCount(): Int = notilist.size
}