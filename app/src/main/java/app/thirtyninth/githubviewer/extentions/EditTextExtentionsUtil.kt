package app.thirtyninth.githubviewer.extentions

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class EditTextExtentionsUtil {
    fun TextInputEditText.doAfterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged.invoke(p0.toString())
            }

        })
    }
}