package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillSubAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillsEditAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVSkillsMainAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewHolder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillSubEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSkillsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.UsersProfile

/**
 *  Main Category
 *      ArrayList(SubCategory
 *          ArrayList(Skills)
 *      )
 *  For User:
 */
class SkillsFragment : Fragment() {
    private var _binding: FragmentSkillsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userProfile: UsersProfile //TODO: Proper Data Handling
    private val skills: ArrayList<SkillMainCategory> = ArrayList()
    private lateinit var rvAdapter: RVSkillsMainAdapter
    private lateinit var layoutSkillEventsBinding: LayoutSkillEventsBinding

    init {
        if(arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        arguments = Bundle().apply {
            putString("TabName", "Skills")
            putInt("TabIcon", R.drawable.ic_skills_24)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments().containsKey("User")){
            userProfile = requireArguments().getSerializable("User") as UsersProfile
        }
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
        layoutSkillEventsBinding = LayoutSkillEventsBinding.inflate(layoutInflater)
        binding.root.addView(layoutSkillEventsBinding.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        minimizeFabs()
        skills.addAll(getSkills())

        rvAdapter = RVSkillsMainAdapter(skills)
        rvAdapter.setAdapterEvents(object: RVSkillsMainAdapter.AdapterEvents{
            override fun mainCategoryClick(mainCategory: SkillMainCategory) {
                expandFabs(mainCategory)
            }
            override fun subCategoryClick(mainCategory: SkillMainCategory, subCategory: SkillSubCategory) {
                expandFabs(mainCategory, subCategory)
            }
        })

        binding.listSkills.layoutManager = LinearLayoutManager(requireContext())
        binding.listSkills.adapter = rvAdapter

        layoutSkillEventsBinding.efabSkillsOptions.setOnClickListener {
            if (layoutSkillEventsBinding.efabSkillsOptions.isExtended){
                minimizeFabs()
            }else{
                expandFabs()
            }
        }

        layoutSkillEventsBinding.btnAddMainCategory.setOnClickListener {
            editMainCategoryDialogue()
        }
    }

    private fun getSkills(): ArrayList<SkillMainCategory>{
        val skills: ArrayList<SkillMainCategory> = ArrayList()

        if(userProfile.id != 22) {
            for (num in 0..2) {
                val mainCategory = SkillMainCategory(num, "Main $num")
                for (num2 in 0..2) {
                    val skillSub = SkillSubCategory(num2, num, "Sub $num2")
                    for (num3 in 0..3) {
                        val skill = Skill(num3, num2, "Skill $num3")
                        skillSub.skills.add(skill)
                    }
                    mainCategory.subCategories.add(skillSub)
                }
                skills.add(mainCategory)
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
                                Skill(0, 0, "asdkaj")
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
        }else{
            skills.addAll(arrayListOf(
                SkillMainCategory(
                    0,
                    "Web Design and Development",
                    arrayListOf(
                        SkillSubCategory(
                            0,
                            0,
                            "Front End",
                            arrayListOf(
                                Skill(0, 0, "HTML"),
                                Skill(1, 0, "CSS"),
                                Skill(2, 0, "jQuery"),
                                Skill(3, 0, "Bootstrap"),
                            )
                        ),
                        SkillSubCategory(
                            0,
                            0,
                            "Back End",
                            arrayListOf(
                                Skill(0, 0, "PHP"),
                                Skill(1, 0, "SQL"),
                                Skill(2, 0, "jQuery"),
                            )
                        ),
                    )
                ),
                SkillMainCategory(
                    0,
                    "Network Management",
                    arrayListOf(
                        SkillSubCategory(
                            0,
                            0,
                            "Hardware",
                            arrayListOf(
                                Skill(0, 0, "asdkaj"),
                                Skill(1, 0, "asdkaj"),
                                Skill(2, 0, "asdkaj"),
                            )
                        ),
                    )
                ),
            ))
        }

        return skills
    }

    private fun minimizeFabs(){
        with(layoutSkillEventsBinding) {
            layoutBackground.visibility = View.GONE
            efabSkillsOptions.shrink()
            btnAddMainCategory.hide()
            btnEditMainCategory.hide()
            btnDeleteMainCategory.hide()
            labelMainCategoryFab.visibility = View.GONE
            btnAddSubCategory.hide()
            btnEditSubCategory.hide()
            btnDeleteSubCategory.hide()
            labelSubCategoryFab.visibility = View.GONE

            skillMain.visibility = View.GONE
            skillSub.visibility = View.GONE
        }
    }
    private fun expandFabs(){
        with(layoutSkillEventsBinding) {
            layoutBackground.visibility = View.VISIBLE
            efabSkillsOptions.extend()
            btnAddMainCategory.show()
            btnEditMainCategory.show()
            btnDeleteMainCategory.show()
            labelMainCategoryFab.visibility = View.VISIBLE
            btnAddSubCategory.show()
            btnEditSubCategory.show()
            btnDeleteSubCategory.show()
            labelSubCategoryFab.visibility = View.VISIBLE

            skillMain.visibility = View.GONE
            skillSub.visibility = View.GONE
            btnEditMainCategory.setOnClickListener {
                Snackbar.make(requireView(), "A Main Category must be selected in order to edit.", Snackbar.LENGTH_SHORT).show()
            }
            btnDeleteMainCategory.setOnClickListener {
                Snackbar.make(requireView(), "A Main Category must be selected in order to delete.", Snackbar.LENGTH_SHORT).show()
            }
            btnAddSubCategory.setOnClickListener {
                Snackbar.make(requireView(), "A Sub Category must be selected in order to add.", Snackbar.LENGTH_SHORT).show()
            }
            btnEditSubCategory.setOnClickListener {
                Snackbar.make(requireView(), "A Sub Category must be selected in order to edit.", Snackbar.LENGTH_SHORT).show()
            }
            btnDeleteSubCategory.setOnClickListener {
                Snackbar.make(requireView(), "A Sub Category must be selected in order to delete.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *  Has: Nothing
     *      Add MainCategory
     *  Has: MainCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Delete MainCategory
     *      Add SubCategory
     *  Has: SubCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Delete MainCategory
     *      Add SubCategory
     *      Edit SubCategory
     *      Delete SubCategory
     *  Has: Skill but no SubCategory
     *      Add MainCategory
     *      Edit MainCategory
     *      Delete MainCategory
     *      Edit SubCategory
     *      Delete SubCategory
     *
     *  TODO: Add function to notify user that a section needs to be selected
     */
    private fun expandFabs(
        mainCategory: SkillMainCategory,
        subCategory: SkillSubCategory? = null
    ){
        expandFabs()
        with(layoutSkillEventsBinding) {
            skillMain.text = mainCategory.categoryMain
            skillMain.visibility = View.VISIBLE

            if (subCategory != null) {
                skillSub.text = subCategory.categorySub.ifEmpty { "Skills" }
                skillSub.visibility = View.VISIBLE
            }
            btnEditMainCategory.setOnClickListener {
                editMainCategoryDialogue(mainCategory)
            }
            btnDeleteMainCategory.setOnClickListener {
                deleteCategoryDialogue(mainCategory)
            }
            btnAddSubCategory.setOnClickListener {
                editSubCategoryDialogue(mainCategory)
            }
            btnEditSubCategory.setOnClickListener {
                if (subCategory != null) {
                    editSubCategoryDialogue(mainCategory, subCategory)
                }else{
                    Snackbar.make(requireView(), "A Sub Category must be selected in order to edit.", Snackbar.LENGTH_SHORT).show()
                }
            }
            btnDeleteSubCategory.setOnClickListener {
                if (subCategory != null) {
                    deleteCategoryDialogue(mainCategory, subCategory)
                }else{
                    Snackbar.make(requireView(), "A Sub Category must be selected in order to delete.", Snackbar.LENGTH_SHORT).show()
                }
            }
            if (mainCategory.subCategories.size > 0 && mainCategory.subCategories[0].categorySub.isEmpty() && subCategory == null) { //TODO: Revise conditions
                val subCategory = mainCategory.subCategories[0]
                skillSub.text = subCategory.categorySub.ifEmpty { "Skills" }
                skillSub.visibility = View.VISIBLE

                btnAddSubCategory.setOnClickListener {
                    Snackbar.make(requireView(), "First SubCategory must have a Name in order to add more SubCategories", Snackbar.LENGTH_SHORT).show()
                }
                btnEditSubCategory.setOnClickListener {
                    editSubCategoryDialogue(mainCategory, mainCategory.subCategories[0])
                }
                btnDeleteSubCategory.setOnClickListener {
                    deleteCategoryDialogue(mainCategory, mainCategory.subCategories[0])
                }
            }
            layoutBackground.setOnClickListener {
                minimizeFabs()
            }
        }
    }

    /**
     *  Add MainCategory if a MainCategory doesn't exist
     *  Update MainCategory if MainCategory exists
     */
    private fun editMainCategoryDialogue(
        mainCategory: SkillMainCategory = SkillMainCategory()
    ){
        val dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillMainEditBinding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        if (skills.contains(mainCategory)){
            dialogueSkillMainEditBinding.skillMain.setText(mainCategory.categoryMain)
            dialogueSkillMainEditBinding.editButtons.btnSave.visibility = View.GONE
            dialogueSkillMainEditBinding.editButtons.btnUpdate.visibility = View.VISIBLE
        }

        with(dialogueSkillMainEditBinding.editButtons){
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                skills.add(mainCategory)
                rvAdapter.notifyItemInserted(skills.size-1)
                binding.listSkills.scrollToPosition(rvAdapter.itemCount-1)
                val scrollListener =
                    View.OnScrollChangeListener { _, _, _, _, _ ->
                        editSubCategoryDialogue(mainCategory)
                        binding.listSkills.setOnScrollChangeListener(
                            View.OnScrollChangeListener { _, _, _, _, _ ->  }
                        )
                    }
                binding.listSkills.setOnScrollChangeListener(scrollListener)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                val holder = binding.listSkills.findViewHolderForLayoutPosition(skills.indexOf(mainCategory)) as ViewHolder
                rvAdapter.notifyItemChanged(holder.layoutPosition)
                minimizeFabs()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    /**
     *  Add SubCategory if a SubCategory doesn't exist
     *      Adding Skill will automatically add SubCategory upon saving
     *  Update SubCategory if SubCategory exists
     *
     *  TODO: Disable Blank SubCategoryName if SubCategories is greater than 1
     */
    private fun editSubCategoryDialogue(
        mainCategory: SkillMainCategory,
        subCategory: SkillSubCategory = SkillSubCategory(categoryMainID = mainCategory.id)
    ){
        val subCategories = mainCategory.subCategories
        val dialogueSkillSubEditBinding = DialogueSkillSubEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillSubEditBinding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)

        val mainViewHolder = binding.listSkills.findViewHolderForLayoutPosition(skills.indexOf(mainCategory)) as ViewHolder
        val mainCategoryBinding = mainViewHolder.binding as ViewholderSkillsMainBinding
        val skillSubAdapter = mainCategoryBinding.listSkillSub.adapter as RVSkillSubAdapter

        val skillAdapter = RVSkillsEditAdapter(subCategory.skills)
        dialogueSkillSubEditBinding.listSkill.layoutManager =  LinearLayoutManager(context)
        dialogueSkillSubEditBinding.listSkill.adapter =  skillAdapter

        if (skillAdapter.itemCount > 0){
            dialogueSkillSubEditBinding.listSkill.visibility = View.VISIBLE
            dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
        }

        dialogueSkillSubEditBinding.labelSkillMain.setText(mainCategory.categoryMain)

        with(dialogueSkillSubEditBinding.editButtons){
            if(mainCategory.subCategories.contains(subCategory)){
                dialogueSkillSubEditBinding.skillSub.setText(subCategory.categorySub)
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.VISIBLE
                val holder = mainCategoryBinding.listSkillSub.findViewHolderForLayoutPosition(
                    mainCategory.subCategories.indexOf(subCategory)
                ) as ViewHolder
                dialog.setOnDismissListener {
                    if (subCategory.categorySub.isEmpty() && subCategory.skills.isEmpty()){
                        mainCategory.subCategories.remove(subCategory)
                        skillSubAdapter.notifyItemRemoved(holder.layoutPosition)
                        expandFabs(mainCategory)
                    }else {
                        skillSubAdapter.notifyItemChanged(holder.layoutPosition)
                    }
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
                subCategories.add(subCategory)
                skillSubAdapter.notifyItemInserted(skillSubAdapter.itemCount-1)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
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
            dialogueSkillSubEditBinding.listSkill.scrollToPosition(skillAdapter.itemCount-1)
            if (skillAdapter.itemCount > 0){
                dialogueSkillSubEditBinding.listSkill.visibility = View.VISIBLE
                dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
            }
            dialogueSkillSubEditBinding.skill.text?.clear()
        }
        dialog.show()
    }
    private fun deleteCategoryDialogue(
        mainCategory: SkillMainCategory,
        subCategory: SkillSubCategory? = null
    ){
        val builder = AlertDialog.Builder(context)
        val mainViewHolder = binding.listSkills.findViewHolderForLayoutPosition(skills.indexOf(mainCategory)) as ViewHolder
        val mainCategoryBinding = mainViewHolder.binding as ViewholderSkillsMainBinding
        if (subCategory != null){
            val skillSubAdapter = mainCategoryBinding.listSkillSub.adapter as RVSkillSubAdapter
            val holder = mainCategoryBinding.listSkillSub.findViewHolderForLayoutPosition(
                mainCategory.subCategories.indexOf(subCategory)
            ) as ViewHolder

            val title = if(subCategory.categorySub.isEmpty()) "Skills List" else "Sub Category"
            val message = subCategory.categorySub.ifEmpty { "Skills List" }

            builder.apply {
                setTitle("Delete $title?")
                setMessage("Are you sure to delete $message and all of its components?")
                setPositiveButton("Yes"){ dialog, _ ->
                    mainCategory.subCategories.remove(subCategory)
                    skillSubAdapter.notifyItemRemoved(holder.layoutPosition)
                    minimizeFabs()
                    dialog.dismiss()
                }
                setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            }
        }else{
            builder.apply {
                setTitle("Delete Main Category?")
                setMessage("Are you sure to delete ${mainCategory.categoryMain} and all of its components?")
                setPositiveButton("Yes"){ dialog, _ ->
                    skills.remove(mainCategory)
                    rvAdapter.notifyItemRemoved(mainViewHolder.layoutPosition)
                    minimizeFabs()
                    dialog.dismiss()
                }
                setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }
}