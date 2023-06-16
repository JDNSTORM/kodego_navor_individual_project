package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.btnEditSkill.setOnClickListener { toggleEdit(binding) }
        binding.btnSaveSkill.setOnClickListener { update(binding, skill) }
        binding.btnDeleteSkill.setOnClickListener { delete(skill) }
        binding.handle.apply {
            setOnClickListener { }
            setOnTouchListener { v, event ->
                when(event.action){
                    KeyEvent.ACTION_DOWN -> {
                        touchHelper.startDrag(holder)
                        true
                    }
                    else -> {
                        v.performClick()
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

    private fun update(binding: ItemSkillEditBinding, skill: String){
        val updatedSkill = binding.skill.text.toString().trim()
        if (updatedSkill.isNotEmpty()) {
            val newItems = ArrayList(items)
            val index = newItems.indexOf(skill)
            newItems[index] = updatedSkill
            setList(newItems)
            binding.skill.isEnabled = false
            binding.btnEditSkill.visibility = View.VISIBLE
            binding.btnSaveSkill.visibility = View.GONE
        }
    }

    private fun toggleEdit(binding: ItemSkillEditBinding){
        with(binding) {
            skill.isEnabled = true
            btnEditSkill.visibility = View.GONE
            btnSaveSkill.visibility = View.VISIBLE
            skill.requestFocus()
        }
    }

    fun skills(): List<String> = items

    fun add(skill: String){
        val newItems = ArrayList(items).apply { add(skill) }
        setList(newItems)
    }
}