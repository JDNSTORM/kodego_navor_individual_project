package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain

class SkillsMainAdapter(): ItemsAdapter<SkillsMain>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillsMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemSkillsMainBinding
        val mainSkill = items[position]
        bind(binding, mainSkill)
    }

    private fun bind(binding: ItemSkillsMainBinding, mainSkill: SkillsMain) {
        with(binding){
            skillMain.text = mainSkill.title
            val itemsAdapter = SkillsSubAdapter()

            listSkillSub.layoutManager = LinearLayoutManager(root.context)
            listSkillSub.adapter = itemsAdapter
            itemsAdapter.setList(mainSkill.subCategories)
        }
    }
}