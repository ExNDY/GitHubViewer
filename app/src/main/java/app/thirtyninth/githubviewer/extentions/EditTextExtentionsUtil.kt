package app.thirtyninth.githubviewer.extentions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun EditText.bindTextTwoWayFlow(
    mutableStateFlow: MutableStateFlow<String>,
    scope: CoroutineScope
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val str = p0.toString()

            mutableStateFlow.value = str
        }
    })

    mutableStateFlow.onEach { text ->
        if (this.text.toString() == text) return@onEach
        this.setText(text)
    }.launchIn(scope)
}

fun EditText.bindTextTwoWayLiveData(livedata: MutableLiveData<String>, lifecycle: LifecycleOwner) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val str = p0.toString()

            if (str == livedata.value) return
            livedata.value = str
        }
    }

    this.addTextChangedListener(textWatcher)

    livedata.observe(lifecycle) { text ->
        if (this.text.toString() == text) return@observe
        this.setText(text)
    }
}