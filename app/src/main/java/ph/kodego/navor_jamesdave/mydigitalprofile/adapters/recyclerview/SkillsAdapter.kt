package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillBinding

class SkillsAdapter(): ItemsAdapter<String>() {
    private var editor: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillBinding
        val skill = items[position]
        binding.bind(skill)
        editor?.apply {
            binding.root.setOnClickListener (this)
        }
    }

    private fun ItemSkillBinding.bind(skillName: String) {
        skill.text = skillName
    }

    fun enableEditing(listener: OnClickListener){
        editor = listener
    }
}