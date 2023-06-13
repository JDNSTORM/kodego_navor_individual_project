package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillEditBinding

class SkillsEditAdapter(): ItemsAdapter<String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ViewholderSkillEditBinding
        val skill = items[position]
        binding.skill.setText(skill)

        binding.btnEditSkill.setOnClickListener { toggleEdit(binding) }
        binding.btnSaveSkill.setOnClickListener { update(binding, skill) }
        binding.btnDeleteSkill.setOnClickListener { delete(skill) }
    }

    private fun delete(skill: String) {
        val newItems = ArrayList(items).apply { remove(skill) }
        setList(newItems)
    }

    private fun update(binding: ViewholderSkillEditBinding, skill: String){
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

    private fun toggleEdit(binding: ViewholderSkillEditBinding){
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