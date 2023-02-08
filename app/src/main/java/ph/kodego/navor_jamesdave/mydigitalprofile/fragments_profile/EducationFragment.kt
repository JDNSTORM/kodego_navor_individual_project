package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding

class EducationFragment : Fragment() {
    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!

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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }
}