package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog

/**
 * TODO:
 *  Firebase DAO
 *  Editing Interface
 */
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
        dao = FirebaseProfessionalSummaryDAOImpl(profile)
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
        setSummaryDetails()
    }

    private fun attachEditingInterface() {
        editBinding = DialogueProfileEditBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(editBinding.root)
        editDialog = builder.create()
        editDialog.setCancelable(false)
        with(editBinding.editButtons){
            btnSave.visibility = View.GONE
            btnUpdate.visibility = View.VISIBLE
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

        if(Firebase.auth.currentUser?.uid == profile.uID) {
            attachEditingInterface()
            progressDialog = ProgressDialog(requireContext())
        }
    }
    private fun setSummaryDetails(){
        lifecycleScope.launch{
            val summary = dao.getProfessionalSummary()
            if (summary != null){
                professionalSummary = summary
            }else{
                professionalSummary = ProfessionalSummary(profile.profileID)
                dao.addProfessionalSummary(professionalSummary)
            }
            binding.professionalSummary.text = professionalSummary.profileSummary
        }
    }
}