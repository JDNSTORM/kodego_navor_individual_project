package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.expandFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillsEditingInterface
import java.util.Collections

class SkillsMainAdapter(): ItemsAdapter<SkillsMain>() {
    private var drag: Boolean = false
    private var editInterface: SkillsEditingInterface? = null
    private lateinit var touchHelper: ItemTouchHelper

    override fun getDiffUtilCallback(
        oldItems: List<SkillsMain>,
        newItems: List<SkillsMain>
    ): DiffUtil.Callback {
        return object: DiffUtil.Callback(){
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].title == newItems[newItemPosition].title
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsMainBinding
        val mainSkill = items[position]
        binding.bind(mainSkill)
        editInterface?.editBinding?.apply {
            binding.root.setOnClickListener { expandFabs(mainSkill) }
        }

        if (this::touchHelper.isInitialized){
            with(binding.handle){
                setOnClickListener {  }
                setOnTouchListener { _, event ->
                    when(event.action){
                        KeyEvent.ACTION_DOWN -> {
                            touchHelper.startDrag(holder)
                            true
                        }
                        else -> {
                            performClick()
                            false
                        }
                    }
                }
            }
        }
    }

    private fun ItemSkillsMainBinding.bind(mainSkill: SkillsMain){
        title.text = mainSkill.title
        val itemsAdapter = SkillsSubAdapter()

        handle.isVisible = drag
        dividerHorizontal.isVisible = !drag
        listSkillSub.isVisible = !drag
        listSkillSub.layoutManager = LinearLayoutManager(root.context)
        listSkillSub.adapter = itemsAdapter
        itemsAdapter.setList(mainSkill.subCategories)
        editInterface?.let {
            itemsAdapter.enableEditing(
                it.apply { subSkillEditDialog.setParentList(items) },
                mainSkill,
                items
            )
        }
    }

    fun toggleDrag(): Boolean{
        drag = !drag
        notifyItemRangeChanged(0, itemCount)
        return drag
    }

    fun clearToggle(){
        if (drag) toggleDrag()
    }

    fun enableEditing(editingInterface: SkillsEditingInterface){
        editInterface = editingInterface
        notifyItemRangeChanged(0, itemCount)
    }

    private fun LayoutSkillEventsBinding.expandFabs(mainSkill: SkillsMain){
        expandFabs()
        val mainEditDialog = editInterface!!.mainSkillEditDialog
        val subEditDialog = editInterface!!.subSkillEditDialog
        skillMain.text = mainSkill.title
        skillMain.isVisible = true

        val skillsOnly = mainSkill.subCategories.isNotEmpty() && mainSkill.subCategories.size < 2 && mainSkill.subCategories[0].subtitle.isEmpty()
        if (skillsOnly){
            skillSub.isVisible = true
            skillSub.setText(R.string.skills)
        }

        btnEditMainCategory.setOnClickListener { mainEditDialog.edit(mainSkill, items) }
        btnDeleteMainCategory.setOnClickListener { mainEditDialog.delete(mainSkill, items) }
        if (skillsOnly){
            btnAddSubCategory.setOnClickListener {
                Snackbar.make(
                    root,
                    "First SubCategory must have a Name in order to add more SubCategories",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            btnEditSubCategory.setOnClickListener {
                subEditDialog.edit(mainSkill.subCategories[0], mainSkill)
            }
            btnDeleteSubCategory.setOnClickListener { subEditDialog.delete(mainSkill.subCategories[0], mainSkill) }
        }else {
            btnAddSubCategory.setOnClickListener { subEditDialog.add(mainSkill) }
        }
    }

    fun activateTouchHelper(): ItemTouchHelper{
        touchHelper = ItemTouchHelper(object: ProfileItemsTouchCallback(){
            override fun updateItem(oldPosition: Int, newPosition: Int) {
                val newItems = ArrayList(items)
                Collections.swap(newItems, oldPosition, newPosition)
                items = newItems
            }
        })
        return touchHelper
    }

    fun skillsMainList(): List<SkillsMain> = items
}