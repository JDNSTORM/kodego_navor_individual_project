package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class EducationFragment(): ViewPagerFragment<FragmentEducationBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
//    private val itemsAdapter //TODO
//    private val editDialog //TODO

    override fun getTabInformation(): TabInfo = TabInfo(
        "Education",
        R.drawable.ic_education_24
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val refUID = viewModel.readActiveProfile()?.first()?.refUID
            val activeUID = Firebase.auth.currentUser?.uid
            Log.d("RefUID", refUID.toString())
            Log.d("ActiveUID", activeUID.toString())
            if(refUID == activeUID && activeUID != null){
                enableEditing()
            }
            viewModel.readActiveProfile()?.let {
                setupRecyclerView()
                it.collect(this@EducationFragment)
            } ?: noActiveProfile()
        }
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    private fun setupRecyclerView() {
        with(binding.listEducation){
            layoutManager = LinearLayoutManager(requireContext())
//            adapter =
        }
        binding.showData()
    }

    private fun enableEditing() {
        with(binding.btnEdit){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
//                editDialog.show(profile, professionalSummary)
            }
        }
    }

    override suspend fun emit(value: Profile?) {
//        TODO("Not yet implemented")
    }
}