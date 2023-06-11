package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.os.Bundle

abstract class DeleteCareerDialog(context: Context): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle("Delete Career?")
        setMessage("Are you sure to delete this career?")
        setButton(BUTTON_NEGATIVE, "No"){ _, _ -> dismiss()}
        setButton(BUTTON_POSITIVE, "Yes", ifYes())
        super.onCreate(savedInstanceState)
    }

    abstract fun ifYes(): OnClickListener
}