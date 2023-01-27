package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding

class AccountInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}