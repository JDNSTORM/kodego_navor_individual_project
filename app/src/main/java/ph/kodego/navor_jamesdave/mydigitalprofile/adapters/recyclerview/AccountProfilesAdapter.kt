package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
    override fun getDiffUtilCallback(
        oldItems: List<Profile>,
        newItems: List<Profile>
    ): DiffUtil.Callback {
        return object: DiffUtil.Callback(){
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].profileID == newItems[newItemPosition].profileID
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        }
    }

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
        DeleteItemDialog(context, profile){
            action(ProfileAction.Delete(profile))
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
                action(ProfileAction.Update(unpublish, it))
            }
        }
        action(ProfileAction.Update(changes, profile))
    }
}