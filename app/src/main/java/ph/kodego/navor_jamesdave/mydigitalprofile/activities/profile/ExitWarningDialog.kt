package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.os.Bundle

class ExitWarningDialog(context: Context): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle("Go Back to Home?")
        setMessage("Closing will send you back to Home. Are you sure?")
        setCancelable(false)
        setButton(BUTTON_NEGATIVE, "No"){_,_ ->
            dismiss()
        }
        super.onCreate(savedInstanceState)
    }

    fun ifYes(listener: OnClickListener){
        setButton(BUTTON_POSITIVE, "Yes", listener)
    }
}