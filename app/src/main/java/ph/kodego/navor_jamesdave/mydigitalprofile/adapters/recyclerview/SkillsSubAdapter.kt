package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

class SkillsSubAdapter(): ItemsAdapter<SkillsSub>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsSubBinding
        val subSkill = items[position]
        bind(binding, subSkill)
    }

    private fun bind(binding: ItemSkillsSubBinding, subSkill: SkillsSub) {
        with(binding){
            if (subSkill.subtitle.isNotEmpty()) {
                skillSub.text = subSkill.subtitle
                skillSub.visibility = View.VISIBLE
            }
            val itemsAdapter = SkillsAdapter()
            if(subSkill.skills.isNotEmpty()) {
                listSkill.visibility = View.VISIBLE
                listSkill.layoutManager = GridLayoutManager(root.context, 2)
                listSkill.adapter = itemsAdapter
                itemsAdapter.setList(subSkill.skills)
            }
        }
    }
}