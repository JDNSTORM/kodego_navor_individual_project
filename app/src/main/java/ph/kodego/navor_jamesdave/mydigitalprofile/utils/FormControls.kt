package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.text.InputType
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.models.*
import kotlin.reflect.full.memberProperties

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
        Log.i("Input Type Text", InputType.TYPE_CLASS_TEXT.toString())
        Log.i("Input Type Name", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.toString())
        Log.i("Input Type TextCapWords", InputType.TYPE_TEXT_FLAG_CAP_WORDS.toString())
        Log.i("Input Type", textInputEditText.inputType.toString())

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
    fun setValidationListener(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout){

    }

    fun getModified(source: Any, edited: Any): HashMap<String, Any?>{
        val modified: HashMap<String, Any?> = HashMap()
        val original: HashMap<String, Any?> = when(source){
            is Account -> {
                source.asMap() as HashMap<String, Any?>
            }
            is ContactInformation -> source.asMap() as HashMap<String, Any?>
            is Address -> source.asMap() as HashMap<String, Any?>
            is ContactNumber -> source.asMap() as HashMap<String, Any?>
            is EmailAddress -> source.asMap() as HashMap<String, Any?>
            is Website -> source.asMap() as HashMap<String, Any?>
            else -> {
                HashMap()
            }
        }
        val updated: HashMap<String, Any?> = when(edited){
            is Account -> edited.asMap() as HashMap<String, Any?>
            is ContactInformation -> edited.asMap() as HashMap<String, Any?>
            is Address -> edited.asMap() as HashMap<String, Any?>
            is ContactNumber -> edited.asMap() as HashMap<String, Any?>
            is EmailAddress -> edited.asMap() as HashMap<String, Any?>
            is Website -> edited.asMap() as HashMap<String, Any?>
            else -> HashMap()
        }
        Log.i("Source", original.toString())
        Log.i("Edited", updated.toString())
        for (key in original.keys){
            if (original[key] != updated[key]){
                modified[key] = updated[key]
            }
        }
        return modified
    }
    private inline fun <reified T : Any> T.asMap() : Map<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) }
    }
}