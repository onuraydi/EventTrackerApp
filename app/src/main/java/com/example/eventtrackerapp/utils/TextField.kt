package com.example.eventtrackerapp.utils

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun EventTrackerAppOutlinedTextField(txt:String,
     state: MutableState<String>,
     leadingIcon: @Composable (() -> Unit)? = null,
     trailingIcon: @Composable (() -> Unit)? = null,
     isPassword:Boolean = false)
{
    OutlinedTextField(value = state.value,
        onValueChange = {
            state.value = it;
        },
        singleLine = true,
        label = { Text(text = txt) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = if(isPassword)
            PasswordVisualTransformation()
        else VisualTransformation.None
    )

}