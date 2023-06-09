package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ItemsAdapter<T>: RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    protected var items: List<T> = emptyList()
    class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = items.size

    fun setList(newItems: List<T>){
        val callback = ItemsDiffCallback(items, newItems)
        val result = DiffUtil.calculateDiff(callback)
        items = newItems
        result.dispatchUpdatesTo(this)
    }
}