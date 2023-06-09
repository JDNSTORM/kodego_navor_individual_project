package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}