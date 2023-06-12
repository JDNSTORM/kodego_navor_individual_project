package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain

class SkillsMainAdapter(): ItemsAdapter<SkillsMain>() {
    private var drag: Boolean = false

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
            title.text = mainSkill.title
            val itemsAdapter = SkillsSubAdapter()

            if (!drag) {
                handle.visibility = View.GONE
                dividerHorizontal.visibility = View.VISIBLE
                listSkillSub.visibility = View.VISIBLE
                listSkillSub.layoutManager = LinearLayoutManager(root.context)
                listSkillSub.adapter = itemsAdapter
                itemsAdapter.setList(mainSkill.subCategories)
            }else{
                handle.visibility = View.VISIBLE
                dividerHorizontal.visibility = View.GONE
                listSkillSub.visibility = View.GONE
            }
        }
    }

    fun toggleDrag(): Boolean{
        drag = !drag
        notifyItemRangeChanged(0, itemCount)
        return drag
    }
}