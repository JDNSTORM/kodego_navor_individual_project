package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory

class RVSkillSubAdapter(private val mainCategories: ArrayList<SkillMainCategory>): RecyclerView.Adapter<ViewHolder>() {
    private var categoryPosition: Int? = null
    private var adapterEvents: RVSkillsMainAdapter.AdapterEvents? = null

    fun setCategoryPosition(position: Int){
        this.categoryPosition = position
    }
    fun setAdapterActions(adapterEvents: RVSkillsMainAdapter.AdapterEvents){
        this.adapterEvents = adapterEvents
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mainCategories[categoryPosition!!].subCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skillSubCategory = mainCategories[categoryPosition!!].subCategories[position]
        val binding = holder.binding as ViewholderSkillSubBinding
        val context = binding.root.context

        with(binding){
            if (skillSubCategory.categorySub.isNotEmpty()) {
                skillSub.setText(skillSubCategory.categorySub)
                skillSub.visibility = View.VISIBLE
            }
            val skillsAdapter = RVSkillsAdapter(skillSubCategory.skills)
            if(skillSubCategory.skills.isNotEmpty()) {
                skillsAdapter.setAdapterActions(adapterEvents!!)
                skillsAdapter.setCategoryPosition(categoryPosition!!)
                listSkill.visibility = View.VISIBLE
                listEmpty.visibility = View.GONE
                listSkill.layoutManager = GridLayoutManager(context, 2)
                listSkill.adapter = skillsAdapter
            }else{
                listSkill.visibility = View.GONE
                listEmpty.visibility = View.GONE
            }

            root.setOnClickListener {//TODO: Possibly pass Adapter with public list to lessen data passed
                adapterEvents?.holderClickNotify(categoryPosition!!)
                adapterEvents?.subCategoryClick(mainCategories, categoryPosition!!, position, this@RVSkillSubAdapter)
            }
        }

    }
}