package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillSubBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory

class RVSkillSubAdapter(private val mainCategories: ArrayList<SkillMainCategory>): RecyclerView.Adapter<ViewHolder>() {
    private var categoryPosition: Int? = null
    private var adapterActions: RVSkillsMainAdapter.AdapterActions? = null
    var skillSubAdapter: RVSkillSubAdapter? = null

    fun setCategoryPosition(position: Int){
        this.categoryPosition = position
    }
    fun setAdapterActions(adapterActions: RVSkillsMainAdapter.AdapterActions){
        this.adapterActions = adapterActions
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
            if (skillSubCategory.skills.isNotEmpty()) {
                skillSub.setText(skillSubCategory.categorySub)
                skillSub.visibility = View.VISIBLE
            }
            val skillsAdapter = RVSkillsAdapter(skillSubCategory.skills)
            skillsAdapter.setAdapterActions(adapterActions!!)
            skillsAdapter.setCategoryPosition(categoryPosition!!)

            listSkill.layoutManager = GridLayoutManager(context, 2)
            listSkill.adapter = skillsAdapter

            root.setOnClickListener {
//                Snackbar.make(root, "Sub Category Clicked", Snackbar.LENGTH_SHORT).show()
                adapterActions?.holderClickNotify(categoryPosition!!)
                //TODO: Possibly pass adapter to listener
                adapterActions?.subCategoryClick(mainCategories, categoryPosition!!, position, skillSubAdapter!!)
            }
        }

    }
}