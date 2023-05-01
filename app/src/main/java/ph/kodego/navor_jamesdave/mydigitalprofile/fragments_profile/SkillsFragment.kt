package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
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
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsSubCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.expandFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.minimizeFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface

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
    private lateinit var profile: Profile
    private val skills: ArrayList<SkillMainCategory> = ArrayList()
    private lateinit var rvAdapter: RVSkillsMainAdapter
    private lateinit var layoutSkillEventsBinding: LayoutSkillEventsBinding
    private lateinit var dao: FirebaseSkillsMainCategoryDAO
    private lateinit var progressDialog: ProgressDialog

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

        if (requireArguments().containsKey(IntentBundles.Profile)){
            profile = requireArguments().getParcelable<Profile>(IntentBundles.Profile)!!
        }else{
            Toast.makeText(requireContext(), "No Profile Selected!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
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
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        binding.loadData()
        lifecycleScope.launch {
            skills.addAll(dao.getMainCategories())
            rvAdapter = RVSkillsMainAdapter(skills)

            binding.listSkills.layoutManager = LinearLayoutManager(requireContext())
            binding.listSkills.adapter = rvAdapter

            if(Firebase.auth.currentUser?.uid == profile.uID) {
                attachEditingInterface()
                progressDialog = ProgressDialog(requireContext())
            }
            binding.showData()
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

        layoutSkillEventsBinding.minimizeFabs()

        layoutSkillEventsBinding.efabSkillsOptions.setOnClickListener {
            if (layoutSkillEventsBinding.efabSkillsOptions.isExtended){
                layoutSkillEventsBinding.minimizeFabs()
            }else{
                layoutSkillEventsBinding.expandFabs()
            }
        }

        layoutSkillEventsBinding.btnAddMainCategory.setOnClickListener {
            editMainCategoryDialogue()
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
        layoutSkillEventsBinding.expandFabs()
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
        mainCategory: SkillMainCategory = SkillMainCategory(profile.profileID)
    ){
        val dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillMainEditBinding.root)
        val dialog = builder.create()
        val index = skills.indexOf(mainCategory)
        val holder = binding.listSkills.findViewHolderForLayoutPosition(index) as? ViewHolder //TODO: Optimize
        dialog.setCancelable(false)
        if (skills.contains(mainCategory)){
            dialogueSkillMainEditBinding.skillMain.setText(mainCategory.categoryMain)
            dialogueSkillMainEditBinding.editButtons.updateInterface()
        }

        with(dialogueSkillMainEditBinding.editButtons){
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener { //TODO: Form Validation
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                lifecycleScope.launch {
                    progressDialog.show()
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
                        Toast.makeText(requireContext(), "Error Adding Main Category", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()
                    layoutSkillEventsBinding.minimizeFabs()
                    dialog.dismiss()
                }
            }
            btnUpdate.setOnClickListener {
                val updatedMainCategory = mainCategory.copy()
                updatedMainCategory.subCategories.addAll(mainCategory.subCategories)
                updatedMainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                val modified = FormControls().getModified(mainCategory, updatedMainCategory)
                modified.remove("subCategories")
                if (modified.isNotEmpty()) {
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "Main Category will be updated in the background", Toast.LENGTH_SHORT).show()
                        if(dao.updateMainCategory(mainCategory, modified)){
                            skills[index] = updatedMainCategory
                            rvAdapter.notifyItemChanged(holder!!.layoutPosition)
                            Toast.makeText(requireContext(), "Main Category Updated Successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "Error updating Main Category", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), "No Fields Changed", Toast.LENGTH_SHORT).show()
                }
                layoutSkillEventsBinding.minimizeFabs()
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
        val subCategoryDAO = FirebaseSkillsSubCategoryDAOImpl(mainCategory)
        val subCategories = mainCategory.subCategories
        val dialogueSkillSubEditBinding = DialogueSkillSubEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillSubEditBinding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)

        val mainViewHolder = binding.listSkills.findViewHolderForLayoutPosition(skills.indexOf(mainCategory)) as ViewHolder
        val mainCategoryBinding = mainViewHolder.binding as ViewholderSkillsMainBinding
        val skillSubAdapter = mainCategoryBinding.listSkillSub.adapter as RVSkillSubAdapter

        val skillAdapter = RVSkillsEditAdapter(subCategory)
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
                        lifecycleScope.launch{
                            subCategoryDAO.deleteSubCategory(subCategory)
                        }
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
                lifecycleScope.launch {
                    progressDialog.show()
                    if (subCategoryDAO.addSubCategory(subCategory)){
                        val skillsDAO = FirebaseSkillsDAOImpl(subCategory)
                        subCategory.skills.forEach {  skill ->
                            if(!skillsDAO.addSkill(skill)){
                                subCategory.skills.remove(skill)
                            }
                        }
                        subCategories.add(subCategory)
//                        skillSubAdapter.notifyItemInserted(skillSubAdapter.itemCount-1)
                        rvAdapter.notifyItemChanged(skills.indexOf(mainCategory))
                    }else{
                        Toast.makeText(requireContext(), "Error Saving SubCategory", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()
                    layoutSkillEventsBinding.minimizeFabs()
                    dialog.dismiss()
                }
            }
            btnUpdate.setOnClickListener {
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
                val fields: HashMap<String, Any?> = hashMapOf("categorySub" to subCategory.categorySub)
                Toast.makeText(requireContext(), "SubCategory will be updated in the background", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    if(subCategoryDAO.updateSubCategory(subCategory, fields)){
                        Toast.makeText(requireContext(), "SubCategory Updated", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Error Updating SubCategory", Toast.LENGTH_SHORT).show()
                    }
                }
                layoutSkillEventsBinding.minimizeFabs()
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
            lifecycleScope.launch {
                if (subCategory.subCategoryID.isNotEmpty()){
                    progressDialog.show()
                    FirebaseSkillsDAOImpl(subCategory).addSkill(skill)
                    progressDialog.dismiss()
                }
                subCategory.skills.add(skill)
                skillAdapter.notifyItemInserted(skillAdapter.itemCount-1)
                dialogueSkillSubEditBinding.listSkill.scrollToPosition(skillAdapter.itemCount-1)
                if (skillAdapter.itemCount > 0){
                    dialogueSkillSubEditBinding.listSkill.visibility = View.VISIBLE
                    dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
                }
                dialogueSkillSubEditBinding.skill.text?.clear()
            }
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
                    Toast.makeText(requireContext(), "$title will be deleted in the background.", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        if (FirebaseSkillsSubCategoryDAOImpl(mainCategory).deleteSubCategory(subCategory)){
                            mainCategory.subCategories.remove(subCategory)
                            skillSubAdapter.notifyItemRemoved(holder.layoutPosition)
                            Toast.makeText(requireContext(), "Successfully deleted $title", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "Error deleting $title", Toast.LENGTH_SHORT).show()
                        }
                    }
                    layoutSkillEventsBinding.minimizeFabs()
                    dialog.dismiss()
                }
                setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            }
        }else{
            val categoryName = mainCategory.categoryMain
            builder.apply {
                setTitle("Delete Main Category?")
                setMessage("Are you sure to delete $categoryName all of its components?")
                setPositiveButton("Yes"){ dialog, _ ->
                    Toast.makeText(requireContext(), "$categoryName be deleted in the background/", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        if(dao.deleteMainCategory(mainCategory)){
                            skills.remove(mainCategory)
                            rvAdapter.notifyItemRemoved(mainViewHolder.layoutPosition)
                            Toast.makeText(requireContext(), "Successfully deleted $categoryName", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "Error Deleting $categoryName", Toast.LENGTH_SHORT).show()
                        }
                    }
                    layoutSkillEventsBinding.minimizeFabs()
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