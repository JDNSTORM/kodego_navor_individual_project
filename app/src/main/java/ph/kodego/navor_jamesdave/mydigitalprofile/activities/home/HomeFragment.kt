package ph.kodego.navor_jamesdave.mydigitalprofile.activities.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo

@AndroidEntryPoint
class HomeFragment(): ViewPagerFragment<FragmentHomeBinding>() {
    override fun getTabInformation(): TabInfo = TabInfo(
        "Home",
        R.drawable.ic_home_24
    )
//    private val viewModel by lazy{
//        ViewModelProvider(viewLifecycleOwner)[] //TODO
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
}