package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding

fun LayoutSkillEventsBinding.minimizeFabs() {
    layoutBackground.visibility = View.GONE
    efabSkillsOptions.shrink()
    btnAddMainCategory.hide()
    btnEditMainCategory.hide()
    btnDeleteMainCategory.hide()
    labelMainCategoryFab.visibility = View.GONE
    btnAddSubCategory.hide()
    btnEditSubCategory.hide()
    btnDeleteSubCategory.hide()
    labelSubCategoryFab.visibility = View.GONE

    skillMain.visibility = View.GONE
    skillSub.visibility = View.GONE
}

fun LayoutSkillEventsBinding.expandFabs(){
    layoutBackground.visibility = View.VISIBLE
    efabSkillsOptions.extend()
    btnAddMainCategory.show()
    btnEditMainCategory.show()
    btnDeleteMainCategory.show()
    labelMainCategoryFab.visibility = View.VISIBLE
    btnAddSubCategory.show()
    btnEditSubCategory.show()
    btnDeleteSubCategory.show()
    labelSubCategoryFab.visibility = View.VISIBLE

    skillMain.visibility = View.GONE
    skillSub.visibility = View.GONE
    btnEditMainCategory.setOnClickListener {
        Snackbar.make(root, "A Main Category must be selected in order to edit.", Snackbar.LENGTH_SHORT).show()
    }
    btnDeleteMainCategory.setOnClickListener {
        Snackbar.make(root, "A Main Category must be selected in order to delete.", Snackbar.LENGTH_SHORT).show()
    }
    btnAddSubCategory.setOnClickListener {
        Snackbar.make(root, "A Sub Category must be selected in order to add.", Snackbar.LENGTH_SHORT).show()
    }
    btnEditSubCategory.setOnClickListener {
        Snackbar.make(root, "A Sub Category must be selected in order to edit.", Snackbar.LENGTH_SHORT).show()
    }
    btnDeleteSubCategory.setOnClickListener {
        Snackbar.make(root, "A Sub Category must be selected in order to delete.", Snackbar.LENGTH_SHORT).show()
    }
}