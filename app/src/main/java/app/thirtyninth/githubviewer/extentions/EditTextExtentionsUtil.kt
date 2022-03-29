package app.thirtyninth.githubviewer.extentions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun EditText.bindTextTwoWayFlow(
    mutableStateFlow: MutableStateFlow<String>,
    scope: CoroutineScope
) {
    this.addTextChangedListener(object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val str = s.toString()

            mutableStateFlow.value = str
        }
    })

    mutableStateFlow.onEach { text ->
        if (this.text.toString() == text) return@onEach
        this.setText(text)
    }.launchIn(scope)
}