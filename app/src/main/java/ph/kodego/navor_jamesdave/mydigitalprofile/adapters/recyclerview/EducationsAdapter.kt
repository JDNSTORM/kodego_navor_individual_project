package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education.EducationEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import java.util.Collections

class EducationsAdapter(): ItemsAdapter<Education>() {
    private var drag: Boolean = false
    private var editDialog: EducationEditDialog<*>? = null
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemEducationBinding
        val education = items[position]
        bind(binding, education)

        editDialog?.let {
            binding.root.setOnClickListener { _ -> it.edit(education, items) }
        }
        if (this::touchHelper.isInitialized){
            with(binding.handle){
                setOnClickListener {  }
                setOnTouchListener { v, event ->
                    when(event.action){
                        KeyEvent.ACTION_DOWN -> {
                            touchHelper.startDrag(holder)
                            true
                        }
                        else -> {
                            v.performClick()
                            false
                        }
                    }
                }
            }
        }
    }

    private fun bind(binding: ItemEducationBinding, education: Education) {
        with(binding) {
            schoolName.text = education.schoolName
            degree.text = education.degree
            fieldOfStudy.text = education.fieldOfStudy
            if(!drag) {
                llEnrollmentPeriod.visibility = View.VISIBLE
                divider.visibility = View.VISIBLE
                handle.visibility = View.GONE
                dateEnrolled.text = education.dateEnrolled
                dateGraduated.text = education.dateGraduated
                val address = education.address.streetAddress
                if (address.isNotEmpty()) {
                    schoolAddress.text = address
                    schoolAddress.visibility = View.VISIBLE
                }
                val website = education.website
                if (website.isNotEmpty()) {
                    schoolWebsite.text = website
                    schoolWebsite.visibility = View.VISIBLE
                }
                val telephone = education.contactNumber.telephone()
                if (telephone.isNotEmpty()) {
                    schoolTelephone.text = telephone
                    schoolTelephone.visibility = View.VISIBLE
                }
            }else{
                llEnrollmentPeriod.visibility = View.GONE
                divider.visibility = View.GONE
                handle.visibility = View.VISIBLE
                handle.setOnClickListener{}
            }
        }
    }

    fun enableEditing(dialog: EducationEditDialog<*>){
        editDialog = dialog
    }

    fun toggleDrag(): Boolean{
        drag = !drag
        notifyItemRangeChanged(0, itemCount)
        return drag
    }

    fun clearToggle(){
        if (drag) toggleDrag()
    }

    fun activateTouchHelper(): ItemTouchHelper{
        touchHelper = ItemTouchHelper(object: ProfileItemsTouchCallback(){
            override fun updateItem(oldPosition: Int, newPosition: Int) {
                val newItems = ArrayList(items)
                Collections.swap(newItems, oldPosition, newPosition)
                items = newItems
            }
        })
        return touchHelper
    }

    fun educations(): List<Education> = items
}