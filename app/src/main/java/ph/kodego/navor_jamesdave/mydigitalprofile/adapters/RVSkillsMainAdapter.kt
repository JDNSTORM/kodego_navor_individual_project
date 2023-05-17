package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

class RVSkillsMainAdapter(private val skillsMain: ArrayList<SkillMainCategory>): ItemsAdapter(skillsMain) {
    interface AdapterEvents{
        fun mainCategoryClick(mainCategory: SkillMainCategory)
        fun subCategoryClick(mainCategory: SkillMainCategory, subCategory: SkillSubCategory)
    }
    private var adapterEvents: AdapterEvents? = null
    fun setAdapterEvents(adapterEvents: AdapterEvents){
        this.adapterEvents = adapterEvents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderSkillsMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        super.onBindViewHolder(holder, position) //TODO: Binding Extension
        val skillMainCategory = skillsMain[position]
        val binding = holder.binding as ViewholderSkillsMainBinding

        with(binding){
            skillMain.text = skillMainCategory.categoryMain
            val skillSubAdapter = RVSkillSubAdapter(skillMainCategory)
            skillSubAdapter.setAdapterEvents(adapterEvents)

            listSkillSub.layoutManager = LinearLayoutManager(root.context)
            listSkillSub.adapter = skillSubAdapter

            if (adapterEvents != null) {
                root.setOnClickListener {
                    adapterEvents!!.mainCategoryClick(skillMainCategory)
                }
            }
        }
    }
}