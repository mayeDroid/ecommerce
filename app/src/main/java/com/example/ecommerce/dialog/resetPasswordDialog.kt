package com.example.ecommerce.dialog

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Fragment.setUpBottomSheetDialog( onSendClick: (String) -> Unit){  // we used this extension function because we are going to use this builder again
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)       // R.style.DialogStyle is optional
    val view = layoutInflater.inflate(R.layout.reset_password_layout, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED  // this will enable the keyboard not to cover the send and cancel buttons
    dialog.show()

    val editTextMail = view.findViewById<EditText>(R.id.editTextResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSend)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)

    val  textChange = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val editTextEmail = editTextMail.text.toString()
            buttonSend.isEnabled = editTextEmail.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    editTextMail.addTextChangedListener(textChange)
    buttonSend.addTextChangedListener(textChange)

    buttonSend.isEnabled = false

    buttonSend.setOnClickListener {
        val email = editTextMail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()    // this will hide dialog fragment after pressing the send button
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()    // this will hide the dialog
    }

}

