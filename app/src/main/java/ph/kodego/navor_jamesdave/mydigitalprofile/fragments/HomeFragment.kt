package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVUsersProfileAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvUsersProfileAdapter: RVUsersProfileAdapter
    private val profileDatas: ArrayList<ProfileData> = ArrayList()

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
        profileDatas.addAll(getProfiles())
        rvUsersProfileAdapter = RVUsersProfileAdapter(profileDatas)
        binding.listProfiles.layoutManager = LinearLayoutManager(context)
        binding.listProfiles.adapter = rvUsersProfileAdapter

        binding.btnSearch.setOnClickListener {
//            goToProfile()
        }
    }

    private fun goToProfile(){
        val intent = Intent(context, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun getProfiles(): ArrayList<ProfileData>{
        val profileDatas = ArrayList<ProfileData>()
        profileDatas.add(
            ProfileData(
                id = 22,
                user = UserData(
                    id = 22,
                    accountID = 22,
                    profilePicture = R.drawable.navor_james,
                    firstName = "James Dave",
                    lastName = "Navor",
                    contactInformation = ContactInformation(
                        emailAddress = EmailAddress(
                            contactInformationID = 22,
                            username = "esteban.dave999",
                            domain = "gmail.com"
                        )
                    )
                ),
                profession = "Mobile App Developer",
                profileSummary = ""
            )
        )
        for (num in 0L until 10L){
            profileDatas.add(
                ProfileData(
                    id = num,
                    user = UserData(
                        id = num,
                        accountID = num,
                        firstName = "User",
                        lastName = "$num",
                        contactInformation = ContactInformation(
                            emailAddress = EmailAddress(
                                contactInformationID = 22,
                                username = "user$num",
                                domain = "email.email"
                            )
                        )
                    ),
                    profession = "Profession $num",
                    profileSummary = ""
                )
            )
        }
        return profileDatas
    }
}