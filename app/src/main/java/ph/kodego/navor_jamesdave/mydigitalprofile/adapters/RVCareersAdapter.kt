package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career

class RVCareersAdapter(private val careers: ArrayList<Career>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderCareerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = careers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderCareerBinding
        val career = careers[position]
        with(binding){
            this.position.text = career.position
            employmentPeriod.text = career.employmentStart + " - " + career.employmentEnd
            companyName.text = career.companyName
            val address = career.contactInformation?.address?.completeAddress()
            if (!address.isNullOrEmpty()) {
                companyAddress.text = address
                companyAddress.visibility = View.VISIBLE
            }
            val website = career.contactInformation?.website?.website
            if (!website.isNullOrEmpty()) {
                companyWebsite.text = website
                companyWebsite.visibility = View.VISIBLE
            }
            val contactNumber = career.contactInformation?.contactNumber
            if (contactNumber != null && contactNumber.telephone().isNotEmpty()){
                companyTelephone.text = contactNumber.telephone()
                companyTelephone.visibility = View.VISIBLE
            }
            if (career.jobDescription.isNotEmpty()){
                jobDescription.text = career.jobDescription
                jobDescription.visibility = View.VISIBLE
            }
            root.setOnClickListener {  }
        }
    }
}