package com.example.eventtrackerapp.common

import android.util.Size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import java.io.File

@Composable
fun SelectableImageBox(
    boxWidth:Dp,
    boxHeight:Dp,
    imagePath:String?,
    modifier: Modifier = Modifier,
    placeHolder: Painter = painterResource(R.drawable.ic_launcher_background),
    shape: Shape,
    borderStroke: BorderStroke = BorderStroke(2.dp,Color.Black),
    onClick:()->Unit = {}
){
    Box(
        modifier
            .size(boxWidth,boxHeight)
            .border(border = borderStroke, shape = shape)
            .clickable {
                onClick()
            }
    ) {
        if(imagePath!=""){
            val imageFile = imagePath?.let { File(it) }
            if (imageFile!=null && imageFile.exists()) {
                AsyncImage(
                    model = imageFile,
                    contentDescription = "Selected Photo",
                    modifier
                        .fillMaxSize(1f)
                        .align(Alignment.Center)
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            } else {
                //eğer dosya boş geldiyse
                Image(
                    painter = placeHolder,
                    contentDescription = "Selected Photo",
                    modifier
                        .fillMaxSize(1f)
                        .align(Alignment.Center)
                        .clip(shape),
                    contentScale = ContentScale.Crop
                )
            }
        }else{
            //eğer fotoğraf yolu boş geldiyse
            Image(
                painter = placeHolder,
                contentDescription = "Selected Photo",
                modifier
                    .fillMaxSize(1f)
                    .align(Alignment.Center)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }

    }
}