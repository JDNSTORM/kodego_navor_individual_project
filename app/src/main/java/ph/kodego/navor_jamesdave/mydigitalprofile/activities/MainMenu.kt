package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelStoreOwner
import ph.kodego.navor_jamesdave.mydigitalprofile.R

class MainMenu(
    private val context: Context,
    private val viewDeveloperProfile: () -> Unit
): MenuProvider{
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.btn_about -> {
                AboutAppDialog(context, viewDeveloperProfile).show()
                true
            }
            else -> true
        }
    }
}