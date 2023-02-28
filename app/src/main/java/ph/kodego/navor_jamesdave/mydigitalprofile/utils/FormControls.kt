package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.content.Context
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ph.kodego.navor_jamesdave.mydigitalprofile.R

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

    fun setTextValidationListener(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout){
        val context = textInputEditText.context
        textInputEditText.doAfterTextChanged {text ->
            if(validateText(text.toString().trim())){
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }else{
                textInputLayout.error = context.getString(R.string.error_empty_field)
            }
        }
    }
}