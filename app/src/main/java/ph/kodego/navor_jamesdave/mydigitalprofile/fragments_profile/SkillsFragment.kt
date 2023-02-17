package ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile

import android.app.AlertDialog
import android.os.Bundle
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
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewHolder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillSubEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSkillsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
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
        rvAdapter.setAdapterEvents(object: RVSkillsMainAdapter.AdapterEvents{
            override fun holderClickNotify(position: Int) {
                Snackbar.make(view, "Adapter Clicked at $position", Snackbar.LENGTH_SHORT).show()
            }

            override fun mainCategoryClick(mainCategory: SkillMainCategory, holder: ViewHolder) {
                expandFabs(mainCategory, null, holder)
            }

            override fun subCategoryClick(mainCategory: SkillMainCategory, subCategory: SkillSubCategory, holder: ViewHolder) {
                expandFabs(mainCategory, subCategory, holder)
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
    }

    private fun getSkills(): ArrayList<SkillMainCategory>{
        val skills: ArrayList<SkillMainCategory> = ArrayList()

        for (num in 0..2){
            val mainCategory = SkillMainCategory(num, "Main $num")
            for (num2 in 0..2){
                val skillSub = SkillSubCategory(num2, num, "Sub $num2")
                for (num3 in 0..3){
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

    // TODO: Add function to notify user that a section needs to be selected
    private fun minimizeFabs(){
        binding.efabSkillsOptions.shrink()
        binding.btnAddMainCategory.hide()
        binding.btnEditMainCategory.hide()
        binding.btnEditMainCategory.isEnabled = false
        binding.btnDeleteMainCategory.hide()
        binding.btnDeleteMainCategory.isEnabled = false
        binding.labelMainCategoryFab.visibility = View.GONE
        binding.btnAddSubCategory.hide()
        binding.btnAddSubCategory.isEnabled = false
        binding.btnEditSubCategory.hide()
        binding.btnEditSubCategory.isEnabled = false
        binding.btnDeleteSubCategory.hide()
        binding.btnDeleteSubCategory.isEnabled = false
        binding.labelSubCategoryFab.visibility = View.GONE

        binding.skillMain.visibility = View.GONE
        binding.skillSub.visibility = View.GONE
    }
    private fun expandFabs(){
        binding.efabSkillsOptions.extend()
        binding.btnAddMainCategory.show()
        binding.btnEditMainCategory.show()
        binding.btnEditMainCategory.isEnabled = false
        binding.btnDeleteMainCategory.show()
        binding.btnDeleteMainCategory.isEnabled = false
        binding.labelMainCategoryFab.visibility = View.VISIBLE
        binding.btnAddSubCategory.show()
        binding.btnAddSubCategory.isEnabled = false
        binding.btnEditSubCategory.show()
        binding.btnEditSubCategory.isEnabled = false
        binding.btnDeleteSubCategory.show()
        binding.btnDeleteSubCategory.isEnabled = false
        binding.labelSubCategoryFab.visibility = View.VISIBLE

        binding.skillMain.visibility = View.GONE
        binding.skillSub.visibility = View.GONE
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
     */
    private fun expandFabs(
        mainCategory: SkillMainCategory,
        subCategory: SkillSubCategory? = null,
        holder: ViewHolder
    ){
        expandFabs()
        binding.btnEditMainCategory.isEnabled = true
        binding.btnAddSubCategory.isEnabled = true
        binding.btnDeleteMainCategory.isEnabled = true
        binding.skillMain.text = mainCategory.categoryMain
        binding.skillMain.visibility = View.VISIBLE

        if(subCategory != null){
            binding.btnEditSubCategory.isEnabled = true
            binding.btnDeleteSubCategory.isEnabled = true
            binding.skillSub.text = subCategory.categorySub.ifEmpty { "Skills" }
            binding.skillSub.visibility = View.VISIBLE
            if (subCategory.categorySub.isEmpty()){
                binding.btnAddSubCategory.isEnabled = false
            }
        }

        binding.btnAddMainCategory.setOnClickListener {
            editMainCategoryDialogue()
        }
        binding.btnEditMainCategory.setOnClickListener {
            editMainCategoryDialogue(mainCategory, holder)
        }
        binding.btnDeleteMainCategory.setOnClickListener {
//            deleteMainCategoryDialogue(mainCategory, holder) //TODO
        }
        binding.btnAddSubCategory.setOnClickListener {
            editSubCategoryDialogue(mainCategory, SkillSubCategory(categoryMainID = mainCategory.id))
        }
        if(mainCategory.subCategories.size > 0 && mainCategory.subCategories[0].categorySub.isEmpty() && subCategory == null){ //TODO: Revise conditions
            val subCategory = mainCategory.subCategories[0]
            binding.btnAddSubCategory.isEnabled = false
            binding.btnEditSubCategory.isEnabled = true
            binding.skillSub.text = subCategory.categorySub.ifEmpty { "Skills" }
            binding.skillSub.visibility = View.VISIBLE
            val mainCategoryBinding = holder.binding as ViewholderSkillsMainBinding
            val subCategoryHolder = mainCategoryBinding.listSkillSub.findViewHolderForLayoutPosition(0) as ViewHolder
            binding.btnEditSubCategory.setOnClickListener {
                editSubCategoryDialogue(mainCategory, mainCategory.subCategories[0], subCategoryHolder)
            }
        }else {
            binding.btnEditSubCategory.setOnClickListener {
                editSubCategoryDialogue(mainCategory, subCategory!!, holder)
            }
        }
        binding.btnDeleteSubCategory.setOnClickListener {
//            deleteSubCategoryDialogue(mainCategory, SkillSubCategory(categoryMainID = mainCategory.id)) //TODO
        }
    }

    /**
     *  Add MainCategory if a MainCategory doesn't exist
     *  Update MainCategory if MainCategory exists
     */
    private fun editMainCategoryDialogue(
        mainCategory: SkillMainCategory = SkillMainCategory(),
        holder: ViewHolder? = null
    ){
        val dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(context).setView(dialogueSkillMainEditBinding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        if (holder != null){
            dialogueSkillMainEditBinding.skillMain.setText(mainCategory.categoryMain)
            dialogueSkillMainEditBinding.editButtons.btnSave.visibility = View.GONE
            dialogueSkillMainEditBinding.editButtons.btnUpdate.visibility = View.VISIBLE
        }

        with(dialogueSkillMainEditBinding.editButtons){
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {//TODO: Add SubCategory after saving
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                Toast.makeText(context, "Save: ${mainCategory.categoryMain}", Toast.LENGTH_SHORT).show()
                skills.add(mainCategory)
                rvAdapter.notifyItemInserted(skills.size-1)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                mainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                rvAdapter.notifyItemChanged(holder!!.layoutPosition)
                Toast.makeText(context, "Update: ${mainCategory.categoryMain}", Toast.LENGTH_SHORT).show()
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
     *  TODO: MinimizeFabs if clicked elsewhere
     */
    private fun editSubCategoryDialogue(
        mainCategory: SkillMainCategory,
        subCategory: SkillSubCategory,
        holder: ViewHolder? = null
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
            if(holder != null){
                dialogueSkillSubEditBinding.skillSub.setText(subCategory.categorySub)
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.VISIBLE
                dialog.setOnDismissListener {//TODO: Debug - Update List then cancel Dialog then Update again ->
//                    skillSubAdapter.notifyItemChanged(holder.layoutPosition)
                    skillSubAdapter.notifyDataSetChanged()
//                    minimizeFabs() //TODO: Needs to get a new Event again in order to update contents properly
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSave.setOnClickListener {// TODO: Add Skill without Subcategory
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
                Toast.makeText(context, "Save: ${subCategory.categorySub}", Toast.LENGTH_SHORT).show()
                subCategories.add(subCategory)
                skillSubAdapter.notifyItemInserted(skillSubAdapter.itemCount-1)
                minimizeFabs()
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                subCategory.categorySub = dialogueSkillSubEditBinding.skillSub.text.toString().trim()
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
            dialogueSkillSubEditBinding.listSkill.scrollToPosition(skillAdapter.itemCount-1)
            if (skillAdapter.itemCount > 0){
                dialogueSkillSubEditBinding.listSkill.visibility = View.VISIBLE
                dialogueSkillSubEditBinding.listEmpty.visibility = View.GONE
            }
        }
        dialog.show()
    }
}