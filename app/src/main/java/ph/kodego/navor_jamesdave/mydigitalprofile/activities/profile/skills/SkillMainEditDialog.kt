package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.SkillsSubDragAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain

class SkillMainEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> Unit
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogSkillMainEditBinding
    private val mainSkills: ArrayList<SkillsMain> = ArrayList()
    private var mainSkill: SkillsMain? = null
    private val itemsAdapter = SkillsSubDragAdapter()

    override fun create(): AlertDialog {
        binding = DialogSkillMainEditBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        binding.editButtons.setupButtons()
        setOnDismissListener {
            resetDialog()
        }
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
        binding.labelSubtitles.visibility = View.VISIBLE
        binding.listSkills.visibility = View.VISIBLE
    }

    private fun openDeleteDialog() {
        DeleteItemDialog(context, mainSkill!!){
            deleteSkill()
        }.show()
    }

    private fun deleteSkill() {
        mainSkills.remove(mainSkill!!)
        saveChanges(mainSkills)
    }

    private fun updateSkill() {
        getFormData()?.let {
            mainSkills[mainSkills.indexOf(mainSkill!!)] = it
            saveChanges(mainSkills)
        }
    }

    private fun saveSkill() {
        getFormData()?.let {
            profile.skills.add(it)
            saveChanges(profile.skills)
        }
    }

    private fun saveChanges(skills: List<SkillsMain>) {
        dialog.dismiss()
        skills.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_SKILLS to skills)
        update(changes)
    }

    private fun getFormData(): SkillsMain? {
        val title = binding.title.text.toString().trim()
        if (title.isEmpty()){
            binding.tilTitle.error = "Title cannot be empty."
            binding.title.requestFocus()
            return null
        }
        return SkillsMain(
            title,
            ArrayList(itemsAdapter.skills())
        )
    }

    override fun show(): AlertDialog? {
        return super.show().also {
            setupRecyclerView()
            binding.title.requestFocus()
            setSkillDetails()
        }
    }

    fun edit(skill: SkillsMain, list: List<SkillsMain>){
        mainSkill = skill
        mainSkills.clear()
        mainSkills.addAll(list)
        show()
    }

    fun delete(skill: SkillsMain, list: List<SkillsMain>){
        mainSkill = skill
        mainSkills.clear()
        mainSkills.addAll(list)
        openDeleteDialog()
    }

    private fun setupRecyclerView(){
        mainSkill?.let {
            if (it.subCategories.size > 1) {
                itemsAdapter.setList(it.subCategories)
                binding.listSkills.apply {
                    itemsAdapter.touchHelper.attachToRecyclerView(this)
                    layoutManager = LinearLayoutManager(context)
                    adapter = itemsAdapter
                }
            }else{
                binding.labelSubtitles.visibility = View.GONE
                binding.listSkills.visibility = View.GONE
            }
        } ?: kotlin.run {
            binding.labelSubtitles.visibility = View.GONE
            binding.listSkills.visibility = View.GONE
        }
    }

    private fun setSkillDetails() {
        with(binding) {
            mainSkill?.let {
                title.setText(it.title)

                editButtons.editInterface()
            } ?: editButtons.saveInterface()
        }
    }
}