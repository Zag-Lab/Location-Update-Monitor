package io.zaglab.android.locationupdatemonitor

import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import io.zaglab.android.locationupdatemonitor.data.Location
import io.zaglab.android.locationupdatemonitor.history.HistoryAdapter
import io.zaglab.android.locationupdatemonitor.history.LocationViewModel
import io.zaglab.android.locationupdatemonitor.main.RequestBuilderViewModel

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("entries")
    fun Spinner.setEntries(entries: Array<RequestBuilderViewModel.Requester.PriorityType>) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("selectedValue")
    fun Spinner.setSelectedValue(value: Any?) {
        if (adapter != null) {
            val position = (adapter as ArrayAdapter<Any>).getPosition(value)
            setSelection(position, false)
            tag = position
        }
    }

    @JvmStatic
    @BindingAdapter("selectedValueAttrChanged")
    fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
        if (inverseBindingListener == null) {
            onItemSelectedListener = null
        } else {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (tag != position) {
                        inverseBindingListener.onChange()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    fun Spinner.getSelectedValue(): Any {
        return selectedItem
    }

    @JvmStatic
    @BindingAdapter("scrollable")
    fun TextView.setScrollable(isScrollable: Boolean) {
        if (isScrollable) {
            movementMethod = ScrollingMovementMethod()
        }
    }

    @JvmStatic
    @BindingAdapter("items")
    fun RecyclerView.setItems(items: List<Location.ViewModel>) {
        val adapter = HistoryAdapter(items)
        this.adapter = adapter
//        adapter.items = items
        adapter.notifyDataSetChanged()
    }

//    @JvmStatic
//    @BindingAdapter("layout_manager")
//    fun RecyclerView.setLayoutManager() {
//        val adapter = HistoryAdapter(items)
//        this.adapter = adapter
////        adapter.items = items
//        adapter.notifyDataSetChanged()
//    }

}