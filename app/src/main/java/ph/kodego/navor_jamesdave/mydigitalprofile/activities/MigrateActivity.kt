package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMigrateBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseEducationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class MigrateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMigrateBinding.inflate(layoutInflater) }
    private val profileViewModel: ProfileViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val dao by lazy { FirebaseProfileDAOImpl(this) }
    private lateinit var profile: Profile
    private val summaryDAO by lazy { FirebaseProfessionalSummaryDAOImpl(profile, this) }
    private val careerDAO by lazy { FirebaseCareerDAOImpl(profile) }
    private val educationDAO by lazy { FirebaseEducationDAOImpl(profile) }
    private val skillsDAO by lazy { FirebaseSkillsMainCategoryDAOImpl(profile.profileID) }
    private val progressDialog by lazy { ProgressDialog(this, ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}