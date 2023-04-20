package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVCareersAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FabListAddBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog

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
        setupRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCareerBinding.inflate(inflater, container, false)
        return binding.root
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
    }
}