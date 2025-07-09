package com.example.eventtrackerapp.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun EventTrackerAppPrimaryButton(
    text:String,
    fontSize: TextUnit = 18.sp,
    onClick : () -> Unit)
{
    Button(
        onClick = onClick,
        Modifier.fillMaxWidth(0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = fontSize)
    }
}

@Composable
fun EventTrackerAppOutlinedButton(
    text:String,
    fontSize: TextUnit = 18.sp,
    textDecoration: TextDecoration?=null,
    onClick: ()-> Unit)
{
    OutlinedButton(
        onClick = onClick,
        Modifier.fillMaxWidth(0.7f),
        shape = RoundedCornerShape(12.dp),
    ){
        Text(text= text, fontSize = fontSize, textDecoration = textDecoration)
    }
}