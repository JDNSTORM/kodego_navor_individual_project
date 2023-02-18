package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Skill
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

class RVSkillsAdapter(private val mainCategory: SkillMainCategory, private val subCategory: SkillSubCategory, private val parentViewHolder: ViewHolder): RecyclerView.Adapter<ViewHolder>() {
    private var adapterEvents: RVSkillsMainAdapter.AdapterEvents? = null

    fun setAdapterEvents(adapterEvents: RVSkillsMainAdapter.AdapterEvents?){
        this.adapterEvents = adapterEvents
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subCategory.skills.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = subCategory.skills[position]
        val binding = holder.binding as ViewholderSkillBinding

        binding.skill.setText(skill.skill)
        binding.root.setOnClickListener{
            adapterEvents?.subCategoryClick(mainCategory, subCategory, parentViewHolder)
        }
    }
}