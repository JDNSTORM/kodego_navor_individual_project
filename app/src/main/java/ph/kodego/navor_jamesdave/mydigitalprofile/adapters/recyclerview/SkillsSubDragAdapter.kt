package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsSubDragBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

class SkillsSubDragAdapter(): ItemsAdapter<SkillsSub>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsSubDragBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsSubDragBinding
        val subSkill = items[position]
        bind(binding, subSkill)
    }

    private fun bind(binding: ItemSkillsSubDragBinding, subSkill: SkillsSub) {
        binding.subtitle.text = subSkill.subtitle
    }

    fun skills(): List<SkillsSub> = items
}