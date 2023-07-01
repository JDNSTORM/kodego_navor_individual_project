package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.flow.StateFlow
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemAccountProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class AccountProfilesAdapter(
    private val action: (ProfileAction) -> StateFlow<RemoteState>?,
    private val dismiss: () -> Unit,
    ): ItemsAdapter<Profile>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccountProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemAccountProfileBinding
        val profile = items[position]
        binding.bind(profile)
        binding.apply {
            root.setOnClickListener { setActiveProfile(profile) }
            btnPublic.setOnClickListener { updateVisibility(profile) }
            btnDelete.setOnClickListener { openDeleteDialog(it.context, profile) }
        }
    }

    private fun openDeleteDialog(context: Context, profile: Profile) {
        object: DeleteItemDialog(context, profile){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                action(ProfileAction.Delete(profile))
                dialog.dismiss()
            }
        }.show()
    }

    private fun setActiveProfile(profile: Profile){
        action(ProfileAction.Select(profile))
        dismiss()
    }

    private fun ItemAccountProfileBinding.bind(profile: Profile){
        profession.text = profile.profession
        if (profile.isPublic){
            btnPublic.setImageResource(R.drawable.ic_visibility_24)
        }else{
            btnPublic.setImageResource(R.drawable.ic_visibility_off_24)
        }
    }

    private fun updateVisibility(profile: Profile){
        val changes = mapOf<String, Any?>(Profile.KEY_IS_PUBLIC to !profile.isPublic)
        val publicProfiles = with(ArrayList(items)) {
            remove(profile)
            filter { it.isPublic }
        }
        val unpublish = mapOf<String, Any?>(Profile.KEY_IS_PUBLIC to false)
        if (!profile.isPublic) {
            publicProfiles.forEach {
                action(ProfileAction.Update(it, unpublish))
            }
        }
        action(ProfileAction.Update(profile, changes))
    }
}