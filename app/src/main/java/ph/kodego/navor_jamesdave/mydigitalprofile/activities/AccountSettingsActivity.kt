package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}