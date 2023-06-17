package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillEditBinding
import java.util.Collections

class SkillsEditAdapter(): ItemsAdapter<String>() {
    val touchHelper = ItemTouchHelper(object: ProfileItemsTouchCallback(){
        override fun updateItem(oldPosition: Int, newPosition: Int) {
            val newItems = ArrayList(items)
            Collections.swap(newItems, oldPosition, newPosition)
            items = newItems
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillEditBinding
        val skill = items[position]
        binding.skill.setText(skill)

        binding.btnEditSkill.setOnClickListener { binding.toggleEdit() }
        binding.btnSaveSkill.setOnClickListener { binding.update(skill) }
        binding.btnDeleteSkill.setOnClickListener { delete(skill) }
        binding.handle.apply {
            setOnClickListener { }
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

    private fun delete(skill: String) {
        val newItems = ArrayList(items).apply { remove(skill) }
        setList(newItems)
    }

    private fun ItemSkillEditBinding.update(skillName: String){
        val updatedSkill = skill.text.toString().trim()
        if (updatedSkill.isNotEmpty()) {
            val newItems = ArrayList(items)
            val index = newItems.indexOf(skillName)
            newItems[index] = updatedSkill
            setList(newItems)
            skill.isEnabled = false
            btnEditSkill.isVisible = true
            btnSaveSkill.isVisible = false
        }
    }

    private fun ItemSkillEditBinding.toggleEdit(){
        skill.isEnabled = true
        btnEditSkill.isVisible = false
        btnSaveSkill.isVisible = true
        skill.requestFocus()
    }

    fun skills(): List<String> = items

    fun add(skill: String){
        val newItems = ArrayList(items).apply { add(skill) }
        setList(newItems)
    }
}