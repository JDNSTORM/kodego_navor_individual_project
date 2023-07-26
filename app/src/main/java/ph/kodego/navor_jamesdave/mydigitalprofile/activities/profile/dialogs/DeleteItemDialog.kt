package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsMain
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.SkillsSub

class DeleteItemDialog(
    context: Context,
    private val item: Any,
    private val confirm: () -> Unit
): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitleAndMessage()
        setButton(BUTTON_NEGATIVE, "No"){ _, _ -> dismiss()}
        setButton(BUTTON_POSITIVE, "Yes"){_, _ ->
            confirm()
            dismiss()
        }
        super.onCreate(savedInstanceState)
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