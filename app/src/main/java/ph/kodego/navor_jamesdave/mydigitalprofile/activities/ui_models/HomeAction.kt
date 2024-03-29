package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

sealed interface HomeAction{
    data class Search(val query: String = ""): HomeAction
    data class View(val profile: Profile? = null): HomeAction
}
