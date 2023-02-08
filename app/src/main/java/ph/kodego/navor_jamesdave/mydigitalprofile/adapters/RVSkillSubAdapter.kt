package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

class RVSkillSubAdapter(private val subCategories: ArrayList<SkillSubCategory>): RecyclerView.Adapter<RVSkillSubAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ViewholderSkillSubBinding): RecyclerView.ViewHolder(binding.root){
        fun bindViewHolder(skillSub: SkillSubCategory, position: Int){
            if (skillSub.skills.isNotEmpty()) {
                binding.skillSub.setText(skillSub.categorySub)
                binding.skillSub.visibility = View.VISIBLE
            }
            val skillsAdapter = RVSkillsAdapter(skillSub.skills)
//            binding.listSkill.layoutManager = LinearLayoutManager(itemView.context)
            binding.listSkill.layoutManager = GridLayoutManager(itemView.context, 2)
            binding.listSkill.adapter = skillsAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(subCategories[position], position)
    }
}