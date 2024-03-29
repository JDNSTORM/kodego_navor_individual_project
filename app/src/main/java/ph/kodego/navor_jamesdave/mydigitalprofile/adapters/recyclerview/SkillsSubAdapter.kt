package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.expandFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillsEditingInterface

class SkillsSubAdapter(): ItemsAdapter<SkillsSub>() {
    private var editInterface: SkillsEditingInterface? = null
    private lateinit var mainSkills: List<SkillsMain>
    private lateinit var mainSkill: SkillsMain

    override fun getDiffUtilCallback(
        oldItems: List<SkillsSub>,
        newItems: List<SkillsSub>
    ): DiffUtil.Callback {
        return object: DiffUtil.Callback(){
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].subtitle == newItems[newItemPosition].subtitle
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsSubBinding
        val subSkill = items[position]
        binding.bind(subSkill)

        editInterface?.editBinding?.apply {
            binding.root.setOnClickListener { expandFabs(subSkill) }
        }
    }

    private fun ItemSkillsSubBinding.bind(subSkill: SkillsSub) {
        skillSub.apply {
            text = subSkill.subtitle
            isVisible = text.isNotEmpty()
        }
        val itemsAdapter = SkillsAdapter()
        if(subSkill.skills.isNotEmpty()) {
            listSkill.isVisible = true
            listSkill.layoutManager = GridLayoutManager(root.context, 2)
            listSkill.adapter = itemsAdapter
            itemsAdapter.setList(subSkill.skills)
        }
        editInterface?.apply {
            itemsAdapter.enableEditing{ editBinding.expandFabs(subSkill) }
        }
    }

    fun enableEditing(editingInterface: SkillsEditingInterface, parent: SkillsMain, parentList: List<SkillsMain>){
        editInterface = editingInterface
        mainSkill = parent
        mainSkills = parentList
    }

    private fun LayoutSkillEventsBinding.expandFabs(subSkill: SkillsSub){
        expandFabs()
        val mainEditDialog = editInterface!!.mainSkillEditDialog
        val subEditDialog = editInterface!!.subSkillEditDialog
        skillMain.text = mainSkill.title
        skillMain.isVisible = true
        val skillsOnly = subSkill.subtitle.isEmpty()
        skillSub.isVisible = true
        if (skillsOnly){
            skillSub.setText(R.string.skills)
        }else{
            skillSub.text = subSkill.subtitle
        }

        btnEditMainCategory.setOnClickListener { mainEditDialog.edit(mainSkill, mainSkills) }
        btnDeleteMainCategory.setOnClickListener { mainEditDialog.delete(mainSkill, mainSkills) }
        if (skillsOnly){
            btnAddSubCategory.setOnClickListener {
                Snackbar.make(
                    root,
                    "First SubCategory must have a Name in order to add more SubCategories",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }else {
            btnAddSubCategory.setOnClickListener { subEditDialog.add(mainSkill) }
        }
        btnEditSubCategory.setOnClickListener { subEditDialog.edit(subSkill, mainSkill) }
        btnDeleteSubCategory.setOnClickListener { subEditDialog.delete(subSkill, mainSkill) }
    }
}