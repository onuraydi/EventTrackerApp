package com.example.eventtrackerapp.views

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyAccountScreen(navController:NavController) {
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Hesap Ayarları",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                })
        }
    ) { innnerPadding ->
        Box(
            modifier = Modifier
                .padding(innnerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var fullNameState = rememberSaveable { mutableStateOf("") }
            var userNameState = rememberSaveable { mutableStateOf("") }
            var emailState = rememberSaveable { mutableStateOf("deneme@gmail.com") }
            var passwordState = rememberSaveable { mutableStateOf("deneme1234") }
            var gender = rememberSaveable { mutableStateOf("") }
            var isExpanded = rememberSaveable { mutableStateOf(false) }
            var profilePhotoState =
                rememberSaveable { mutableStateOf(R.drawable.profile_photo_add_icon) }
            // Buraya kullanıcının yüklediği profil gelecek

            Column(
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            )
            {
                Spacer(modifier = Modifier.padding(vertical = 15.dp))

                Box(
                    Modifier
                        .size(80.dp, 80.dp)
                        .border(border = BorderStroke(2.dp, Color.Black), shape = CircleShape)
                        .clickable {
                        }
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                            .align(Alignment.Center)
                            .padding(start = 5.dp),
                        painter = painterResource(profilePhotoState.value),
                        contentDescription = "PhotoAdd",
                    )
                }

                Spacer(Modifier.padding(vertical = 5.dp))

                Text(
                    text = "Update Profile Photo",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )

                Spacer(Modifier.padding(vertical = 7.dp))


                EventTrackerAppOutlinedTextField(txt = "Full Name", fullNameState)

                Spacer(modifier = Modifier.padding(vertical = 12.dp))

                EventTrackerAppOutlinedTextField(txt = "Username", userNameState)

                Spacer(Modifier.padding(vertical = 12.dp))


                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange = { isExpanded.value = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(),
                        value = gender.value,
                        onValueChange = {},
                        placeholder = { Text("Gender") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                        },
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded.value,
                        onDismissRequest = { isExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Male") },
                            onClick = {
                                gender.value = "Male"
                                isExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Female") },
                            onClick = {
                                gender.value = "Female"
                                isExpanded.value = false
                            }
                        )
                    }
                }

                Spacer(Modifier.padding(vertical = 12.dp))


                EventTrackerAppOutlinedTextField(txt = "email", emailState, isReadOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Build,null)
                        Modifier.clickable {  }
                    })

                Spacer(Modifier.padding(vertical = 12.dp))


                EventTrackerAppOutlinedTextField(txt = "Şifre", passwordState, isPassword = true, isReadOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Build,null)
                        Modifier.clickable {  }
                    })

            }
            Spacer(Modifier.padding(vertical = 20.dp))
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)){
                EventTrackerAppPrimaryButton("Complete") { }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun MyAccountScreenPrev() {
    EventTrackerAppTheme {
        //MyAccountScreen()
    }
}