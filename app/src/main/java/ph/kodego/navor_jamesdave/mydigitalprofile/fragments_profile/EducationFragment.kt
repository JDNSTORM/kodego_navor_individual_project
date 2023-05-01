package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

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
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVEducationsAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FabListAddBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.EducationEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseEducationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

class EducationFragment : Fragment() {
    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    private lateinit var profile: Profile
    private val educations: ArrayList<Education> = ArrayList()
    private lateinit var dao: FirebaseEducationDAOImpl
    private lateinit var rvAdapter: RVEducationsAdapter
    private lateinit var editDialog: EducationEditDialog

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Education")
            putInt("TabIcon", R.drawable.ic_education_24)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments().containsKey(IntentBundles.Profile)){
            profile = requireArguments().getParcelable(IntentBundles.Profile)!!
        }else{
            Toast.makeText(requireContext(), "No Profile Selected!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        dao = FirebaseEducationDAOImpl(profile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        binding.loadData()
        lifecycleScope.launch {
            educations.addAll(dao.getEducations())
            rvAdapter = RVEducationsAdapter(educations)

            if(Firebase.auth.currentUser?.uid == profile.uID) {
                attachEditingInterface()
            }

            binding.listEducation.layoutManager = LinearLayoutManager(requireContext())
            binding.listEducation.adapter = rvAdapter
            binding.showData()
        }
    }

    private fun attachEditingInterface() {
        editDialog = EducationEditDialog(requireContext(), dao, rvAdapter)
        rvAdapter.editDialog = editDialog

        val fabListAddBinding = FabListAddBinding.inflate(layoutInflater)
        binding.root.addView(fabListAddBinding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        fabListAddBinding.btnAdd.setOnClickListener {
            editDialog.show()
        }
    }
}