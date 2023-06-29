package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@Deprecated("Used Paging Adapter Instead")
class ProfilesAdapter(private val viewModel: ProfileViewModel): ItemsAdapter<Profile>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemProfileBinding
        val profile = items[position]
        binding.bind(profile)
        binding.root.setOnClickListener { viewProfile(it.context, profile) }
    }

    private fun viewProfile(context: Context, profile: Profile){
        viewModel.setActiveProfile(profile)
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }

    private fun ItemProfileBinding.bind(profile: Profile){
        profileUserName.text = profile.displayName()
        profession.text = profile.profession
        GlideModule().loadProfilePhoto(profilePicture, profile.image)
    }
}