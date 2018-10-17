package io.zaglab.android.locationupdatemonitor.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.zaglab.android.locationupdatemonitor.BR
import io.zaglab.android.locationupdatemonitor.R
import io.zaglab.android.locationupdatemonitor.data.Location
import io.zaglab.android.locationupdatemonitor.databinding.ItemLocationHistoryBinding

class HistoryAdapter(var items: List<Location.ViewModel> = emptyList()) : RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemLocationHistoryBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_location_history, parent, false)
        return ViewHolder(binding)
    }

//    override fun getItemViewType(position: Int): Int {
//        return getLayoutId(position)
//    }

    override fun getItemCount() = items.size


}

class ViewHolder(val binding: ItemLocationHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(obj: Any) {
        binding.setVariable(BR.obj, obj)
        binding.executePendingBindings()
    }
}

