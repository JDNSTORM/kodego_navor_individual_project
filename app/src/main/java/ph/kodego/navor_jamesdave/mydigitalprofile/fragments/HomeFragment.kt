package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvAdapter: RVProfilesAdapter
    private val profiles: ArrayList<Profile> = ArrayList()
    private val shownProfiles: ArrayList<Profile> = ArrayList()
    private lateinit var dao: FirebaseProfileDAO

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Home") //TODO: Set as String Resource as it is passed around
            putInt("TabIcon", R.drawable.ic_home_24)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = FirebaseProfileDAOImpl(requireContext())
        setupRecyclerView()
        binding.searchBox.addTextChangedListener {
            if (it?.length!! > 3){
                searchKeyword(it.toString())
            }else{
                resetRecyclerView()
            }
        }
    }

    private fun setupRecyclerView(){
        binding.loadData()
        CoroutineScope(IO).launch{
            launch {
                profiles.addAll(dao.getProfiles())
                shownProfiles.addAll(profiles)
            }.join()
            withContext(Main) {
                rvAdapter = RVProfilesAdapter(shownProfiles)
                binding.listProfiles.layoutManager = LinearLayoutManager(context)
                binding.listProfiles.adapter = rvAdapter
                binding.showData()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetRecyclerView(){
        shownProfiles.clear()
        shownProfiles.addAll(profiles)
        rvAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchKeyword(keyword: String){
        shownProfiles.clear()
        shownProfiles.addAll(
            profiles.filter { profile ->
                profile.profession.contains(keyword, true) or profile.fullName().contains(keyword, true)
            }
        )
        rvAdapter.notifyDataSetChanged()
    }
}