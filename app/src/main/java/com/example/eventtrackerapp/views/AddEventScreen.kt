package com.example.eventtrackerapp.views

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedButton
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen() {
    EventTrackerAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Add Event", fontSize = 25.sp) },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            "GoBack",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                //Adı, Tarihi,
                // Detayı,Etkinlik Süresi,Resim,
                // Konum,Websitesi,Kategori, Tag(AltKategori)
                val eventName = rememberSaveable { mutableStateOf("") }
                val eventDetail = rememberSaveable { mutableStateOf("") }
                val selectedDate = rememberSaveable { mutableStateOf<Long?>(null) }
                val showModal = rememberSaveable { mutableStateOf(false) }
                val eventDuration = rememberSaveable { mutableStateOf("") }
                val eventLocation = rememberSaveable { mutableStateOf("") }
                /*TODO(Şimdilik bu kısım hem etiket ve kategori için kullanılacak. Sonrasında modellere göre)*/
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
                val category = rememberSaveable{ mutableStateOf("") }
                val isExpanded = rememberSaveable{ mutableStateOf(false)}

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.padding(vertical = 12.dp))

                    Icon(
                        painter = painterResource(R.drawable.image_icon),
                        contentDescription = "Add Image",
                        modifier = Modifier
                            .size(180.dp, 160.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                            .clickable {
                                /*TODO(Buraya fotoğraf yükleme gelecek)*/
                            }
                    )
                    Spacer(Modifier.padding(vertical = 5.dp))
                    Text("Please add event photo")

                    Spacer(Modifier.padding(vertical = 12.dp))
                    EventTrackerAppOutlinedTextField(
                        txt = "Event Name",
                        state = eventName,
                        leadingIcon = {
                            Icon(Icons.Default.Edit,"Add Event")
                        },
                    )

                    Spacer(Modifier.padding(vertical = 12.dp))
                    /*TODO(SINGLE LINE)*/
                    EventTrackerAppOutlinedTextField(
                        txt = "Event Detail",
                        state = eventDetail,
                        leadingIcon = {
                            Icon(Icons.Default.Info,"Event Detail")
                        },

                    )

                    Spacer(Modifier.padding(vertical = 12.dp))

                    ShowDateModal(Modifier,selectedDate, showModal)

                    Spacer(Modifier.padding(vertical = 12.dp))

                    EventTrackerAppOutlinedTextField(
                        txt = "Event Duration",
                        state = eventDuration,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.clock_icon),
                                contentDescription = "Clock"
                            )
                        }
                    )

                    Spacer(Modifier.padding(vertical = 12.dp))

                    EventTrackerAppOutlinedTextField(
                        txt = "Event Location",
                        state = eventLocation,
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn,
                                "Location"
                            )
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
                                    contentDescription = "Category"
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = { isExpanded.value = false}//bu sayede menü kapanacak
                        ) {
                            categories.forEach{
                                DropdownMenuItem(
                                    text = {Text(it)},
                                    onClick = {
                                        isExpanded.value = false
                                        category.value = it
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
                    EventTrackerAppPrimaryButton(
                        text = "Add Event",
                    ){
                        //onClick
                    }
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?)->Unit,
    onDismiss: ()-> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ){
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ){
        DatePicker(state = datePickerState)
    }
}

@Composable
fun ShowDateModal(
    modifier: Modifier = Modifier,
    dateState : MutableState<Long?>,
    modalState : MutableState<Boolean>
){

    OutlinedTextField(
        value = dateState.value?.let{ convertMillisToDate(it) } ?: "",
        onValueChange = { },
        readOnly = true,
        label = {Text("DOB")},
        placeholder = {
            Text("MM/DD/YYYY")
        },
        leadingIcon = {
            Icon(Icons.Default.DateRange,"Select Date")
        },
        modifier = modifier
            .fillMaxWidth(0.71f)
            .pointerInput(dateState.value) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(
                        pass = PointerEventPass.Initial
                    )
                    if (upEvent != null) {
                        modalState.value = true
                    }
                }
            }
    )

    if(modalState.value){
        DatePickerModal(
            onDateSelected = {
                dateState.value = it
            },
            onDismiss = {
                modalState.value = false
            }
        )
    }

}

fun convertMillisToDate(millis:Long) :String{
    val formatter = SimpleDateFormat("MM//dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
fun PreviewPage(){
    EventTrackerAppTheme {
        AddEventScreen()
    }
}