package com.fruitfal.arjunsbi.presentation.newsFeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fruitfal.arjunsbi.R
import com.fruitfal.arjunsbi.core.utils.HelperMethods.getNewsFeedTime
import com.fruitfal.arjunsbi.databinding.LayoutNewsItemBinding

class TopHeadlinesAdapter(
    private var mainList: ArrayList<TopHeadlineItemUiState> = arrayListOf(),
) : RecyclerView.Adapter<TopHeadlinesAdapter.TopHeadlinesAdapterHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TopHeadlinesAdapterHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = LayoutNewsItemBinding.inflate(layoutInflater, viewGroup, false)
        return TopHeadlinesAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: TopHeadlinesAdapterHolder, position: Int) {
        val item = mainList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    fun addData(
        newList: List<TopHeadlineItemUiState>,
    ) {
        mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TopHeadlinesAdapterHolder internal constructor(var binding: LayoutNewsItemBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(post: TopHeadlineItemUiState) {
            binding.apply {
                post.let {
                    textViewNewsSource.text = it.newsSource
                    textViewNewsDescription.text = it.body
                    Glide.with(root.context)
                        .load(it.imageUrl)
                        .error(R.drawable.fake_news_icon)
                        .into(newsImage)
                    textViewNewsTime.text = getNewsFeedTime(it.time)
                }
                root.setOnClickListener {
                    post.saveNews(post.newsSource)
                }
            }
        }
    }

}