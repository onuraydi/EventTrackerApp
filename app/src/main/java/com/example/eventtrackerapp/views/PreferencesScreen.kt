package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.example.eventtrackerapp.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    navController: NavController,
    profile: Profile,
    profileViewModel: ProfileViewModel = viewModel(),
    isDark:Boolean,
    themeViewModel: ThemeViewModel
){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                    title = {Text("Preferences")},
                    navigationIcon = {
                        IconButton(
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Go Back")
                              },
                            onClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        )
        { innerPadding->

            val myTags = rememberSaveable{ mutableStateOf(profile.selectedTagList.orEmpty().toList()) }
            val isEdit = rememberSaveable { mutableStateOf(false) }


            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //Bildirimler
                    // TODO SONRA AÇILACAK
//                    CardRow("Notifications") {
//                        Icon(Icons.Default.Notifications,
//                            "Notification",
//                            Modifier.size(32.dp)
//                        )
//                    }

                    //DarkMode
                    CardRow(
                        preference = "Dark Mode",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.dark_mode_icon),
                                contentDescription = "DarkMode",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        isChecked = isDark,
                        onCheckedChange = { themeViewModel.toogleTheme(it) }
                    )

                    Spacer(Modifier.padding(vertical = 12.dp))

                    //Kategorilerim
                    Row {
                        Text("My Tags", modifier = Modifier.weight(5f))
                        Text(
                            text = if(isEdit.value){
                                "Confirm"
                            }else{
                                "Edit"
                            },
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isEdit.value = !isEdit.value
                                    if(!isEdit.value){
                                        myTags.value = profile.selectedTagList.orEmpty().toList()
                                    }
                                }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ){
                        FlowRow(
                            Modifier.padding(5.dp),
                            maxItemsInEachRow = 4
                        ) {
                            myTags.value.forEach {tag ->

                                val isSelected = rememberSaveable{ mutableStateOf(true) }
                                FilterChip(
                                    modifier = Modifier.padding(end = 8.dp),
                                    selected = isSelected.value,
                                    enabled = isEdit.value,
                                    onClick = {
                                        isSelected.value = !isSelected.value

                                        if(isSelected.value){
                                            //kategori eklenecek
                                            profileViewModel.addTag(tag,profile.id)
                                        }else{
                                            //kategori silinecek
                                            profileViewModel.removeTag(tag.id,profile.id)
                                        }
                                    },
                                    label = {Text(tag.name ?: "")},
                                    trailingIcon = {
                                        if(myTags.value.contains(tag)){
                                            Icon(Icons.Default.Clear,"Clear")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }

@Composable
fun CardRow(
    preference: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .heightIn(min = 72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.invoke()

            Text(
                text = preference,
                fontSize = 21.sp,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun ShowMyScreen(){
//    EventTrackerAppTheme {
//        //PreferencesScreen()
//    }
//}