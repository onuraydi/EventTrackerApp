package com.example.eventtrackerapp.views

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.Greeting
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.security.KeyStore.TrustedCertificateEntry

@Composable
fun SignUpScreen()
{
    Box(modifier = Modifier
        .padding(15.dp)
        .fillMaxSize())
    {
        OutlinedButton(modifier = Modifier,
            onClick = {},

            colors = ButtonColors(
                contentColor = Color.Green,
                containerColor = Color.White,
                disabledContentColor = Color.Red,
                disabledContainerColor = Color.Red),
            // Border rengi
        ) {
            Icon(Icons.Filled.KeyboardArrowLeft,"Back");
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            )
        {
            var fullName = remember { mutableStateOf("") }
            var email = remember { mutableStateOf("") }
            var password = remember { mutableStateOf("") }
            var passwordConfirm = remember { mutableStateOf("") }
            var isObscure = remember { mutableStateOf(false) }
            var isObscureConfirm = remember { mutableStateOf(false) }

            Text(text = "Sign Up",
                fontWeight = FontWeight.W500,
                fontSize = 25.sp,
                modifier = Modifier.padding(top = 20.dp)
                )
            Spacer(modifier = Modifier.padding(15.dp))
            EventTrackerAppOutlinedTextField("Kullanıcı Adı", state = fullName,
                leadingIcon = {
                    Icon(Icons.Filled.Person,"full name")
                })

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Email", state = email,
                leadingIcon = {
                    Icon(Icons.Filled.Email,"email")
                });

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Şifre", state = password,
                leadingIcon = {
                    Icon(Icons.Filled.Lock,"password")
                },
                trailingIcon = {
                    val icon = if(isObscure.value){
                        painterResource(R.drawable.visibility_icon)}
                    else{
                        painterResource(R.drawable.visibility_off_icon)}

                    Icon(painter = icon,"visibility",
                        modifier = Modifier.clickable { isObscure.value = !isObscure.value })
                },
                isPassword = !isObscure.value);


            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Şifre tekrarı", state = passwordConfirm,
                leadingIcon = {
                    Icon(Icons.Filled.Lock,"confirm password")
            },
                trailingIcon = {
                        Icon(painter = painterResource(isObscureConfirm.value),"visibility",
                            modifier = Modifier.clickable { isObscureConfirm.value = !isObscureConfirm.value })
                    },
                isPassword = !isObscureConfirm.value);


            Spacer(modifier = Modifier.padding(20.dp));

            Button(onClick = {},
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(12.dp)){
                Text("Sign Up", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Or")

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedButton(onClick = {},
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(12.dp) ) {
                Text(text="Login",
                    textDecoration = TextDecoration.Underline, fontSize = 18.sp)
            }

        }
    }

}

@Composable
private fun painterResource(isObscure:Boolean):Painter
{
    val icon = if(isObscure){
        painterResource(R.drawable.visibility_icon)}
    else{
        painterResource(R.drawable.visibility_off_icon)}

    return icon
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventTrackerAppTheme {
        SignUpScreen();
    }
}