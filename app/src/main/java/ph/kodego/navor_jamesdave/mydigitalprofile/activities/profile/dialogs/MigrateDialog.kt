package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle

abstract class MigrateDialog(context: Context): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle("Account Not Found. Migrate?")
        setMessage("If you previously had an existing account, you can still migrate it. Continue?")
        setButton(BUTTON_NEGATIVE, "No", ifNo())
        setButton(BUTTON_POSITIVE, "Yes", ifYes())
        super.onCreate(savedInstanceState)
    }

    abstract fun ifYes(): DialogInterface.OnClickListener
    abstract fun ifNo(): DialogInterface.OnClickListener
}