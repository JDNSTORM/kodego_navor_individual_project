package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

class DeleteItemDialog(
    context: Context,
    private val item: Any,
    private val delete: () -> Unit
): MaterialAlertDialogBuilder(context) {

    init {
        setTitleAndMessage()
        setNegativeButton("No"){ dialog, _ -> dialog.dismiss() }
        setPositiveButton("Yes"){ dialog, _ ->
            delete()
            dialog.dismiss()
        }
    }

    private fun setTitleAndMessage(){
        var itemName: String = "Item"
        when(item){
            is Career -> itemName = "Career"
            is Education -> itemName = "Education"
            is SkillsMain -> itemName = "Main Category"
            is SkillsSub -> itemName = "SubCategory"
            is Profile -> itemName = "Profile"
        }
        setTitle("Delete $itemName?")
        setMessage("Are you sure to delete this ${itemName.lowercase()}?")
    }
}