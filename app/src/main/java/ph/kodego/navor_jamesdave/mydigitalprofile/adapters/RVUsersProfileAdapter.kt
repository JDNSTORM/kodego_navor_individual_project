package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfileData
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

class RVUsersProfileAdapter(private val profileDatas: ArrayList<ProfileData>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return profileDatas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderProfileBinding
        val profileData = profileDatas[position]
        binding.profilePicture.setImageResource(profileData.user.profilePicture)
        binding.profileUserName.text = "${profileData.user.firstName} ${profileData.user.lastName}"
        binding.profession.text = profileData.profile.profession

        binding.root.setOnClickListener {
            val intent = Intent(it.context, ProfileActivity::class.java)
            intent.putExtra(Constants.BundleProfileData, profileData)
            startActivity(it.context, intent, null)
        }
    }
}