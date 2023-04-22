package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
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
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profile: Profile
    private lateinit var dao: FirebaseProfessionalSummaryDAOImpl
    private lateinit var professionalSummary: ProfessionalSummary
    private lateinit var progressDialog: ProgressDialog
    private lateinit var editBinding: DialogueProfileEditBinding
    private lateinit var editDialog: AlertDialog

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
        profile = if(requireArguments().containsKey(IntentBundles.Profile)){
            requireArguments().getParcelable(IntentBundles.Profile)!!
        }else{
            Profile() //TODO: Throw Error
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
        progressDialog = ProgressDialog(requireContext())
        editBinding = DialogueProfileEditBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(editBinding.root)
        editDialog = builder.create()
        editDialog.setCancelable(false)
        with(editBinding.editButtons){
            btnSave.visibility = View.GONE
            btnUpdate.visibility = View.VISIBLE
            btnUpdate.setOnClickListener {
                val updatedProfile = profile.exportFirebaseProfile()
                val updatedProfessionalSummary = professionalSummary.copy()
                updatedProfessionalSummary.setSummary(professionalSummary)
                with(editBinding){
                    updatedProfile.profession = profession.text.toString().trim()
                    updatedProfessionalSummary.profileSummary = profileSummary.text.toString().trim()
                }
                val updatedProfileFields: HashMap<String, Any?> = FormControls().getModified(profile.exportFirebaseProfile(), updatedProfile)
                val updatedSummaryFields: HashMap<String, Any?> = FormControls().getModified(professionalSummary, updatedProfessionalSummary)
                if (updatedProfileFields.isNotEmpty() || updatedSummaryFields.isNotEmpty()){
                    progressDialog.show()
                    lifecycleScope.launch {
                        if (dao.updateProfile(profile, updatedProfileFields) && dao.updateProfessionalSummary(professionalSummary, updatedSummaryFields)){
                            profile.importFirebaseProfile(updatedProfile)
                            professionalSummary = updatedProfessionalSummary
                            setProfileDetails()
                            setSummaryDetails()
//                            requireActivity().startActivity(requireActivity().intent)
//                            requireActivity().finish()
                            (requireActivity() as ProfileActivity).updateProfile()
                            Toast.makeText(requireContext(), "Profile and Summary updated", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "Error Updating Profile and Summary", Toast.LENGTH_SHORT).show()
                        }
                        editDialog.dismiss()
                        progressDialog.dismiss()
                    }
                }else{
                    Toast.makeText(requireContext(), "No fields have been changed", Toast.LENGTH_SHORT).show()
                }
            }
            btnCancel.setOnClickListener {
                editDialog.dismiss()
            }
        }

        with(binding.btnEdit){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
                editBinding.profession.setText(profile.profession)
                editBinding.profileSummary.setText(professionalSummary.profileSummary)

                editDialog.show()
            }
        }
    }

    private fun setProfileDetails(){
        binding.address.text = profile.contactInformation?.address?.localAddress()
        binding.email.text = profile.contactInformation?.emailAddress?.email
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
    }
}