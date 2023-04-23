package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.EducationEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education

class RVEducationsAdapter(private val educations: ArrayList<Education>): RecyclerView.Adapter<ViewHolder>() {
    var editDialog: EducationEditDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = educations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderEducationBinding
        val education = educations[position]
        binding.bind(education)
        with(binding){
            if (editDialog != null){
                root.setOnClickListener { editDialog!!.show(education, holder) }
            }
        }
    }

    fun addEducation(education: Education) {
        educations.add(education)
        notifyItemInserted(itemCount - 1)
    }

    fun updateEducation(education: Education, newEducation: Education, holder: ViewHolder){
        val index = educations.indexOf(education)
        educations[index] = newEducation
        notifyItemChanged(holder.adapterPosition)
    }

    fun deleteEducation(education: Education, holder: ViewHolder){
        educations.remove(education)
        notifyItemRemoved(holder.adapterPosition)
    }
}