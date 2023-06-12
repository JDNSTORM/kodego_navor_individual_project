package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import ph.kodego.navor_jamesdave.mydigitalprofile.R

abstract class ListMenu(): MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_list_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.btn_organize -> {
                onOrganize(menuItem)
                true
            }
            else -> true
        }
    }

    abstract fun onOrganize(menuItem: MenuItem)
}