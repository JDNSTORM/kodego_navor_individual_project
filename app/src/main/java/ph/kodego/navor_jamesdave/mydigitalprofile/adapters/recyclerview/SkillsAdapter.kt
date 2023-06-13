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
        bind(binding, skill)
        editor?.let {
            binding.root.setOnClickListener (it)
        }
    }

    private fun bind(binding: ItemSkillBinding, skill: String) {
        binding.skill.text = skill
    }

    fun enableEditing(listener: OnClickListener){
        editor = listener
    }
}