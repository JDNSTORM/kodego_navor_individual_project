package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillSubAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillsEditAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillsMainAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillSubEditBinding
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
        minimizeFabs()
        skills.addAll(getSkills())

        rvAdapter = RVSkillsMainAdapter(skills)

        /**
         *  If Main Category is Clicked:
         *
         */
        rvAdapter.setAdapterActions(object: RVSkillsMainAdapter.AdapterEvents{ //TODO: Possible error due to using position. Might need to use AdapterPosition
            override fun holderClickNotify(position: Int) {
                Snackbar.make(view, "Adapter Clicked at $position", Snackbar.LENGTH_SHORT).show()
            }

            override fun mainCategoryClick(mainCategories: ArrayList<SkillMainCategory>, position: Int, skillSubAdapter: RVSkillSubAdapter) {
                expandFabs(mainCategories, position, null, skillSubAdapter)
            }

            override fun subCategoryClick(
                mainCategories: ArrayList<SkillMainCategory>,
                mainCategoryPosition: Int,
                subCategoryPosition: Int,
                skillSubAdapter: RVSkillSubAdapter
            ) {
                expandFabs(mainCategories, mainCategoryPosition, subCategoryPosition, skillSubAdapter)
            }

        })

        binding.listSkills.layoutManager = LinearLayoutManager(requireContext())
        binding.listSkills.adapter = rvAdapter

        binding.efabSkillsOptions.setOnClickListener {
            if (binding.efabSkillsOptions.isExtended){
                minimizeFabs()
            }else{
                expandFabs()
            }
        }
        binding.btnAddMainCategory.setOnClickListener {
            editMainCategoryDialogue(skills, null)
        }
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
        skills.add(
            SkillMainCategory(
                4,
                "Main 4",
                arrayListOf(
                    SkillSubCategory(
                        0,
                        4,
                        "",
                        arrayListOf(
                            Skill(0,0,"asdkaj")
                        )
                    )
                )
            )
        )
        skills.add(
            SkillMainCategory(
                5,
                "Main 5"
            )
        )

        return skills
    }

    private fun minimizeFabs(){
        binding.efabSkillsOptions.shrink()
        binding.btnAddMainCategory.hide()
        binding.btnEditMainCategory.hide()
        binding.btnEditMainCategory.isEnabled = false
        binding.labelMainCategory.visibility = View.GONE
        binding.btnAddSubCategory.hide()
        binding.btnAddSubCategory.isEnabled = false
        binding.btnEditSubCategory.hide()
        binding.btnEditSubCategory.isEnabled = false
        binding.labelSubCategory.visibility = View.GONE
    }
    private fun expandFabs(){
        binding.efabSkillsOptions.extend()
        binding.btnAddMainCategory.show()
        binding.btnEditMainCategory.show()
        binding.btnEditMainCategory.isEnabled = false
        binding.labelMainCategory.visibility = View.VISIBLE
        binding.btnAddSubCategory.show()
        binding.btnAddSubCategory.isEnabled = false
        binding.btnEditSubCategory.show()
        binding.btnEditSubCategory.isEnabled = false
        binding.labelSubCategory.visibility = View.VISIBLE
    }

    /**
     *  Has: Nothing
     *      Add MainCategory
     *  Has: MainCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Add SubCategory
     *  Has: SubCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Add SubCategory
     *      Edit SubCategory
     *  Has: Skill but no SubCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Edit SubCategory
     */
    private fun expandFabs(
        mainCategories: ArrayList<SkillMainCategory>,
        mainCategoryPosition: Int,
        subCategoryPosition: Int? = null,
        skillSubAdapter: RVSkillSubAdapter
    ){
        expandFabs()
        binding.btnEditMainCategory.isEnabled = true
        binding.btnAddSubCategory.isEnabled = true
        if(subCategoryPosition != null){
            binding.btnEditSubCategory.isEnabled = true
        }

        binding.btnEditMainCategory.setOnClickListener {
            editMainCategoryDialogue(mainCategories, mainCategoryPosition)
        }
        binding.btnAddSubCategory.setOnClickListener {
            editSubCategoryDialogue(mainCategories[mainCategoryPosition], null, skillSubAdapter)
        }
        binding.btnEditSubCategory.setOnClickListener {
            editSubCategoryDialogue(mainCategories[mainCategoryPosition], subCategoryPosition, skillSubAdapter)
        }
    }

    /**
     *  Add MainCategory if a MainCategory doesn't exist
     *  Update MainCategory if MainCategory exists
     */
    private fun editMainCategoryDialogue(mainCategories: ArrayList<SkillMainCategory> = ArrayList(), mainCategoryPosition: Int? = null){
        val dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillMainEditBinding.root)
        val dialog = builder.create()
        var skillMain: SkillMainCategory? = null

        if (mainCategories.isNotEmpty() && mainCategoryPosition != null){
            skillMain = mainCategories[mainCategoryPosition]
            dialogueSkillMainEditBinding.skillMain.setText(skillMain.categoryMain)
            dialogueSkillMainEditBinding.editButtons.btnSave.visibility = View.GONE
            dialogueSkillMainEditBinding.editButtons.btnUpdate.visibility = View.VISIBLE
        }

        with(dialogueSkillMainEditBinding.editButtons){
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {
                val skillMain = SkillMainCategory(
                    categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                )
                Toast.makeText(context, "Save: ${skillMain.categoryMain}", Toast.LENGTH_SHORT).show()
                mainCategories.add(skillMain)
                rvAdapter.notifyItemInserted(mainCategories.size-1)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                skillMain!!.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                rvAdapter.notifyItemChanged(mainCategoryPosition!!)
                Toast.makeText(context, "Update: ${skillMain.categoryMain}", Toast.LENGTH_SHORT).show()
                minimizeFabs()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    /**
     *
     *  Add SubCategory if a SubCategory doesn't exist
     *      TODO: Adding Skill will automatically add SubCategory
     *  Update SubCategory if SubCategory exists
     */
    private fun editSubCategoryDialogue(
        skillMain: SkillMainCategory,
        subCategoryPosition: Int? = null,
        skillSubAdapter: RVSkillSubAdapter
    ){
        val subCategories = skillMain.subCategories
        val dialogueSkillSubEditBinding = DialogueSkillSubEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillSubEditBinding.root)
        val dialog = builder.create()
        val subCategory: SkillSubCategory = if(subCategoryPosition != null){
            subCategories[subCategoryPosition]
        } else {
            SkillSubCategory(categoryMainID = skillMain.id)
        }
        val skillAdapter = RVSkillsEditAdapter(subCategory.skills)
        dialogueSkillSubEditBinding.listSkill.layoutManager =  LinearLayoutManager(context)
        dialogueSkillSubEditBinding.listSkill.adapter =  skillAdapter

        if (skillAdapter.itemCount > 0){
            dialogueSkillSubEditBinding.svListSkill.visibility = View.VISIBLE
            dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
        }

        dialogueSkillSubEditBinding.labelSkillMain.setText(skillMain.categoryMain)

        with(dialogueSkillSubEditBinding.editButtons){
            if(subCategoryPosition != null){
                dialogueSkillSubEditBinding.skillSub.setText(subCategories[subCategoryPosition].categorySub)
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.VISIBLE
                dialog.setOnDismissListener {
                    skillSubAdapter.notifyItemChanged(subCategoryPosition)
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {// TODO: Add Skill without Subcategory
                /**
                 *
                 *
                 */
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
                Toast.makeText(context, "Save: ${subCategory.categorySub}", Toast.LENGTH_SHORT).show()
                subCategories.add(subCategory)
                skillSubAdapter.notifyItemInserted(skillSubAdapter.itemCount-1)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                val subCategory = subCategories[subCategoryPosition!!]
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
                skillSubAdapter.notifyItemChanged(subCategoryPosition)
                Toast.makeText(context, "Update: Clicked", Toast.LENGTH_SHORT).show()
                minimizeFabs()
                dialog.dismiss()
            }
        }
        /**
         * Adding Skills
         */
        dialogueSkillSubEditBinding.btnAddSkill.setOnClickListener {
            val skill = Skill(
                categorySubID = subCategory.id,
                skill = dialogueSkillSubEditBinding.skill.text.toString().trim()
            )
            subCategory.skills.add(skill)
            skillAdapter.notifyItemInserted(skillAdapter.itemCount-1)
            if (skillAdapter.itemCount > 0){
                dialogueSkillSubEditBinding.svListSkill.visibility = View.VISIBLE
                dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
            }
//            dialogueSkillSubEditBinding.svListSkill.scrollY = dialogueSkillSubEditBinding.svListSkill.bottom
        }

        dialog.show()
    }
}