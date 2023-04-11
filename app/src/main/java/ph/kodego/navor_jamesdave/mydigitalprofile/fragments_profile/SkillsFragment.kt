package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
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
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

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
    private lateinit var profile: Profile //TODO: Proper Data Handling
    private val skills: ArrayList<SkillMainCategory> = ArrayList()
    private lateinit var rvAdapter: RVSkillsMainAdapter
    private lateinit var layoutSkillEventsBinding: LayoutSkillEventsBinding
    private lateinit var dao: FirebaseSkillsMainCategoryDAO

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

        profile = if (requireArguments().containsKey(IntentBundles.Profile)){
            requireArguments().getParcelable<Profile>(IntentBundles.Profile)!!
        }else{
            Profile()
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
        dao = FirebaseSkillsMainCategoryDAOImpl(profile.profileID)
        skills.addAll(getSkillsSample())
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        lifecycleScope.launch {
            skills.addAll(dao.getMainCategories())
            rvAdapter = RVSkillsMainAdapter(skills)

            binding.listSkills.layoutManager = LinearLayoutManager(requireContext())
            binding.listSkills.adapter = rvAdapter

            if(Firebase.auth.currentUser?.uid == profile.uID) { //TODO: Enclose
                attachEditingInterface()
            }
        }
    }

    private fun attachEditingInterface(){
        layoutSkillEventsBinding = LayoutSkillEventsBinding.inflate(layoutInflater)
        binding.root.addView(layoutSkillEventsBinding.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        rvAdapter.setAdapterEvents(object: RVSkillsMainAdapter.AdapterEvents{
            override fun mainCategoryClick(mainCategory: SkillMainCategory) {
                expandFabs(mainCategory)
            }
            override fun subCategoryClick(mainCategory: SkillMainCategory, subCategory: SkillSubCategory) {
                expandFabs(mainCategory, subCategory)
            }
        })

        minimizeFabs()

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

    private fun getSkillsSample(): ArrayList<SkillMainCategory>{
        val skills: ArrayList<SkillMainCategory> = ArrayList()
        with(profile) {
            if (profile.profileID != "22") {
                for (categoryMainID in 0..2) {
                    val mainCategory =
                        SkillMainCategory(categoryMainID.toString(), profile.profileID, "Main $categoryMainID")
                    for (categorySubID in 0..2) {
                        val skillSub =
                            SkillSubCategory(categorySubID.toString(), categoryMainID.toString(), "Sub $categorySubID")
                        for (skillID in 0..3) {
                            val skill = Skill(skillID.toString(), categorySubID.toString(), "Skill $skillID")
                            skillSub.skills.add(skill)
                        }
                        mainCategory.subCategories.add(skillSub)
                    }
                    skills.add(mainCategory)
                }
                skills.add(
                    SkillMainCategory(
                        "4",
                        profile.profileID,
                        "Main 4",
                        arrayListOf(
                            SkillSubCategory(
                                "0",
                                "4",
                                "",
                                arrayListOf(
                                    Skill("0", "0", "asdkaj")
                                )
                            )
                        )
                    )
                )
                skills.add(
                    SkillMainCategory(
                        "5",
                        profile.profileID,
                        "Main 5"
                    )
                )
            } else {
                skills.addAll(
                    arrayListOf(
                        SkillMainCategory(
                            "0",
                            profile.profileID,
                            "Computer Maintenance and Troubleshooting",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "0",
                                    "Hardware",
                                    arrayListOf(
                                        Skill("0", "0", "Hardware Installation and Setup"),
                                        Skill("1", "0", "Hardware Troubleshooting and Repair"),
                                        Skill("2", "0", "System Upgrade/Downgrade"),
                                        Skill("3", "0", "Hardware Maintenance"),
                                        Skill("4", "0", "Peripheral Installation and Setup"),
                                    )
                                ),
                                SkillSubCategory(
                                    "1",
                                    "0",
                                    "Software",
                                    arrayListOf(
                                        Skill("0", "0", "Software Troubleshooting and Repair"),
                                        Skill("1", "0", "Malware/Virus Removal"),
                                        Skill("2", "0", "Software/System Update"),
                                        Skill("3", "0", "Formatting"),
                                        Skill("4", "0", "Operating System/Application Installation"),
                                        Skill("5", "0", "File Recovery"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "1",
                            profile.profileID,
                            "Network Management",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "1",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Network Setup and Configuration"),
                                        Skill("1", "0", "File and Printer Sharing"),
                                        Skill("2", "0", "Network Sharing"),
                                        Skill("3", "0", "Remote Desktop Configuration"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "2",
                            profile.profileID,
                            "Android App Development",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "2",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Kotlin"),
                                        Skill("1", "0", "Android Studio"),
                                        Skill("2", "0", "Object-Oriented Programming"),
                                        Skill("3", "0", "ViewBinding"),
                                        Skill("4", "0", "RecyclerView Manipulation"),
                                        Skill("5", "0", "WireFraming"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "3",
                            profile.profileID,
                            "Web Development (Obsolete)",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "3",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "HTML5"),
                                        Skill("1", "0", "CSS3"),
                                        Skill("2", "0", "Bootstrap 4"),
                                        Skill("3", "0", "Javascript"),
                                        Skill("4", "0", "jQuery"),
                                        Skill("5", "0", "PHP"),
                                        Skill("6", "0", "MySQL"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "4",
                            profile.profileID,
                            "Java Programming (Obsolete)",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "4",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Netbeans IDE"),
                                        Skill("1", "0", "GUI Applications"),
                                        Skill("2", "0", "Console Applications"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "5",
                            profile.profileID,
                            "Java Development (Obsolete)",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "5",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Netbeans IDE"),
                                        Skill("1", "0", "GUI Applications"),
                                        Skill("2", "0", "Console Applications"),
                                        Skill("3", "0", "Javascript"),
                                        Skill("4", "0", "jQuery"),
                                        Skill("5", "0", "PHP"),
                                        Skill("6", "0", "MySQL"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "6",
                            profile.profileID,
                            "C# Development (Obsolete)",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "6",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Visual Studio 2017"),
                                        Skill("1", "0", "Windows Applications"),
                                        Skill("2", "0", "Console Applications"),
                                        Skill("3", "0", "MySQL Connection"),
                                    )
                                ),
                            )
                        ),
                        SkillMainCategory(
                            "7",
                            profile.profileID,
                            "Adobe Photoshop",
                            arrayListOf(
                                SkillSubCategory(
                                    "0",
                                    "7",
                                    "",
                                    arrayListOf(
                                        Skill("0", "0", "Basic Photo Editing"),
                                        Skill("1", "0", "Basic Photo Manipulation"),
                                        Skill("2", "0", "Basic Retouching"),
                                    )
                                ),
                            )
                        ),
                    )
                )
            }
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
    private fun editMainCategoryDialogue( //TODO: Change Approach
        mainCategory: SkillMainCategory = SkillMainCategory(profile.profileID)
    ){
        val dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillMainEditBinding.root)
        val dialog = builder.create()
        val holder = binding.listSkills.findViewHolderForLayoutPosition(skills.indexOf(mainCategory)) as? ViewHolder //TODO: Optimize
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
            btnSave.setOnClickListener { //TODO: Form Validation
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                lifecycleScope.launch {
                    if(dao.addMainCategory(mainCategory)) {
                        skills.add(mainCategory)
                        rvAdapter.notifyItemInserted(skills.size - 1)
                        binding.listSkills.scrollToPosition(rvAdapter.itemCount - 1)
                        val scrollListener =
                            View.OnScrollChangeListener { _, _, _, _, _ ->
                                editSubCategoryDialogue(mainCategory)
                                binding.listSkills.setOnScrollChangeListener { _, _, _, _, _ -> }
                            }
                        binding.listSkills.setOnScrollChangeListener(scrollListener)
                    }else{
                        Snackbar.make(requireView(), "Error Adding Main Category", Snackbar.LENGTH_SHORT).show()
                    }
                    minimizeFabs()
                    dialog.dismiss()
                }
            }
            btnUpdate.setOnClickListener {
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                rvAdapter.notifyItemChanged(holder!!.layoutPosition)
                minimizeFabs()
                dialog.dismiss()
            }
        }
        dialog.show()
        dialogueSkillMainEditBinding.skillMain.requestFocus()
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
        subCategory: SkillSubCategory = SkillSubCategory(mainCategory.mainCategoryID)
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

        dialogueSkillSubEditBinding.labelSkillMain.text = mainCategory.categoryMain

        with(dialogueSkillSubEditBinding.editButtons){
            if(subCategories.contains(subCategory)){
                val holder = mainCategoryBinding.listSkillSub.findViewHolderForLayoutPosition(
                    subCategories.indexOf(subCategory)
                ) as ViewHolder
                dialogueSkillSubEditBinding.skillSub.setText(subCategory.categorySub)
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.VISIBLE
                dialog.setOnDismissListener {
                    if (subCategory.categorySub.isEmpty() && subCategory.skills.isEmpty()){
                        subCategories.remove(subCategory)
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
                subCategory.subCategoryID,
                dialogueSkillSubEditBinding.skill.text.toString().trim()
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
        dialogueSkillSubEditBinding.skillSub.requestFocus()
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