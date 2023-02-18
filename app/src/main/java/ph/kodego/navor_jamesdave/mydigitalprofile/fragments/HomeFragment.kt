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
import ph.kodego.navor_jamesdave.mydigitalprofile.models.UsersProfile

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvUsersProfileAdapter: RVUsersProfileAdapter
    private val usersProfiles: ArrayList<UsersProfile> = ArrayList()

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Home")
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
        usersProfiles.addAll(getProfiles())
        rvUsersProfileAdapter = RVUsersProfileAdapter(usersProfiles)
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

    private fun getProfiles(): ArrayList<UsersProfile>{
        val usersProfiles = ArrayList<UsersProfile>()
        usersProfiles.add(
            UsersProfile(
                id = 22,
                profilePicture = R.drawable.navor_james,
                firstName = "James Dave",
                lastName = "Navor",
                profession = "Mobile App Developer"
            )
        )
        for (num in 0 until 10){
            usersProfiles.add(
                UsersProfile(
                    firstName = "User",
                    lastName = "$num",
                    profession = "Profession $num"
                )
            )
        }
        return usersProfiles
    }
}