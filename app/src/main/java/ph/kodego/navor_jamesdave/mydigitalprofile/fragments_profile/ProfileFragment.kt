package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProfileEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profile: Profile
    private lateinit var dao: FirebaseProfessionalSummaryDAOImpl
    private lateinit var professionalSummary: ProfessionalSummary
    private lateinit var editDialog: ProfileEditDialog

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Profile")
            putInt("TabIcon", R.drawable.ic_account_circle_24)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(requireArguments().containsKey(IntentBundles.Profile)){
            profile = requireArguments().getParcelable(IntentBundles.Profile)!!
        }else{
            Toast.makeText(requireContext(), "No Profile Selected!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        dao = FirebaseProfessionalSummaryDAOImpl(profile, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProfileDetails()
        getSummary()
        if(Firebase.auth.currentUser?.uid == profile.uID) {
            attachEditingInterface()
        }
    }

    private fun attachEditingInterface() {
        editDialog = ProfileEditDialog(requireContext(), dao)
        editDialog.setOnDismissListener {
            if (editDialog.updated){
                profile = editDialog.profile
                professionalSummary = editDialog.professionalSummary
                setProfileDetails()
                setSummaryDetails()
            }
        }

        with(binding.btnEdit){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
                editDialog.show(profile, professionalSummary)
            }
        }
    }

    private fun setProfileDetails(){
        (requireActivity() as ProfileActivity).updateProfile()
        binding.address.text = profile.contactInformation?.address?.localAddress()
        val email = profile.contactInformation!!.emailAddress!!
        Log.d("Email", "${email.id}, ${email.contactInformationID}, ${email.email}")
        binding.email.text = profile.contactInformation!!.emailAddress!!.email
    }
    private fun getSummary(){
        lifecycleScope.launch{
            val summary = dao.getProfessionalSummary()
            if (summary != null){
                professionalSummary = summary
            }else{
                professionalSummary = ProfessionalSummary(profile.profileID)
                dao.addProfessionalSummary(professionalSummary)
            }
            setSummaryDetails()
        }
    }
    private fun setSummaryDetails(){
        binding.professionalSummary.text = professionalSummary.profileSummary
        binding.professionalSummary.visibility = View.VISIBLE
        binding.loadingData.visibility = View.GONE
    }
}