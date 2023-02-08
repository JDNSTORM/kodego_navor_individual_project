package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill

class RVSkillsAdapter(private val skillList: ArrayList<Skill>): RecyclerView.Adapter<RVSkillsAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ViewholderSkillBinding): RecyclerView.ViewHolder(binding.root){
        fun bindViewHolder(skill: Skill, position: Int){
            binding.skill.setText(skill.skill)

//            binding.root.setOnClickListener {
//                Toast.makeText(itemView.context, "Skill Clicked", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(skillList[position], position)
    }
}