package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

class ExitWarningDialog(
    context: Context,
    private val exit: () -> Unit
): MaterialAlertDialogBuilder(context) {

    init {
        setTitle("Go Back to Home?")
        setMessage("Closing will send you back to Home. Are you sure?")
        setCancelable(false)
        setPositiveButton("Yes"){ dialog, _ ->
            exit()
            dialog.dismiss()
        }
        setNegativeButton("No"){ dialog, _ ->
            dialog.dismiss()
        }
    }
}