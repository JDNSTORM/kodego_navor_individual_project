package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory

class RVSkillSubAdapter(private val mainCategory: SkillMainCategory): RecyclerView.Adapter<ViewHolder>() {
    private var adapterEvents: RVSkillsMainAdapter.AdapterEvents? = null

    fun setAdapterEvents(adapterEvents: RVSkillsMainAdapter.AdapterEvents?){
        this.adapterEvents = adapterEvents
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mainCategory.subCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subCategory = mainCategory.subCategories[position]
        val binding = holder.binding as ViewholderSkillSubBinding
        val context = binding.root.context

        with(binding){
            if (subCategory.categorySub.isNotEmpty()) {
                skillSub.text = subCategory.categorySub
                skillSub.visibility = View.VISIBLE
            }
            val skillsAdapter = RVSkillsAdapter(mainCategory, subCategory)
            if(subCategory.skills.isNotEmpty()) {
                skillsAdapter.setAdapterEvents(adapterEvents)
                listSkill.visibility = View.VISIBLE
                listEmpty.visibility = View.GONE
                listSkill.layoutManager = GridLayoutManager(context, 2)
                listSkill.adapter = skillsAdapter
            }else{
                listSkill.visibility = View.GONE
                listEmpty.visibility = View.GONE
            }

            if (adapterEvents != null) {
                root.setOnClickListener {
                    adapterEvents!!.subCategoryClick(mainCategory, subCategory)
                }
            }
        }

    }
}