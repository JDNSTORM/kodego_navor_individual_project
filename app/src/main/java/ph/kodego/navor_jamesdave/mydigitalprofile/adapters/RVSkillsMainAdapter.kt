package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory

/**
 * TODO: Setting ClickListener inside ViewHolder is inefficient because it is redundant but same
 * TODO: Nested RecyclerViews are not affected by ViewHolder.OnClickListener
 */
class RVSkillsMainAdapter(private val skillsMain: ArrayList<SkillMainCategory>): RecyclerView.Adapter<ViewHolder>() {
    interface AdapterEvents{//TODO: Open clickEvent when clicking Skill
        fun holderClickNotify(position: Int)
        fun mainCategoryClick(mainCategories: ArrayList<SkillMainCategory>, position: Int, skillSubAdapter: RVSkillSubAdapter)
        fun subCategoryClick(mainCategories: ArrayList<SkillMainCategory>, mainCategoryPosition: Int, subCategoryPosition: Int, skillSubAdapter: RVSkillSubAdapter)
    }
    private var adapterEvents: AdapterEvents? = null
    fun setAdapterActions(adapterEvents: AdapterEvents){
        this.adapterEvents = adapterEvents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillsMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return skillsMain.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skillMainCategory = skillsMain[position]
        val binding = holder.binding as ViewholderSkillsMainBinding

        with(binding){
            skillMain.setText(skillMainCategory.categoryMain)
            val skillSubAdapter = RVSkillSubAdapter(skillsMain)
            skillSubAdapter.setAdapterActions(adapterEvents)
            skillSubAdapter.setCategoryPosition(position)

            listSkillSub.layoutManager = LinearLayoutManager(root.context)
            listSkillSub.adapter = skillSubAdapter

            root.setOnClickListener {
                adapterEvents?.holderClickNotify(position) ?: Log.e("AdapterError", "adapterActions not set")
                adapterEvents?.mainCategoryClick(skillsMain, position, skillSubAdapter) //TODO: Possibly pass holder instead of adapter
            }
        }
    }
}