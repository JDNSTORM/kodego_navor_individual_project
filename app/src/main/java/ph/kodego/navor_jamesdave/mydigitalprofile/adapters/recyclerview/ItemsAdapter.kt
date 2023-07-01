package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ItemsAdapter<T>: RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    protected var items: List<T> = emptyList()
    private val updateCallback by lazy { AdapterListUpdateCallback(this) }

    class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = items.size

    fun setList(newItems: List<T>){
        val callback = getDiffUtilCallback(items, newItems)
        val result = DiffUtil.calculateDiff(callback)
        items = newItems
        result.dispatchUpdatesTo(updateCallback)
    }

    protected open fun getDiffUtilCallback(oldItems: List<T>, newItems: List<T>): DiffUtil.Callback {
        return ItemsDiffCallback(oldItems, newItems)
    }
}