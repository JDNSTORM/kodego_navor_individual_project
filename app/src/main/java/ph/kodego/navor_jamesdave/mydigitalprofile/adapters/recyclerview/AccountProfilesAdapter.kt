package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.SelectProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class AccountProfilesAdapter(private val viewModel: ProfileViewModel, private val dialog: SelectProfileDialog<*>): ItemsAdapter<Profile>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemProfileBinding
        val profile = items[position]
        bind(binding, profile)
        binding.root.setOnClickListener { setActiveProfile(profile) }
    }

    private fun setActiveProfile(profile: Profile){
        viewModel.setActiveProfile(profile)
        dialog.dismiss()
    }

    private fun bind(binding: ItemProfileBinding, profile: Profile){
        with(binding) {
            profileUserName.text = profile.displayName()
            profession.text = profile.profession
            GlideModule().loadProfilePhoto(profilePicture, profile.image)
        }
    }
}