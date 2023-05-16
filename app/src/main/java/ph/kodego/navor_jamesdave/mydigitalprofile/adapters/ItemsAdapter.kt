package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

/**
 * A Parent Class designed as a Universal RecyclerView Adapter
 */
abstract class ItemsAdapter(private val items: ArrayList<*>): RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]
        bindItem(binding, item)
    }

    /**
     * This function binds the Item to its designated ViewBinding and must be continuously modified.
     * ViewBinding and Item must be matched accordingly.
     */
    private fun bindItem(binding: ViewBinding, item: Any){
        if (binding is ViewholderProfileBinding && item is Profile){
            binding.bind(item)
        }else if (binding is ViewholderCareerBinding && item is Career){
            binding.bind(item)
        }else if (binding is ViewholderEducationBinding && item is Education){
            binding.bind(item)
        }
    }
}