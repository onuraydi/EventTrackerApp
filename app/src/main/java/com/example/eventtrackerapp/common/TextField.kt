package com.example.eventtrackerapp.common

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun EventTrackerAppOutlinedTextField(
    modifier: Modifier = Modifier,
    txt: String,
    state: MutableState<String>,
    leadingIcon: @Composable (()->Unit)? = null,
    trailingIcon: @Composable (()->Unit)? = null,
    isSingleLine: Boolean = true,
    isReadOnly: Boolean = false,
    isPassword: Boolean = false,
    isError: Boolean = false,
    onValueChange: (String)->Unit = {}
)
{
    OutlinedTextField(
        modifier = modifier.widthIn(max = 280.dp),
        value = state.value,
        onValueChange = onValueChange,
        singleLine = isSingleLine,
        readOnly = isReadOnly,
        label = { Text(text = txt) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText =
        {
            if(isError) {
                Text(
                    text = "Bu alan boş bırakılamaz!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        visualTransformation = if(isPassword)
            PasswordVisualTransformation()
        else VisualTransformation.None
    )

}
