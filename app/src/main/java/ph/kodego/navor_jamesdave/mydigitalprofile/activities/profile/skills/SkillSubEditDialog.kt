package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.SkillsEditAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogSkillSubEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

class SkillSubEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> Unit
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogSkillSubEditBinding
    private lateinit var mainSkills: List<SkillsMain>
    private var mainSkill: SkillsMain? = null
    private var subSkill: SkillsSub? = null
    private val itemsAdapter = SkillsEditAdapter()

    override fun create(): AlertDialog {
        binding = DialogSkillSubEditBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        binding.btnAddSkill.setOnClickListener { addSkill() }
        setupRecyclerView()
        binding.editButtons.setupButtons()
        setOnDismissListener { resetDialog() }
        setSubSkillDetails()
        return super.create().also {
            dialog = it
        }
    }

    private fun LayoutEditButtonsBinding.setupButtons(){
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener { saveSkill() }
        btnUpdate.setOnClickListener { updateSkill() }
        btnDelete.setOnClickListener { openDeleteDialog() }
    }

    private fun resetDialog(){
        mainSkill = null
        subSkill = null
        itemsAdapter.setList(emptyList())
        binding.subtitle.text?.clear()
        binding.skill.text?.clear()
    }

    private fun setupRecyclerView(){
        binding.listSkill.apply{
            itemsAdapter.touchHelper.attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }

    private fun addSkill() {
        val skill = binding.skill.text.toString().trim()
        if (skill.isNotEmpty()){
            itemsAdapter.add(skill)
            binding.skill.text?.clear()
        }else{
            binding.skill.error = "Skill cannot be empty."
        }
        if (itemsAdapter.skills().isNotEmpty()){
            binding.listEmpty.visibility = View.GONE
            binding.listSkill.visibility = View.VISIBLE
        }
    }

    private fun openDeleteDialog() {
        DeleteItemDialog(context, subSkill!!){
            deleteSkill()
        }.show()
    }

    private fun deleteSkill(){
        mainSkill!!.subCategories.remove(subSkill!!)
        saveChanges(mainSkills)
    }

    private fun updateSkill() {
        getFormData()?.let {
            mainSkill!!.subCategories[mainSkill!!.subCategories.indexOf(subSkill!!)] = it
            saveChanges(mainSkills)
        }
    }

    private fun saveSkill() {
        getFormData()?.let {
            mainSkill!!.subCategories.add(it)
            saveChanges(mainSkills)
        }
    }

    private fun getFormData(): SkillsSub?{
        val subtitle = binding.subtitle.text.toString().trim()
        if (subtitle.isEmpty() && mainSkill!!.subCategories.size >= 1 && subSkill == null){
            binding.subtitle.error = "You have at least 1 subcategory. This field cannot be empty."
            binding.subtitle.requestFocus()
            return null
        }
        return SkillsSub(
            subtitle,
            ArrayList(itemsAdapter.skills())
        )
    }

    private fun saveChanges(skills: List<SkillsMain>) {
        dialog.dismiss()
        skills.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_SKILLS to skills)
        update(changes)
    }

    fun add(parent: SkillsMain){
        mainSkill = parent
        show()
        binding.subtitle.requestFocus()
    }

    fun edit(skill: SkillsSub, parent: SkillsMain){
        mainSkill = parent
        subSkill = skill
        show()
    }

    fun delete(skill: SkillsSub, parent: SkillsMain){
        mainSkill = parent
        subSkill = skill
        openDeleteDialog()
    }

    fun setParentList(list: List<SkillsMain>){
        mainSkills = list
    }

    private fun updateList(){
        subSkill?.let {
            if (it.skills.isEmpty()) {
                binding.listEmpty.visibility = View.VISIBLE
                binding.listSkill.visibility = View.GONE
            }else{
                binding.listEmpty.visibility = View.GONE
                binding.listSkill.visibility = View.VISIBLE
            }
        }
    }

    private fun setSubSkillDetails() {
        with(binding){
            title.text = mainSkill!!.title
            subSkill?.let {
                subtitle.setText(it.subtitle)
                itemsAdapter.setList(it.skills)
                updateList()
                editButtons.editInterface()
            } ?: editButtons.saveInterface()
        }
    }
}