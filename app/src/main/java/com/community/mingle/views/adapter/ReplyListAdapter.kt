package com.community.mingle.views.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.BR
import com.community.mingle.R
import com.community.mingle.databinding.ItemReplyBinding
import com.community.mingle.service.models.Reply
import com.community.mingle.utils.DateUtils.formatTo
import com.community.mingle.utils.DateUtils.toDate


class ReplyListAdapter(private val parentPosition: Int) :
    RecyclerView.Adapter<ReplyListAdapter.ReplyViewHolder>() {

    interface OnReplyClickListener {
        fun onClickOption(item: Reply, position: Int)
        fun onLikeReply(position: Int, parentPosition: Int, reply: Reply)
        fun onWriteReply(replyId: Int, replyNickname: String)
    }

    private lateinit var replyClickListener: OnReplyClickListener

    fun setOnReplyClickListener(listener: OnReplyClickListener) {
        this.replyClickListener = listener
    }

    var replyList = ArrayList<Reply>()

    override fun getItemCount(): Int = replyList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReplyViewHolder {
        val binding: ItemReplyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_reply,
            parent,
            false
        )

        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bind(replyList[position], position, parentPosition)
    }

    fun getColoredString(text: CharSequence?, color: Int): Spannable {
        val spannable: Spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(color),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    inner class ReplyViewHolder(
        private val binding: ItemReplyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reply, position: Int, parentPosition: Int) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()

            binding.replyContent.setText(getColoredString("@"+item.mention+ "  ",Color.parseColor("#FF7663")))
            binding.replyContent.append(getColoredString(item.content,Color.BLACK))

            binding.replyDatetime.text = item.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()

            if (item.liked) {
                binding.replyLikeIv.setColorFilter(Color.parseColor("#FF7663"))
            } else {
                binding.replyLikeIv.setColorFilter(Color.parseColor("#CFCFCF"))
            }

            if (item.myComment) {
                binding.replyNickname.text = binding.replyNickname.text.toString() + " (나)"
                binding.replyNickname.setTextColor(Color.parseColor("#FF7663"))
            } else if (item.commentFromAuthor) {
                binding.replyNickname.setTextColor(Color.parseColor("#FF7663"))
            }

            binding.btnReplyMore.setOnClickListener {
                replyClickListener.onClickOption(item, position)
            }
            binding.btnReplyLike.setOnClickListener {
                replyClickListener.onLikeReply(position, parentPosition, item)
            }
            binding.btnReplyReply.setOnClickListener {
                replyClickListener.onWriteReply(item.commentId, item.nickname)
            }
        }
    }

    fun addReplyList(replyList: List<Reply>) {
        this.replyList.clear()
        this.replyList.addAll(replyList)
        notifyDataSetChanged()
    }

    fun updateItem(reply: Reply, position: Int) {
        replyList[position] = reply
        notifyItemChanged(position)
    }
}