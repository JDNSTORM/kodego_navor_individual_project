package ph.kodego.navor_jamesdave.mydigitalprofile.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueSkillMainEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderSkillsMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseSkillsMainCategoryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillMainCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillSubCategory
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.expandFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.minimizeFabs

class RVSkillsMainAdapter(private val skillsMain: ArrayList<SkillMainCategory>): RecyclerView.Adapter<ViewHolder>() {
    private var layoutSkillEventsBinding: LayoutSkillEventsBinding? = null
    private lateinit var lifecycleScope: CoroutineScope
    private lateinit var dao: FirebaseSkillsMainCategoryDAOImpl
    private lateinit var dialogueSkillMainEditBinding: DialogueSkillMainEditBinding
    private lateinit var editDialogue: AlertDialog

    interface AdapterEvents{
        fun mainCategoryClick(mainCategory: SkillMainCategory)
        fun subCategoryClick(mainCategory: SkillMainCategory, subCategory: SkillSubCategory)
    }
    private var adapterEvents: AdapterEvents? = null
    fun setAdapterEvents(adapterEvents: AdapterEvents){
        this.adapterEvents = adapterEvents
    }

    fun setEditingInterface(daoImpl: FirebaseSkillsMainCategoryDAOImpl, binding: LayoutSkillEventsBinding){
        dao = daoImpl
        layoutSkillEventsBinding = binding
        lifecycleScope = CoroutineScope(Dispatchers.Main.immediate)
        dialogueSkillMainEditBinding = DialogueSkillMainEditBinding.inflate(LayoutInflater.from(binding.root.context))
        val builder = AlertDialog.Builder(binding.root.context).setView(dialogueSkillMainEditBinding.root)
        editDialogue = builder.create()
        editDialogue.setCancelable(false)
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
            skillMain.text = skillMainCategory.categoryMain
            val skillSubAdapter = RVSkillSubAdapter(skillMainCategory)
            skillSubAdapter.setAdapterEvents(adapterEvents)

            listSkillSub.layoutManager = LinearLayoutManager(root.context)
            listSkillSub.adapter = skillSubAdapter

            root.setOnClickListener {
//                adapterEvents?.holderClickNotify(position) ?: Log.e("AdapterError", "adapterActions not set")
//                adapterEvents?.mainCategoryClick(skillMainCategory)
                layoutSkillEventsBinding?.expandFabs(holder, skillMainCategory)
            }
        }
    }

    private fun LayoutSkillEventsBinding.expandFabs(holder: ViewHolder, mainCategory: SkillMainCategory){
        expandFabs()
        skillMain.text = mainCategory.categoryMain
        skillMain.visibility = View.VISIBLE
        btnEditMainCategory.setOnClickListener {
            editMainCategoryDialogue(holder, mainCategory)
        }
        btnDeleteMainCategory.setOnClickListener {
            deleteCategoryDialogue(holder, mainCategory)
        }
    }

    private fun deleteCategoryDialogue(holder: ViewHolder, mainCategory: SkillMainCategory) {
        val builder = AlertDialog.Builder(holder.itemView.context)
        val categoryName = mainCategory.categoryMain
        builder.apply {
            setTitle("Delete Main Category?")
            setMessage("Are you sure to delete $categoryName and all of its components?")
            setPositiveButton("Yes"){ dialog, _ ->
                Toast.makeText(holder.itemView.context, "$categoryName be deleted in the background", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    if(dao.deleteMainCategory(mainCategory)){
                        skillsMain.remove(mainCategory)
                        notifyItemRemoved(holder.layoutPosition)
                        Toast.makeText(holder.itemView.context, "Successfully deleted $categoryName", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(holder.itemView.context, "Error Deleting $categoryName", Toast.LENGTH_SHORT).show()
                    }
                }
                layoutSkillEventsBinding!!.minimizeFabs()
                dialog.dismiss()
            }
            setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun editMainCategoryDialogue(holder: ViewHolder, mainCategory: SkillMainCategory){
        val index = skillsMain.indexOf(mainCategory)
        if (skillsMain.contains(mainCategory)){
            dialogueSkillMainEditBinding.skillMain.setText(mainCategory.categoryMain)
            dialogueSkillMainEditBinding.editButtons.btnSave.visibility = View.GONE
            dialogueSkillMainEditBinding.editButtons.btnUpdate.visibility = View.VISIBLE
        }else{
            dialogueSkillMainEditBinding.skillMain.text?.clear()
            dialogueSkillMainEditBinding.editButtons.btnSave.visibility = View.VISIBLE
            dialogueSkillMainEditBinding.editButtons.btnUpdate.visibility = View.GONE
        }

        with(dialogueSkillMainEditBinding.editButtons) {
            btnCancel.setOnClickListener {
                editDialogue.dismiss()
            }
            btnUpdate.setOnClickListener {
                val updatedMainCategory = mainCategory.copy()
                updatedMainCategory.subCategories.addAll(mainCategory.subCategories)
                updatedMainCategory.categoryMain = dialogueSkillMainEditBinding.skillMain.text.toString().trim()
                val modified = FormControls().getModified(mainCategory, updatedMainCategory)
                modified.remove("subCategories")
                if (modified.isNotEmpty()) {
                    lifecycleScope.launch {
                        Toast.makeText(root.context, "Main Category will be updated in the background", Toast.LENGTH_SHORT).show()
                        if(dao.updateMainCategory(mainCategory, modified)){
                            skillsMain[index] = updatedMainCategory
                            notifyItemChanged(holder.layoutPosition)
                            Toast.makeText(root.context, "Main Category Updated Successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(root.context, "Error updating Main Category", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(root.context, "No Fields Changed", Toast.LENGTH_SHORT).show()
                }
                layoutSkillEventsBinding!!.minimizeFabs()
                editDialogue.dismiss()
            }
        }
        editDialogue.show()
        dialogueSkillMainEditBinding.skillMain.requestFocus()
    }
}