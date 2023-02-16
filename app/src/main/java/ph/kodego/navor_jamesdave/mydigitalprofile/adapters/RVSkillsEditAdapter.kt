package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill

class RVSkillsEditAdapter(private val skillList: ArrayList<Skill>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return skillList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = skillList[position]
        val binding = holder.binding as ViewholderSkillEditBinding

        binding.skill.setText(skill.skill)
        binding.btnEditSkill.setOnClickListener {
            binding.skill.isEnabled = true
            binding.btnEditSkill.visibility = View.GONE
            binding.btnSaveSkill.visibility = View.VISIBLE
            binding.skill.requestFocus()
        }
        binding.btnSaveSkill.setOnClickListener {
            skill.skill = binding.skill.text.toString().trim()
            binding.skill.isEnabled = false
            binding.btnEditSkill.visibility = View.VISIBLE
            binding.btnSaveSkill.visibility = View.GONE
        }
        binding.btnDeleteSkill.setOnClickListener {
            skillList.remove(skill)
            notifyItemRemoved(holder.layoutPosition)
        }
    }
}