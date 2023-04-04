package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvUsersProfileAdapter: RVProfilesAdapter
    private val profiles: ArrayList<Profile> = ArrayList()

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
        profiles.addAll(getProfilesSample())
        rvUsersProfileAdapter = RVProfilesAdapter(profiles)
        binding.listProfiles.layoutManager = LinearLayoutManager(context)
        binding.listProfiles.adapter = rvUsersProfileAdapter

        binding.btnSearch.setOnClickListener {

        }
    }

    private fun getProfilesSample(): ArrayList<Profile>{
        val profiles = ArrayList<Profile>()
        val jden = Profile("22", "Mobile App Developer")
//        jden.profilePicture = R.drawable.navor_james
        jden.uID = "22"
        jden.firstName = "James Dave"
        jden.lastName = "Navor"
        jden.contactInformation = ContactInformation("22")
        jden.contactInformation
        jden.contactInformation!!.emailAddress = EmailAddress("22", "22", "esteban.dave999@gmail.com")
        profiles.add(jden)
        profiles.add(Profile())
//        profileDatas.add(
//            ProfileData(
//                user = User(
//                    id = 22,
//                    accountID = "22",
//                    profilePicture = R.drawable.navor_james,
//                    firstName = "James Dave",
//                    lastName = "Navor",
//                    contactInformationID = ""
//                ),
//                profile = Profile(
//                    id = 22,
//                    userID = 22,
//                    profession = "Mobile App Developer"
//                )
//            )
//        )
//        for (num in 0L until 10L){
//            profileDatas.add(
//                ProfileData(
//                    user = User(
//                        id = num,
//                        accountID = "$num",
//                        firstName = "User",
//                        lastName = "$num",
//                        contactInformationID = ""
//                    ),
//                    profile = Profile(
//                        id = num,
//                        userID = num,
//                        profession = "Profession $num"
//                    )
//                )
//            )
//        }
        return profiles
    }
}