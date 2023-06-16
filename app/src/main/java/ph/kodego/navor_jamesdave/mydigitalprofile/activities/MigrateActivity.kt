package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMigrateBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseEducationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@Deprecated("Data Migration Complete. This Activity will not be necessary")
@AndroidEntryPoint
class MigrateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMigrateBinding.inflate(layoutInflater) }
    private val profileViewModel: ProfileViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var profiles: ArrayList<Profile>
    private lateinit var profile: Profile
    private lateinit var summary: ProfessionalSummary
    private lateinit var careers: ArrayList<Career>
    private lateinit var educations: ArrayList<Education>
    private lateinit var skills: ArrayList<SkillMainCategory>

    private val dao by lazy { FirebaseProfileDAOImpl(this) }
    private val summaryDAO by lazy { FirebaseProfessionalSummaryDAOImpl(profile, this) }
    private val careerDAO by lazy { FirebaseCareerDAOImpl(profile) }
    private val educationDAO by lazy { FirebaseEducationDAOImpl(profile) }
    private val skillsDAO by lazy { FirebaseSkillsMainCategoryDAOImpl(profile.profileID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        retrieveProfiles()
    }

    private fun invalidAccess() {
        Toast.makeText(this, "Invalid Access", Toast.LENGTH_SHORT).show()
        toMain()
    }

    private fun retrieveProfile(uid: String) {
        val progressDialog = ProgressDialog(this, R.string.retrieving_profile).apply { show() }
        lifecycleScope.launch {
            profile = dao.getProfile(uid)
            summary = summaryDAO.getProfessionalSummary() ?: ProfessionalSummary()
            careers = careerDAO.getCareers()
            educations = educationDAO.getEducations()
            skills = skillsDAO.getMainCategories()
            migrateData()
            progressDialog.dismiss()
        }
    }

    private fun retrieveProfiles(){
        val progressDialog = ProgressDialog(this, R.string.retrieving_profiles).apply { show() }
        lifecycleScope.launch {
            profiles = dao.getProfiles()
            progressDialog.dismiss()
            migrateDatas()
        }
    }

    private fun migrateDatas() {
        lifecycleScope.launch {
            profiles.forEach {
                migrateAccount(it.migrateAccount())
                migrateProfile(it)
            }
            Toast.makeText(this@MigrateActivity, "Data Migration Complete!", Toast.LENGTH_SHORT)
                .show()
            toMain()
        }
    }

    private suspend fun migrateAccount(account: Account){
        val progressDialog = ProgressDialog(this, R.string.migrating_account).apply { show() }
//        accountViewModel.addAccount(account.uid, account)
        progressDialog.dismiss()
    }

    private suspend fun migrateProfile(profile: Profile){
        val progressDialog = ProgressDialog(this, R.string.retrieving_profile_data).apply { show() }
        val summary = FirebaseProfessionalSummaryDAOImpl(profile, this).getProfessionalSummary() ?: ProfessionalSummary()
        val careers = FirebaseCareerDAOImpl(profile).getCareers()
        val educations = FirebaseEducationDAOImpl(profile).getEducations()
        val skills = FirebaseSkillsMainCategoryDAOImpl(profile.profileID).getMainCategories()
        progressDialog.dismiss()
        migrateProfile(profile.migrateProfile(summary, careers, skills, educations))
    }

    private suspend fun migrateProfile(profile: ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile){
        val progressDialog = ProgressDialog(this, R.string.migrating_profile).apply { show() }
//        profileViewModel.addProfile(profile.refUID, profile)
        progressDialog.dismiss()
    }

    private fun migrateData() {
        lifecycleScope.launch {
            migrateAccount()
            migrateProfile()
            Toast.makeText(this@MigrateActivity, "Data Migration Complete!", Toast.LENGTH_SHORT)
                .show()
            toMain()
        }
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun migrateAccount(){
        val progressDialog = ProgressDialog(this, R.string.migrating_account).apply { show() }
//        val migration = accountViewModel.addAccount(uid!!, profile.migrateAccount())
//        Log.d("Account Migration", migration.toString())
        progressDialog.dismiss()
    }

    private suspend fun migrateProfile(){
        val progressDialog = ProgressDialog(this, R.string.migrating_profile).apply { show() }
//        val migration = profileViewModel.addProfile(uid!!, profile.migrateProfile(summary, careers, skills, educations))
//        Log.d("Profile Migration", migration.toString())
        progressDialog.dismiss()
    }
}