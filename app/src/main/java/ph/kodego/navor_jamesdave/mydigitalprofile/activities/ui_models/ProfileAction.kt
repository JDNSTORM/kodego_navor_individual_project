package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

sealed interface ProfileAction{
    data class Select(val profile: Profile? = null): ProfileAction
    data class Create(val profession: String): ProfileAction
    data class Update(val changes: Map<String, Any?>, val profile: Profile? = null): ProfileAction
    data class Delete(val profile: Profile): ProfileAction
}
