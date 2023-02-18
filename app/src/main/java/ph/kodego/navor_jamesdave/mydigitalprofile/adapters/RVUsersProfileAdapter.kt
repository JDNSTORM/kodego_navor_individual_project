package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.UsersProfile

class RVUsersProfileAdapter(private val usersProfiles: ArrayList<UsersProfile>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersProfiles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderProfileBinding
        val userProfile = usersProfiles[position]
        binding.profilePicture.setImageResource(userProfile.profilePicture)
        binding.profileUserName.text = "${userProfile.firstName} ${userProfile.lastName}"
        binding.profession.text = userProfile.profession

        binding.root.setOnClickListener {
            val intent = Intent(it.context, ProfileActivity::class.java)
            intent.putExtra("User", userProfile)
            startActivity(it.context, intent, null)
        }
    }
}