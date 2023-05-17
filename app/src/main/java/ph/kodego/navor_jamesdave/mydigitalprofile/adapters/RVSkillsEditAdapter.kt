package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

class RVSkillsEditAdapter(private val subCategory: SkillSubCategory): ItemsAdapter(subCategory.skills) {
    private lateinit var dao: FirebaseSkillsDAO

    init {
        if (subCategory.subCategoryID.isNotEmpty()){
            dao = FirebaseSkillsDAOImpl(subCategory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//TODO: Add color stripes per row
//        super.onBindViewHolder(holder, position) //TODO: Binding Extension
        val skill = subCategory.skills[position]
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
            if (subCategory.subCategoryID.isNotEmpty()){
                updateOnFirestore(skill)
            }
            binding.skill.isEnabled = false
            binding.btnEditSkill.visibility = View.VISIBLE
            binding.btnSaveSkill.visibility = View.GONE
        }
        binding.btnDeleteSkill.setOnClickListener {
            if (subCategory.subCategoryID.isNotEmpty()){
                deleteOnFirestore(skill)
            }
            subCategory.skills.remove(skill)
            notifyItemRemoved(holder.layoutPosition)
        }
    }

    private fun updateOnFirestore(skill: Skill){
        val fields: HashMap<String, Any?> = hashMapOf("skill" to skill.skill)
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateSkill(skill, fields)
        }
    }
    private fun deleteOnFirestore(skill: Skill){
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteSkill(skill)
        }
    }
}