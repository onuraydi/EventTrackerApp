package com.example.eventtrackerapp.utils

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun EventTrackerAppOutlinedTextField(txt:String,
     state: MutableState<String>,
     leadingIcon: @Composable (() -> Unit)? = null,
     trailingIcon: @Composable (() -> Unit)? = null,
     isSingleLine: Boolean = true,
     isReadOnly:Boolean=false,
     isPassword:Boolean = false)
{
    OutlinedTextField(
        modifier = Modifier.widthIn(max = 280.dp),
        value = state.value,
        onValueChange = {
            state.value = it;
        },
        singleLine = isSingleLine,
        readOnly = isReadOnly,
        label = { Text(text = txt) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = if(isPassword)
            PasswordVisualTransformation()
        else VisualTransformation.None
    )

}


@Composable
fun EventTrackerAppAuthTextField(
    txt: String,
    state: MutableState<String>,
    leadingIcon: @Composable (()->Unit)? = null,
    trailingIcon: @Composable (()->Unit)? = null,
    isSingleLine: Boolean = true,
    isReadOnly: Boolean = false,
    isPassword: Boolean = false,
    isError: Boolean = false,
    onValueChange: (String)->Unit
)
{
    OutlinedTextField(
        modifier = Modifier.widthIn(max = 280.dp),
        value = state.value,
        onValueChange = onValueChange,
        singleLine = isSingleLine,
        readOnly = isReadOnly,
        label = { Text(text = txt) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = if(isPassword)
            PasswordVisualTransformation()
        else VisualTransformation.None
    )

}