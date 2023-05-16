package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

class RVProfilesAdapter(private val profiles: ArrayList<Profile>): ItemsAdapter(profiles) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as ViewholderProfileBinding
        val profile = profiles[position]

        binding.root.setOnClickListener {
            val intent = Intent(it.context, ProfileActivity::class.java)
            intent.putExtra(IntentBundles.Profile, profile)
            startActivity(it.context, intent, null)
        }
    }
}