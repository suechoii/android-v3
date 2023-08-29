package com.community.mingle.views.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.databinding.ItemCommentBinding
import com.community.mingle.service.models.Comment2
import com.community.mingle.service.models.Reply
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.BR
import com.community.mingle.R
import com.community.mingle.utils.DateUtils.formatTo
import com.community.mingle.utils.DateUtils.toDate
import com.community.mingle.viewmodel.MarketPostViewModel

class MarketCommentListAdapter(private val context: Context, private val menuInflater: MenuInflater, private val viewModel: MarketPostViewModel) :
    RecyclerView.Adapter<MarketCommentListAdapter.CommentViewHolder>() {

    interface OnCommentClickListener {
        fun onClickCommentOption(comment: Comment2)
        fun onLikeComment(position: Int, comment: Comment2)
        fun onWriteReply(parentCommentId: Int, mentionNickname: String, mentionId: Int)
        fun onLikeReply(position: Int, parentPosition: Int, reply: Reply, comment: Comment2)
    }

    private lateinit var commentClickListener: OnCommentClickListener

    fun setOnCommentClickListener(listener: OnCommentClickListener) {
        this.commentClickListener = listener
    }

    var commentList = ArrayList<Comment2>()

    override fun getItemCount(): Int = commentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding: ItemCommentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_comment,
            parent,
            false
        )

        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position], position, viewModel)
    }

    inner class CommentViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment2, position: Int, viewModel: MarketPostViewModel) {
            Log.d("check",item.toString()+" "+position.toString())
            lateinit var replyListAdapter: ReplyListAdapter
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()

           //viewModel.reply

            binding.commentDatetime.text = item.createdAt.toDate()?.formatTo("MM/dd HH:mm").toString()

            if (item.liked) {
                binding.commentLikeIv.setColorFilter(ContextCompat.getColor(context, R.color.orange_02))
            } else {
                binding.commentLikeIv.setColorFilter(Color.parseColor("#CFCFCF"))
            }

            if (!item.commentDeleted) {
                binding.btnCommentMore.visibility = View.VISIBLE
                binding.btnCommentLike.visibility = View.VISIBLE

                if (item.myComment ) {
                    binding.commentNickname.text = binding.commentNickname.text.toString() + " (나)"
                    binding.commentNickname.setTextColor(Color.parseColor("#FF7663"))
                } else if (item.commentFromAuthor) {
                    binding.commentNickname.setTextColor(Color.parseColor("#FF7663"))
                }
            }

            if ((item.commentDeleted or item.commentReported) && item.coCommentsList != null) {
                Log.d("item",item.toString())
                binding.commentContent.setTypeface(Typeface.createFromAsset(context.assets,"pretendard_regular.otf"), Typeface.ITALIC)
                binding.btnCommentMore.visibility = View.GONE
                binding.btnCommentLike.visibility = View.GONE
                binding.commentNickname.setTextColor(Color.parseColor("#CFCFCF"))

                replyListAdapter = ReplyListAdapter(position)
                replyListAdapter.addReplyList(item.coCommentsList as ArrayList<Reply>)
                replyListAdapter.notifyDataSetChanged()

                binding.cocommentRv.apply {
                    adapter = replyListAdapter
                    layoutManager = LinearLayoutManager(binding.root.context)
                    hasFixedSize()
                }

                replyListAdapter.setOnReplyClickListener(object :
                    ReplyListAdapter.OnReplyClickListener {

                    override fun onClickOption(reply: Reply, position: Int) {
                        if (reply.myComment) {
                            viewModel.showMyReplyOptionDialog(reply)
                        } else {
                            viewModel.showReplyOptionDialog(reply)
                        }
                    }

                    override fun onLikeReply(position: Int, parentPosition: Int, reply: Reply) {
                        commentClickListener.onLikeReply(position, parentPosition, reply, item)
                    }

                    override fun onWriteReply(replyId: Int, replyNickname: String) {
                        commentClickListener.onWriteReply(item.commentId,replyNickname,replyId)
                    }
                })
            } else if (item.commentReported){
                binding.commentContent.setTypeface(Typeface.createFromAsset(context.assets,"pretendard_regular.otf"), Typeface.ITALIC)
                binding.btnCommentMore.visibility = View.GONE
                binding.btnCommentLike.visibility = View.GONE
                binding.commentNickname.setTextColor(Color.parseColor("#CFCFCF"))
            }
            else {
                replyListAdapter = ReplyListAdapter(position)
                replyListAdapter.addReplyList(item.coCommentsList as ArrayList<Reply>)
                replyListAdapter.notifyDataSetChanged()

                binding.cocommentRv.apply {
                    adapter = replyListAdapter
                    layoutManager = LinearLayoutManager(binding.root.context)
                    hasFixedSize()
                }

                replyListAdapter.setOnReplyClickListener(object :
                    ReplyListAdapter.OnReplyClickListener {

                    override fun onClickOption(reply: Reply, position: Int) {
                        if (reply.myComment) {
                            viewModel.showMyReplyOptionDialog(reply)
                        } else {
                            viewModel.showReplyOptionDialog(reply)
                        }
                    }

                    override fun onLikeReply(position: Int, parentPosition: Int, reply: Reply) {
                        commentClickListener.onLikeReply(position, parentPosition, reply, item)
                    }

                    override fun onWriteReply( replyId: Int, replyNickname: String) {
                        commentClickListener.onWriteReply(item.commentId,replyNickname,replyId)
                    }
                })
            }

            binding.btnCommentLike.setOnClickListener {
                commentClickListener.onLikeComment(position, item)
            }

            binding.btnCommentMore.setOnClickListener {
                commentClickListener.onClickCommentOption(item)
            }

            binding.btnCommentReply.setOnClickListener {
                commentClickListener.onWriteReply(item.commentId, item.nickname,item.commentId)
            }
        }
    }

    fun clearItems() {
        this.commentList.clear()
        notifyDataSetChanged()
    }

    fun updateItem(comment: Comment2, position: Int) {
        commentList[position] = comment
        notifyItemChanged(position)
    }

    fun addCommentList(commentList: List<Comment2>) {
        this.commentList.clear()
        this.commentList.addAll(commentList)
        Log.d("commentList",this.commentList.toString())
        notifyDataSetChanged()
    }
}