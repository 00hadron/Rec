package ru.hadron.rec.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.hadron.rec.R
import ru.hadron.rec.db.model.Record

class RecListAdapter : RecyclerView.Adapter<RecListAdapter.RecViewHolder>() {

    inner class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolder {
        return RecViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rec,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecViewHolder, position: Int) {
       val record = differ.currentList[position]
        holder.itemView.apply {
            //to do
            setOnClickListener{
                onItemClickListener?.let { it(record) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Record) -> Unit)?= null

    fun setOnItemClickListener(listener: (Record) -> Unit) {
        onItemClickListener = listener
    }
}