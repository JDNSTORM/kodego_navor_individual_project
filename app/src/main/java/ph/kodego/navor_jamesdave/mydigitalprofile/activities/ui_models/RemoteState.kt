package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

/**
 * State used as Callback for Remote Actions such as Add, Update, Delete
 * This State will be used as an alternative in case AccountState needs to be retained
 */
enum class RemoteState {
    Idle,
    Waiting,
    Success,
    Failed,
    Invalid
}