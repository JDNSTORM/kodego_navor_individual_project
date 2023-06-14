package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile.SelectProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemAccountProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class AccountProfilesAdapter(private val viewModel: ProfileViewModel, private val dialog: SelectProfileDialog<*>): ItemsAdapter<Profile>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccountProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemAccountProfileBinding
        val profile = items[position]
        bind(binding, profile)
        binding.apply {
            root.setOnClickListener { setActiveProfile(profile) }
            btnPublic.setOnClickListener { updateVisibility(profile) }
            btnDelete.setOnClickListener { openDeleteDialog(it.context, profile) }
        }
    }

    private fun openDeleteDialog(context: Context, profile: Profile) {
        object: DeleteItemDialog(context, profile){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                deleteProfile(profile)
                dialog.dismiss()
            }
        }.show()
    }

    private fun setActiveProfile(profile: Profile){
        viewModel.setActiveProfile(profile)
        dialog.dismiss()
    }

    private fun bind(binding: ItemAccountProfileBinding, profile: Profile){
        with(binding) {
            profession.text = profile.profession
            if (profile.isPublic){
                btnPublic.setImageResource(R.drawable.ic_visibility_24)
            }else{
                btnPublic.setImageResource(R.drawable.ic_visibility_off_24)
            }
        }
    }

    private fun updateVisibility(profile: Profile){
        val changes = mapOf<String, Any?>(Profile.KEY_IS_PUBLIC to !profile.isPublic)
        val publicProfiles = with(ArrayList(items)) {
            remove(profile)
            filter { it.isPublic }
        }
        val depublicize = mapOf<String, Any?>(Profile.KEY_IS_PUBLIC to false)
        viewModel.viewModelScope.launch {
            viewModel.updateProfile(profile, changes)
            if (!profile.isPublic) {
                publicProfiles.forEach {
                    viewModel.updateProfile(it, depublicize)
                }
            }
        }
    }

    private fun deleteProfile(profile: Profile){
        viewModel.viewModelScope.launch {
            viewModel.deleteProfile(profile)
        }
    }
}