package com.example.eventtrackerapp.views

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.simulateHotReload
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton

@ExperimentalLayoutApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController:NavHostController,
    event: Event
){
    Scaffold(Modifier
        .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text="Edit Event", fontSize = 25.sp)},
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,null,
                        modifier = Modifier.padding(start = 8.dp)
                            .clickable { navController.popBackStack() }
                    )
                }
            )
        }) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            val eventName = rememberSaveable { mutableStateOf(event.name ?: "sssss88") }
            val eventDetail = rememberSaveable { mutableStateOf(event.detail ?: "") }
            val selectedDate = rememberSaveable { mutableStateOf<Long?>(null) }
            val showModal = rememberSaveable { mutableStateOf(false)}
            val eventDuration = rememberSaveable { mutableStateOf(event.duration ?: "") }
            val eventLocation = rememberSaveable { mutableStateOf(event.location ?: "") }
            val tagIsSelected = rememberSaveable { mutableStateOf(false) }
            val category = rememberSaveable { mutableStateOf("Teknoloji") }
            val selectedTagList = remember { mutableStateListOf<String>() }
            val isExpanded = rememberSaveable { mutableStateOf(false) }

            val categories = arrayListOf(
                "Yazılım",
                "Yapay Zeka",
                "Back-End",
                "Front-End",
                "Teknoloji",
                "Örnek",
                "Örnek2",
                "Örnek3",
                "Örnek4"
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally){

                Spacer(Modifier.padding(12.dp))

                Icon(
                    painter = painterResource(R.drawable.image_icon),null,
                    modifier = Modifier
                        .size(180.dp, 160.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                        .clickable {

                        }
                )

                Spacer(Modifier.padding(vertical = 5.dp))
                Text("you are updated event hhoto")

                Spacer(Modifier.padding(vertical = 12.dp))
                EventTrackerAppOutlinedTextField(
                    txt = "Event Name",
                    state = eventName,
                    leadingIcon = {
                        Icon(Icons.Default.Edit,null)
                    },
                )

                Spacer(Modifier.padding(vertical = 12.dp))

                EventTrackerAppOutlinedTextField(
                    txt = "Event Detail",
                    state = eventDetail,
                    leadingIcon = {
                        Icon(Icons.Default.Info,null)
                    },
                )

                Spacer(Modifier.padding(vertical = 12.dp))

                //ShowDateModal(Modifier,selectedDate, showModal)

                Spacer(Modifier.padding(vertical = 12.dp))

                EventTrackerAppOutlinedTextField(
                    txt = "Event Duration",
                    state = eventDuration,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.clock_icon),
                            contentDescription = null
                        )
                    }
                )

                Spacer(Modifier.padding(vertical = 12.dp))

                EventTrackerAppOutlinedTextField(
                    txt = "Event Location",
                    state = eventLocation,
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn,null)
                    }
                )

                Spacer(Modifier.padding(vertical = 12.dp))

                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange = {
                        isExpanded.value = it
                    }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        value = category.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text("Event Category")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(isExpanded.value)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.category_icon),
                                contentDescription = null
                            )
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded.value,
                        onDismissRequest = {isExpanded.value = false}
                    ) {
                        categories.forEach{
                            DropdownMenuItem(
                                text = { Text(it)},
                                onClick = {
                                    isExpanded.value = false
                                    category.value = it
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.padding(vertical = 12.dp))

                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)){
                    items(categories){
                        FilterChip(
                            modifier = Modifier.padding(end = 8.dp),
                            selected = tagIsSelected.value,
                            label = {
                                Text(it)
                            },
                            onClick = {
                                tagIsSelected.value = !tagIsSelected.value
                                if(!selectedTagList.contains(it)){
                                    selectedTagList.add(it)
                                }else{
                                    selectedTagList.remove(it)
                                }
                            },
                            trailingIcon = if(tagIsSelected.value) {
                                {
                                    Icon(
                                        Icons.Default.Done, null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    }
                }

                Spacer(Modifier.padding(vertical = 5.dp))

                Box(
                    Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .defaultMinSize(minHeight = 80.dp)
                        .heightIn(max=150.dp)
                        .verticalScroll(rememberScrollState())
                        .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                ) {
                    FlowRow(
                        modifier = Modifier.padding(5.dp),
                        maxItemsInEachRow = 4
                    ){
                        selectedTagList.filterNotNull().forEach{
                            FilterChip(
                                modifier = Modifier.padding(end = 3.dp),
                                selected = tagIsSelected.value,
                                label = { Text(it, fontSize = 12.sp, maxLines = 1)},
                                onClick = {
                                    tagIsSelected.value = !tagIsSelected.value
                                    selectedTagList.remove(it)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.Clear,null)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.padding(vertical = 20.dp))

            Box(
                Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 15.dp)
            ) {
                EventTrackerAppPrimaryButton(text = "Update Event", onClick = {})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected:(Long?) -> Unit,
    onDismiss:()-> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton =  {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun EditEventScreenPrev(){
    EventTrackerAppTheme {
//        EditEventScreen()
    }
}