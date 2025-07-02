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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.model.Tag
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppAuthTextField
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEventScreen(
    navController: NavController,
    tagViewModel: TagViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    eventViewModel: EventViewModel
) {
    LaunchedEffect(Unit) {
        tagViewModel.resetTag()
    }
    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsState()
    val selectedTag by tagViewModel.selectedTag.collectAsStateWithLifecycle()
    val chosenTags by tagViewModel.chosenTags.collectAsStateWithLifecycle()

    val selectedCategoryName = remember { mutableStateOf("") }

    val events by eventViewModel.allEventsWithTags.collectAsState(initial = emptyList())

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
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { navController.popBackStack() }
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
                val nameError = rememberSaveable{mutableStateOf(false)}

                val eventDetail = rememberSaveable { mutableStateOf("") }
                val detailError = rememberSaveable{mutableStateOf(false)}

                val selectedDate = rememberSaveable { mutableStateOf<Long?>(null) }
                val dateError = rememberSaveable{mutableStateOf(false)}

                val showModal = rememberSaveable { mutableStateOf(false) }

                val eventDuration = rememberSaveable { mutableStateOf("") }
                val durationError = rememberSaveable{mutableStateOf(false)}

                val eventLocation = rememberSaveable { mutableStateOf("") }
                val locationError = rememberSaveable{mutableStateOf(false)}

                val categoryError = rememberSaveable{mutableStateOf(false)}

                val categoryId = rememberSaveable{ mutableStateOf(0) }
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

                    //Profil Fotoğrafı
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

                    //Event Name
                    Spacer(Modifier.padding(vertical = 12.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Name",
                        state = eventName,
                        onValueChange = {
                            eventName.value = it
                            nameError.value = eventName.value.isBlank()
                        },
                        isError = nameError.value,
                        supportingText = {
                            if(nameError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Event Detail
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Detail",
                        state = eventDetail,
                        onValueChange = {
                            eventDetail.value = it
                            detailError.value = eventDetail.value.isBlank()
                        },
                        isError = detailError.value,
                        supportingText = {
                            if(categoryError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Event Date
                    Spacer(Modifier.padding(vertical = 8.dp))
                    ShowDateModal(Modifier, selectedDate, showModal, dateError)

                    //Event Duration
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Duration",
                        state = eventDuration,
                        onValueChange = {
                            eventDuration.value = it
                            durationError.value = eventDuration.value.isBlank()
                        },
                        isError = durationError.value,
                        supportingText = {
                            if(durationError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Event Location
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Location",
                        state = eventLocation,
                        onValueChange = {
                            eventLocation.value = it
                            locationError.value = eventLocation.value.isBlank()
                        },
                        isError = locationError.value,
                        supportingText = {
                            if(locationError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Select Category
                    Spacer(Modifier.padding(vertical = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = isExpanded.value,
                        onExpandedChange = {
                            isExpanded.value = it
                        }
                    ) {

                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            value = selectedCategoryName.value,
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
                            },
                            isError = categoryError.value,
                            supportingText = {
                                if(categoryError.value){
                                    Text("Bu alanı boş bırakamazsınız")
                                }
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = {
                                isExpanded.value = false
                            }
                        )
                        {
                            categoryWithTags.forEach{
                                DropdownMenuItem(
                                    text = {Text("${it.category.name}")},
                                    onClick = {
                                        selectedCategoryName.value = it.category.name ?: ""
                                        categoryError.value = selectedCategoryName.value.isBlank()
                                        isExpanded.value = false
                                        categoryId.value = it.category.id
                                        tagViewModel.updateSelectedCategoryTags(it)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.padding(vertical = 12.dp))
                    /*TODO(BURADA KULLANILAN ROW VE BOX REFACTOR EDİLMELİ)*/
                    Text("Select event tag")
                    LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)){
                        items(selectedTag){tag->
                            val isSelected = chosenTags.any{it.id==tag.id}

                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                selected = isSelected,
                                label = {
                                    Text(tag.name?:"")
                                },
                                onClick = {
                                    tagViewModel.toggleTag(tag)
                                },
                                trailingIcon = if (isSelected){
                                    {
                                        Icon(
                                            Icons.Default.Done,
                                            "Done",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                }else{
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
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ){
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            maxItemsInEachRow = 4
                        ) {
                            chosenTags.forEach {tag->
                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    selected = true,
                                    label = {Text(tag.name?:"", fontSize = 12.sp, maxLines = 1)},
                                    onClick = {
                                        tagViewModel.removeChosenTag(tag)
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear,"Clear")
                                    }
                                )
                            }
                        }
                    }

                }

                Spacer(Modifier.padding(vertical = 20.dp))

                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp)
                ) {
                    EventTrackerAppPrimaryButton(
                        text = "Add Event",
                        onClick = {
                            if(
                                eventName.value.isBlank() ||
                                eventDetail.value.isBlank() ||
                                eventDuration.value.isBlank() ||
                                eventLocation.value.isBlank() ||
                                selectedCategoryName.value.isBlank() ||
                                selectedDate.value == null ||
                                chosenTags.isEmpty()
                            ){
                                nameError.value = eventName.value.isBlank()
                                detailError.value = eventDetail.value.isBlank()
                                durationError.value = eventDuration.value.isBlank()
                                locationError.value = eventLocation.value.isBlank()
                                dateError.value = selectedDate.value == null
                                categoryError.value = selectedCategoryName.value.isBlank()
                                return@EventTrackerAppPrimaryButton
                            }else{
                                val event = Event(
                                    name = eventName.value,
                                    detail = eventDetail.value,
                                    image = R.drawable.ic_launcher_background,
                                    date = selectedDate.value,
                                    duration = eventDuration.value,
                                    location = eventLocation.value,
                                    likeCount = 0,
                                    categoryId = categoryId.value,

                                    //participants = arrayListOf(),
//                                category = Category(),
//                                tagList = arrayListOf()
                            )
                            eventViewModel.insertEventWithTags(event = event, tags = chosenTags)
                            println(eventViewModel.allEventsWithTags)
                            navController.popBackStack()
                        }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
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
    modalState : MutableState<Boolean>,
    dateErrorState: MutableState<Boolean>
){


    OutlinedTextField(
        value = dateState.value?.let{ convertMillisToDate(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = {Text("Event Date")},
        placeholder = {
            Text("MM/DD/YYYY")
        },
        leadingIcon = {
            Icon(Icons.Default.DateRange,"Select Date")
        },
        isError = dateErrorState.value,
        supportingText = {
            if(dateErrorState.value){
                Text("Bu alan boş bırakılamaz")
            }
        },
        modifier = modifier
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
                dateErrorState.value = it == null
            },
            onDismiss = {
                modalState.value = false
            }
        )
    }

}

fun convertMillisToDate(millis:Long) :String{
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
fun PreviewPage(){
    EventTrackerAppTheme {
        //AddEventScreen()
    }
}