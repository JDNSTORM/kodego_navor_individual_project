package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfileData

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
        val profile = profileDatas[position]
        binding.profilePicture.setImageResource(profile.user.profilePicture)
        binding.profileUserName.text = "${profile.user.firstName} ${profile.user.lastName}"
        binding.profession.text = profile.profession

        binding.root.setOnClickListener {
            val intent = Intent(it.context, ProfileActivity::class.java)
            intent.putExtra("User", profile)
            startActivity(it.context, intent, null)
        }
    }
}