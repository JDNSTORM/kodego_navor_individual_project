package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillsMainAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSkillsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

/**
 *  TODO: Skills Editing Structure
 *      If Main Category has no SubCategory but has Skills, do not Add SubCategory. *Main Category must have SubCategory to organize Skills.
 *  Main Category
 *      ArrayList(SubCategory
 *          ArrayList(Skills)
 *      )
 *  For User:
 *  TODO: Upon clicking on Category ViewHolder, Invoke Category to Activity
 *
 *  TODO: Review CustomView clicked
 */
class SkillsFragment : Fragment() {
    private var _binding: FragmentSkillsBinding? = null
    private val binding get() = _binding!!
    private val skills: ArrayList<SkillMainCategory> = ArrayList()
    private lateinit var rvAdapter: RVSkillsMainAdapter

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Skills")
            putInt("TabIcon", R.drawable.ic_skills_24)
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
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        skills.addAll(getSkills())

        rvAdapter = RVSkillsMainAdapter(skills)

        rvAdapter.setAdapterActions(object: RVSkillsMainAdapter.AdapterActions{
            override fun holderClickNotify(position: Int) {
                Snackbar.make(view, "Adapter Clicked at $position", Snackbar.LENGTH_SHORT).show()
            }

        })

        binding.listSkills.layoutManager = LinearLayoutManager(requireContext())
        binding.listSkills.adapter = rvAdapter
    }

    private fun getSkills(): ArrayList<SkillMainCategory>{
        val skills: ArrayList<SkillMainCategory> = ArrayList()

        for (num in 0..2){
            val skill = SkillMainCategory(num, "Main $num")
            for (num2 in 0..2){
                val skillSub = SkillSubCategory(num2, num, "Sub $num2")
                for (num3 in 0..3){
                    val skill = Skill(num3, num2, "Skill $num3")
                    skillSub.skills.add(skill)
                }
                skill.subCategories.add(skillSub)
            }
            skills.add(skill)
        }
        return skills
    }
}