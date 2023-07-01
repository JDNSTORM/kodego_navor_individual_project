package ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education.EducationEditDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ItemEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import java.util.Collections

class EducationsAdapter(): ItemsAdapter<Education>() {
    private var drag: Boolean = false
    private var editDialog: EducationEditDialog? = null
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding as ItemEducationBinding
        val education = items[position]
        binding.bind(education)

        editDialog?.apply {
            binding.root.setOnClickListener { edit(education, items) }
        }
        if (this::touchHelper.isInitialized){
            binding.handle.apply{
                setOnClickListener {  }
                setOnTouchListener { _, event ->
                    when(event.action){
                        KeyEvent.ACTION_DOWN -> {
                            touchHelper.startDrag(holder)
                            true
                        }
                        else -> {
                            performClick()
                            false
                        }
                    }
                }
            }
        }
    }

    private fun ItemEducationBinding.bind(education: Education){
        schoolName.text = education.schoolName
        degree.text = education.degree
        fieldOfStudy.text = education.fieldOfStudy
        llEnrollmentPeriod.isVisible = !drag
        divider.isVisible = !drag
        handle.isVisible = drag
        dateEnrolled.text = education.dateEnrolled
        dateGraduated.text = education.dateGraduated
        schoolAddress.apply {
            text = education.address.streetAddress
            isVisible = text.isNotEmpty() && !drag
        }
        schoolWebsite.apply {
            text = education.website
            isVisible = text.isNotEmpty() && !drag
        }
        schoolTelephone.apply {
            text = education.contactNumber.telephone()
            isVisible = text.isNotEmpty() && !drag
        }
    }

    fun enableEditing(dialog: EducationEditDialog){
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