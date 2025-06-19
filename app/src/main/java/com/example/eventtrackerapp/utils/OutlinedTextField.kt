package com.example.eventtrackerapp.utils

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun EventTrackerAppOutlinedTextField(txt:String,state: MutableState<String>)
{
    OutlinedTextField(value = "${txt}",
        onValueChange = {
            state.value = it;
        },
        singleLine = true,
        placeholder = {"${txt}"},
        label = { Text(text = "${txt}") }


    )
}