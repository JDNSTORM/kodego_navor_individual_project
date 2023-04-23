package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.CareerEditDialog
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
    fun updateCareer(career: Career, newCareer: Career, holder: ViewHolder){
        val index = careers.indexOf(career)
        Log.d("Index", index.toString())
        Log.d("Career", career.toString())
        Log.d("NewCareer", newCareer.toString())
        careers[index] = newCareer
        notifyItemChanged(holder.adapterPosition)
    }

    fun deleteCareer(career: Career, holder: ViewHolder) {
        careers.remove(career)
        notifyItemRemoved(holder.adapterPosition)
    }
}