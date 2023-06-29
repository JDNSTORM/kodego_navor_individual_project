package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

sealed class ProfileAction{
    data class Select(val profile: Profile? = null): ProfileAction()
    data class Create(val profession: String): ProfileAction()
    data class Update(val profile: Profile, val changes: Map<String, Any?>): ProfileAction()
    data class Delete(val profile: Profile): ProfileAction()
}
