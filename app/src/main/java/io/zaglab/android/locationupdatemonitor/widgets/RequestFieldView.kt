//package io.zaglab.android.locationupdatemonitor.widgets
//
//import android.content.Context
//import android.databinding.BindingAdapter
//import android.databinding.DataBindingUtil
//import android.databinding.InverseBindingAdapter
//import android.databinding.InverseBindingListener
//import android.text.Editable
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewStub
//import android.widget.EditText
//import android.widget.RelativeLayout
//import io.zaglab.android.locationupdatemonitor.LocationRequestViewModel
//import io.zaglab.android.locationupdatemonitor.R
//import kotlinx.android.synthetic.main.request_field_view.view.*
//import timber.log.Timber
//
//class RequestFieldView : RelativeLayout {
//    constructor(context: Context?) : super(context) {
//        setupUi(context)
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        setupUi(context)
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        setupUi(context)
//    }
//
//    private lateinit var valueView: View
//
//    enum class InputType { NUMBER, SELECTION, SWITCH }
//
//    private fun setupUi(context: Context?) {
//        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.request_field_view, null, false)
////        binding.stub.viewStub?.inflate()
//        addView(binding.root)
//    }
//
//    fun setLabel(label: String) {
//        binding.label.text = label
//    }
//
//    fun setInputType(type: InputType) {
////        with(binding.flipperValue) {
////            displayedChild = when (type) {
////                InputType.NUMBER -> 0
////                InputType.SELECTION -> 1
////                InputType.SWITCH -> 2
////            }
////            valueView = currentView
////        }
//    }
//
////    fun setEntries(entries: Array<String>) {
////        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entries)
////        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerValue.adapter = arrayAdapter
////    }
//
////    sealed class ViewType<T:View>(val type: Class<T>) {
////        class EditText:ViewType<android.widget.EditText>(android.widget.EditText::class.java)
////    }
//
//    enum class ViewType { EDITTEXT, IMAGEVIEW }
//    lateinit var viewType: ViewType
//
//    fun setValueViewType(type: ViewType) {
//        viewType = type
//        val stub = binding.value.viewStub ?: throw Exception("Not a viewstub")
//        val view = when (type) {
//            ViewType.EDITTEXT -> EditText(context)
//            else -> View(context)
//        }
//        val container = binding.root as RelativeLayout
//        container.replaceView(stub, view)
//    }
//
//    fun <T : View> ViewGroup.replaceView(old: T, new: T) {
//        val index = indexOfChild(old)
//        new.layoutParams = old.layoutParams
//        new.id = old.id
//        removeView(old)
//        addView(new, index)
//    }
//
//    fun setLocationRequest(viewModel: LocationRequestViewModel) {
//        binding.locationRequest = viewModel
//    }
//
//    fun <T : View> setValueViewType(type: T) {
//    }
//
////    @BindingAdapter("inputType", "items")
////    fun setPrioritySpinnner(view: RequestFieldView, input: InputType, items: List<String>) {
////        (view.valueView as Spinner).adapter = SpinnerBindingAdapter
////    }
//
////    fun getValue() : Int {
////        val viewStub: ViewStub
////        viewStub.inflate()
////        with(binding.flipperValue.currentView) {
////            when (this) {
////                is EditText -> return text.toString().toInt()
////                is Spinner -> return selectedItem
////                is Switch -> return
////            }
////        }
////    }
//
//    companion object {
//        @InverseBindingAdapter(attribute = "value")
//        @JvmStatic
//        fun getValue(view: RequestFieldView): Int {
//            with(view.binding) {
//                return when (view.viewType) {
//                    ViewType.EDITTEXT -> {
//                        val tmp = value as EditText
//                        tmp.text.toString().toInt()
//                    }
//                    else -> throw Exception("Unknown type")
//                }
//            }
//        }
//
//        @BindingAdapter("value","valueAttrChanged")
//        @JvmStatic
//        fun setValue(view: RequestFieldView, value: String, attrChange: InverseBindingListener) {
//            val tmp = view.binding.value as EditText
//            tmp.setText(value)
//            attrChange.onChange()
//        }
//    }
//}
