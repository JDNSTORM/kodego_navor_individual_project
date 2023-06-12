package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CareerEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career

class CareersAdapter(): ItemsAdapter<Career>() {
    private var drag: Boolean = false
    private var editDialog: CareerEditDialog<*>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCareerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemCareerBinding
        val career = items[position]
        bind(binding, career)

        editDialog?.let {
            binding.root.setOnClickListener { _ -> it.edit(career, items) }
        }
    }

    private fun bind(binding: ItemCareerBinding, career: Career){
        with(binding) {
            position.text = career.position
            companyName.text = career.companyName
            if (!drag) {
                employmentPeriod.text = career.employmentPeriod()
                val address = career.address.streetAddress
                if (address.isNotEmpty()) {
                    companyAddress.text = address
                    companyAddress.visibility = View.VISIBLE
                }
                val website = career.website
                if (website.isNotEmpty()) {
                    companyWebsite.text = website
                    companyWebsite.visibility = View.VISIBLE
                }
                val contactNumber = career.contactNumber.telephone()
                if (contactNumber.isNotEmpty()) {
                    companyTelephone.text = contactNumber
                    companyTelephone.visibility = View.VISIBLE
                }
                if (career.jobDescription.isNotEmpty()) {
                    jobDescription.text = career.jobDescription
                    jobDescription.visibility = View.VISIBLE
                }
            }else{
                handle.visibility = View.VISIBLE
                employmentPeriod.visibility = View.GONE
                handle.setOnClickListener{}
            }
        }
    }

    fun enableEditing(dialog: CareerEditDialog<*>){
        editDialog = dialog
    }

    fun toggleDrag(): Boolean{
        drag = !drag
        notifyItemRangeChanged(0, itemCount)
        return drag
    }
}