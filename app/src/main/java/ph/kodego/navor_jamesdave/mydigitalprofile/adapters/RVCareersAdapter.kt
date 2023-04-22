package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.CareerEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.bind

class RVCareersAdapter(private val careers: ArrayList<Career>): RecyclerView.Adapter<ViewHolder>() {
    var editDialog: CareerEditDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderCareerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = careers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderCareerBinding
        val career = careers[position]
        binding.bind(career)
        with(binding){
            if (editDialog != null) {
                root.setOnClickListener {
                    editDialog!!.show(career, holder)
                }
            }
        }
    }
    fun addCareer(career: Career){
        careers.add(career)
        notifyItemInserted(itemCount - 1)
    }
}