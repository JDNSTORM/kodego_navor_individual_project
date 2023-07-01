package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule

class ProfilePagingAdapter(
    private val view: (Profile) -> Unit
): PagingDataAdapter<Profile, ItemsAdapter.ViewHolder>(DiffCallBack) {

    companion object{
        private val DiffCallBack = object: DiffUtil.ItemCallback<Profile>(){
            override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int) {
        val binding = holder.binding as ItemProfileBinding
        val profile = getItem(position)
        profile?.let {
            binding.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        return ItemsAdapter.ViewHolder(
            ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private fun ItemProfileBinding.bind(profile: Profile){
        profileUserName.text = profile.displayName()
        profession.text = profile.profession
        GlideModule().loadProfilePhoto(profilePicture, profile.image)

        root.setOnClickListener { view(profile) }
    }
}