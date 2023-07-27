package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle

class ExitWarningDialog(
    context: Context,
    private val exit: () -> Unit
): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle("Go Back to Home?")
        setMessage("Closing will send you back to Home. Are you sure?")
        setCancelable(false)
        setButton(BUTTON_NEGATIVE, "No"){_,_ ->
            dismiss()
        }
        setButton(BUTTON_POSITIVE, "Yes"){_, _ ->
            exit()
            dismiss()
        }
        super.onCreate(savedInstanceState)
    }
}