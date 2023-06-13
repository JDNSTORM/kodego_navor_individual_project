package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.SkillsEditAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogSkillSubEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SkillSubEditDialog<T>(context: T, private val profile: Profile): AlertDialog(context) where T: Context, T: ViewModelStoreOwner{
    private val binding by lazy { DialogSkillSubEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(context)[ProfileViewModel::class.java] }
    private lateinit var mainSkills: List<SkillsMain>
    private var mainSkill: SkillsMain? = null
    private var subSkill: SkillsSub? = null
    private val itemsAdapter = SkillsEditAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener { saveSkill() }
            btnUpdate.setOnClickListener { updateSkill() }
            btnDelete.setOnClickListener { openDeleteDialog() }
        }
        binding.btnAddSkill.setOnClickListener { addSkill() }
        setOnShowListener { setupRecyclerView() }
    }

    override fun dismiss() {
        resetDialog()
        super.dismiss()
    }

    private fun resetDialog(){
        mainSkill = null
        subSkill = null
        itemsAdapter.setList(emptyList())
        binding.subtitle.text?.clear()
        binding.skill.text?.clear()
    }

    private fun setupRecyclerView(){
        with(binding.listSkill){
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
        object: DeleteItemDialog(context, subSkill!!){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                deleteSkill()
                dialog.dismiss()
            }
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
        dismiss()
        skills.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_SKILLS to skills)
        CoroutineScope(Dispatchers.IO).launch {
            val updateSuccessful = viewModel.updateProfile(profile, changes)
            withContext(Dispatchers.Main){
                if (updateSuccessful){
                    Toast.makeText(context, "Skills Updated!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Save Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun show() {
        if (mainSkill == null){
            Log.e("SubSkill Edit", "Prerequisites Not Initialized")
            return
        }
    }

    fun add(parent: SkillsMain){
        super.show()
        mainSkill = parent
        binding.editButtons.saveInterface()
        binding.subtitle.requestFocus()
    }

    fun edit(skill: SkillsSub, parent: SkillsMain){
        super.show()
        mainSkill = parent
        subSkill = skill
        setSubSkillDetails()
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
            subSkill?.let {
                subtitle.setText(it.subtitle)
                itemsAdapter.setList(it.skills)
                updateList()

                editButtons.editInterface()
            } ?: editButtons.saveInterface()
        }
    }
}