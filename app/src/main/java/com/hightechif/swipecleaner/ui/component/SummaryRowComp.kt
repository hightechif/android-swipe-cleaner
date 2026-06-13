package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SummaryRowComp(
    label: String,
    value: String,
    color: Color = Color.White
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}
