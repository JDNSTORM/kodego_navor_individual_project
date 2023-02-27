package ph.kodego.navor_jamesdave.mydigitalprofile.utils

class FormControls {
    fun validateText(text: String): Boolean{
        return text.isNotEmpty()
    }
    fun validateEmail(email: String): Boolean{
        val emailRegex: Regex = Regex("^[A-Za-z](.*)(@+)(.+)(\\.)(.+)")
        return email.matches(emailRegex)
    }
    fun validatePassword(password: String, confirmPassword: String): Boolean{
        return password == confirmPassword
    }
}