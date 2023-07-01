package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

sealed interface ViewedProfileState{
    object Inactive: ViewedProfileState
    data class Active(val profile: Flow<Profile?>, val uid: String, val profileID: String): ViewedProfileState
    object Invalid: ViewedProfileState
}
