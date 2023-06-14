package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.SkillsSubDragAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SkillMainEditDialog<T>(context: T, private val profile: Profile): AlertDialog(context) where T: Context, T: ViewModelStoreOwner{
    private val binding by lazy { DialogSkillMainEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(context)[ProfileViewModel::class.java] }
    private val mainSkills: ArrayList<SkillsMain> = ArrayList()
    private var mainSkill: SkillsMain? = null
    private val itemsAdapter = SkillsSubDragAdapter()

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

        setOnShowListener { setupRecyclerView() }
    }

    override fun dismiss() {
        resetDialog()
        super.dismiss()
    }
    private fun resetDialog(){
        mainSkill = null
        binding.labelSubtitles.visibility = View.VISIBLE
        binding.listSkills.visibility = View.VISIBLE
    }

    private fun openDeleteDialog() {
        object: DeleteItemDialog(context, mainSkill!!){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                deleteSkill()
                dialog.dismiss()
            }
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

    override fun show() {
        super.show()
        binding.title.requestFocus()
        binding.editButtons.saveInterface()
    }

    fun edit(skill: SkillsMain, list: List<SkillsMain>){
        super.show()
        mainSkill = skill
        mainSkills.clear()
        mainSkills.addAll(list)
        setSkillDetails()
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