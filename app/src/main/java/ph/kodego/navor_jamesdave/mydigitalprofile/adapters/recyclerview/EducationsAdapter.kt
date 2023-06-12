package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education.EducationEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education

class EducationsAdapter(): ItemsAdapter<Education>() {
    private var editDialog: EducationEditDialog<*>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemEducationBinding
        val education = items[position]
        bind(binding, education)

        editDialog?.let {
            binding.root.setOnClickListener { _ -> it.edit(education, items) }
        }
    }

    private fun bind(binding: ItemEducationBinding, education: Education) {
        with(binding) {
            dateEnrolled.text = education.dateEnrolled
            dateGraduated.text = education.dateGraduated
            schoolName.text = education.schoolName
            val address = education.address.streetAddress
            if (address.isNotEmpty()) {
                schoolAddress.text = address
                schoolAddress.visibility = View.VISIBLE
            }
            val website = education.website
            if (website.isNotEmpty()) {
                schoolWebsite.text = website
                schoolWebsite.visibility = View.VISIBLE
            }
            val telephone = education.contactNumber.telephone()
            if (telephone.isNotEmpty()) {
                schoolTelephone.text = telephone
                schoolTelephone.visibility = View.VISIBLE
            }
            degree.text = education.degree
            fieldOfStudy.text = education.fieldOfStudy
        }
    }

    fun enableEditing(dialog: EducationEditDialog<*>){
        editDialog = dialog
    }
}