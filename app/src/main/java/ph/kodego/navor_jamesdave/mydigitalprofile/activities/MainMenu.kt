package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelStoreOwner
import ph.kodego.navor_jamesdave.mydigitalprofile.R

class MainMenu<T>(private val context: T): MenuProvider where T: Context, T: ViewModelStoreOwner{
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.btn_about -> {
                AboutAppDialog(context).show()
                true
            }
            else -> true
        }
    }
}