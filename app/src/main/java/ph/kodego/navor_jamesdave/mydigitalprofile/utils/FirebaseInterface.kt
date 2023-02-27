package ph.kodego.navor_jamesdave.mydigitalprofile.utils

interface FirebaseInterface {
    fun showProgressDialog()
    fun hideProgressDialog()
}

interface FirebaseRegisterInterface: FirebaseInterface{
    fun userRegistrationFail(message: String)
    fun accountRegistrationSuccess()
    fun accountRegistrationFail()
}

interface FirebaseLoginInterface: FirebaseInterface{
    fun signInSuccessful()
    fun signInFailed(message: String)
}