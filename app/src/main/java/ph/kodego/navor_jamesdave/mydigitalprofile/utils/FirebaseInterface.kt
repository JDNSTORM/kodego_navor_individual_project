package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

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

interface FirebaseAccountInterface: FirebaseInterface{
    fun getAccountSuccess(account: Account)
    fun getAccountFailed()
}