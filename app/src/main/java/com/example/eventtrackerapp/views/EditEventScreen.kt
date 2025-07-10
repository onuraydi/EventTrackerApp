package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.model.Tag
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import java.io.File


@SuppressLint("NewApi", "AutoboxingStateCreation")
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavHostController,
    eventWithTag: EventWithTags,
    eventViewModel: EventViewModel,
    categoryViewModel: CategoryViewModel,
    tagViewModel: TagViewModel,
    ownerId: String
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        categoryViewModel.getAllCategoryWithTags()
    }

    val categoryId = rememberSaveable { mutableStateOf(eventWithTag.event.categoryId) }
    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsState()

    val selectedCategoryName = rememberSaveable { mutableStateOf("") }
    val chosenTags = remember { mutableStateListOf<Tag>() }

    LaunchedEffect(categoryWithTags) {
        if (categoryWithTags.isNotEmpty())
        {
            val selectedCategory = categoryWithTags.firstOrNull { it.category.id == eventWithTag.event.categoryId }
            if (selectedCategory != null)
            {
                selectedCategoryName.value = selectedCategory.category.name ?: ""
                categoryId.value = selectedCategory.category.id
                chosenTags.clear()
                chosenTags.addAll(eventWithTag.tags)
            }
        }
    }

    val currentCategoryTags = remember(categoryId.value, categoryWithTags)
    {
        categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.tags ?: emptyList()
    }

    LaunchedEffect(categoryId.value, categoryWithTags) {
        val selected = categoryWithTags.firstOrNull { it.category.id == categoryId.value }
        selectedCategoryName.value = selected?.category?.name ?: ""
        if (categoryId.value != eventWithTag.event.categoryId) {
            chosenTags.clear()
        } else {
            chosenTags.clear()
            chosenTags.addAll(eventWithTag.tags)
        }
        tagViewModel.resetTag()
    }

    val isExpanded = rememberSaveable { mutableStateOf(false) }

    if (categoryWithTags.isEmpty())
    {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar =
        {
            EventTrackerTopAppBar(
                title = "Edit Event",
                modifier = Modifier,
                showBackButton = true,
                onBackClick =
                {
                    navController.popBackStack()
                },
            )
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            val eventName = rememberSaveable { mutableStateOf(eventWithTag.event.name ?: "") }
            val nameError = rememberSaveable { mutableStateOf(false) }

            val eventDetail = rememberSaveable { mutableStateOf(eventWithTag.event.detail ?: "") }
            val detailError = rememberSaveable { mutableStateOf(false) }

            val selectedDate = rememberSaveable { mutableStateOf(eventWithTag.event.date) }
            val dateError = rememberSaveable { mutableStateOf(false) }

            val showModal = rememberSaveable { mutableStateOf(false) }

            val eventDuration = rememberSaveable { mutableStateOf(eventWithTag.event.duration ?: "") }
            val durationError = rememberSaveable { mutableStateOf(false) }

            val eventLocation = rememberSaveable { mutableStateOf(eventWithTag.event.location ?: "") }
            val locationError = rememberSaveable { mutableStateOf(false) }

            val categoryError = rememberSaveable { mutableStateOf(false) }

            val eventImage = rememberSaveable{mutableStateOf(eventWithTag.event.image)}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    Modifier
                        .padding(vertical = 12.dp)
                )

                if(eventImage.value !=null)
                {
                    val imageFile = eventImage.value?.let { File(it) }
                    if(imageFile!=null && imageFile.exists())
                    {
                        AsyncImage(
                            model = imageFile,
                            contentDescription = "Add Image",
                            modifier = Modifier
                                .size(180.dp, 160.dp)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    /*TODO(Buraya fotoğraf yükleme gelecek)*/
                                }
                        )
                    }else{
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "Add Image",
                            modifier = Modifier
                                .size(180.dp, 160.dp)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    /*TODO(Buraya fotoğraf yükleme gelecek)*/
                                }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )

                Text(
                    text = "you are updated event photo"
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Name",
                    state = eventName,
                    onValueChange =
                    {
                        eventName.value = it
                        nameError.value = eventName.value.isBlank()
                    },
                    isError = nameError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    modifier = Modifier
                        .heightIn(min = 120.dp, max = 200.dp),
                    txt = "Event Detail",
                    state = eventDetail,
                    onValueChange =
                    {
                        eventDetail.value = it
                        detailError.value = eventDetail.value.isBlank()
                    },
                    isError = detailError.value,
                    isSingleLine = false
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                ShowDateModal(
                    modifier = Modifier,
                    dateState = selectedDate,
                    modalState = showModal,
                    dateErrorState = dateError,
                    context = context
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Duration",
                    state = eventDuration,
                    onValueChange =
                    {
                        eventDuration.value = it
                        durationError.value = eventDuration.value.isBlank()
                    },
                    isError = durationError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Location",
                    state = eventLocation,
                    onValueChange =
                    {
                        eventLocation.value = it
                        locationError.value = eventLocation.value.isBlank()
                    },
                    isError = locationError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange =
                    {
                        isExpanded.value = it
                    }
                ) {

                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        value = selectedCategoryName.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder =
                        {
                            Text("Event Category")
                        },
                        trailingIcon =
                        {
                            ExposedDropdownMenuDefaults.TrailingIcon(isExpanded.value)
                        },
                        leadingIcon =
                        {
                            Icon(
                                painter = painterResource(R.drawable.category_icon),
                                contentDescription = "Category"
                            )
                        },
                        isError = categoryError.value,
                        supportingText =
                        {
                            if (categoryError.value)
                            {
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
                        categoryWithTags.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.category.name}") },
                                onClick =
                                {
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

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                Text(
                    text = "Update event tag"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(currentCategoryTags)
                    { tag ->
                        val isSelected = chosenTags.any { it.id == tag.id }

                        FilterChip(
                            modifier = Modifier.padding(end = 8.dp),
                            selected = isSelected,
                            label = { Text(tag.name ?: "") },
                            onClick =
                            {
                                if (chosenTags.any { it.id == tag.id })
                                {
                                    chosenTags.removeAll { it.id == tag.id }
                                } else
                                {
                                    chosenTags.add(tag)
                                }
                            },
                            trailingIcon = if (isSelected)
                            {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Done",
                                        modifier = Modifier
                                            .size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )

                Box(
                    Modifier
                        .fillMaxWidth(fraction = .9f)
                        .wrapContentHeight()
                        .defaultMinSize(minHeight = 80.dp)
                        .heightIn(max = 150.dp)
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    FlowRow(
                        modifier = Modifier.padding(5.dp),
                        maxItemsInEachRow = 4
                    ) {
                        chosenTags.forEach { tag ->
                            FilterChip(
                                modifier = Modifier.padding(end = 3.dp),
                                selected = true,
                                label = { Text(tag.name ?: "", fontSize = 12.sp, maxLines = 1) },
                                onClick =
                                {
                                    chosenTags.removeAll { it.id == tag.id }
                                },
                                trailingIcon =
                                {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            )
                        }
                    }
                }

            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 20.dp)
            )

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 15.dp)
            ) {
                EventTrackerAppPrimaryButton(
                    text = "Update Event",
                    onClick = {
                        if (
                            eventName.value.isBlank() ||
                            eventDetail.value.isBlank() ||
                            eventDuration.value.isBlank() ||
                            eventLocation.value.isBlank() ||
                            selectedCategoryName.value.isBlank() ||
                            selectedDate.value == null ||
                            chosenTags.isEmpty()
                        )
                        {
                            nameError.value = eventName.value.isBlank()
                            detailError.value = eventDetail.value.isBlank()
                            durationError.value = eventDuration.value.isBlank()
                            locationError.value = eventLocation.value.isBlank()
                            dateError.value = selectedDate.value == null
                            categoryError.value = selectedCategoryName.value.isBlank()
                            return@EventTrackerAppPrimaryButton
                        } else
                        {
                            val event = Event(
                                id = eventWithTag.event.id,
                                ownerId = ownerId,
                                name = eventName.value,
                                detail = eventDetail.value,
                                image = eventImage.value,
                                date = selectedDate.value,
                                duration = eventDuration.value,
                                location = eventLocation.value,
                                likeCount = 0,
                                categoryId = categoryId.value,
                            )
                            eventViewModel.updateEventWithTags(event = event, tags = chosenTags)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}
