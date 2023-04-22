package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVCareersAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueCareerEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FabListAddBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Website
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.clear

/**
 * TODO:
 *  Firebase DAO
 *  Editing Interface
 */
class CareerFragment : Fragment() {
    private var _binding: FragmentCareerBinding? = null
    private val binding get() = _binding!!
    private lateinit var profile: Profile
    private val careers: ArrayList<Career> = ArrayList()
    private lateinit var dao: FirebaseCareerDAOImpl
    private lateinit var rvAdapter: RVCareersAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var editDialog: AlertDialog
    private lateinit var editBinding: DialogueCareerEditBinding
    private lateinit var fabListAddBinding: FabListAddBinding

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Career")
            putInt("TabIcon", R.drawable.ic_work_history_24)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profile = if (requireArguments().containsKey(IntentBundles.Profile)){
            requireArguments().getParcelable<Profile>(IntentBundles.Profile)!!
        }else{
            Profile() // TODO: Throw Error
        }
        dao = FirebaseCareerDAOImpl(profile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCareerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        lifecycleScope.launch {
            careers.addAll(dao.getCareers())
            rvAdapter = RVCareersAdapter(careers)

            binding.listCareer.layoutManager = LinearLayoutManager(requireContext())
            binding.listCareer.adapter = rvAdapter

            if(Firebase.auth.currentUser?.uid == profile.uID) {
                attachEditingInterface()
                progressDialog = ProgressDialog(requireContext())
            }
        }
    }

    private fun attachEditingInterface() {
//        TODO("Not yet implemented")
        fabListAddBinding = FabListAddBinding.inflate(layoutInflater)
        binding.root.addView(fabListAddBinding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        editBinding = DialogueCareerEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(editBinding.root)
        builder.setCancelable(false)
        editDialog = builder.create()
        editDialog.setOnDismissListener {
            editBinding.clear()
        }

        with(editBinding.editButtons) {
            btnCancel.setOnClickListener {
                editDialog.dismiss()
            }
            btnSave.setOnClickListener {
                val career = Career(profile.profileID)
                with(editBinding){
                    career.employmentStart = dateEmployed.text.toString().trim()
                    career.employmentEnd = employmentEnd.text.toString().trim()
                    career.position = position.text.toString().trim()
                    career.companyName = company.text.toString().trim()
                    val contactInformation = ContactInformation()
                    val address = Address()
                    address.streetAddress = streetAddress.text.toString().trim()
                    address.subdivision = subdivision.text.toString().trim()
                    address.cityOrMunicipality = city.text.toString().trim()
                    address.zipCode = zipCode.text.toString().toIntOrNull() ?: 0
                    address.province = province.text.toString().trim()
                    address.country = country.text.toString().trim()
                    contactInformation.address = address
                    val companyWebsite = companyWebsite.text.toString().trim()
                    if (companyWebsite.isNotEmpty()){
                        val website = Website()
                        website.website = companyWebsite
                        contactInformation.website = website
                    }
                    val telephone = ContactNumber()
                    telephone.areaCode = layoutContactEdit.telAreaCode.text.toString().trim()
                    telephone.contact = layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
                    contactInformation.contactNumber = telephone
                    career.contactInformation = contactInformation
                    career.jobDescription = jobDescription.text.toString()
                }
                addCareer(career)
                editDialog.dismiss()
            }
        }

        fabListAddBinding.btnAdd.setOnClickListener {
            editDialog.show()
            editBinding.dateEmployed.requestFocus()
        }
    }
    private fun addCareer(career: Career){
        progressDialog.show()
        lifecycleScope.launch {
            if(dao.addCareer(career)){
                careers.add(career)
                rvAdapter.notifyItemInserted(rvAdapter.itemCount - 1)
            }else{
                Toast.makeText(requireContext(), "Error Adding Career", Toast.LENGTH_SHORT).show()
            }
            progressDialog.dismiss()
        }
    }
}