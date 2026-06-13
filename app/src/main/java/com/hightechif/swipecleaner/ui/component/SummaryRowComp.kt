package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.ui.theme.SwipeCleanerTheme

@Composable
fun SummaryRowComp(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color = Color.White,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14)
@Composable
private fun SummaryRowCompPreview() {
    SwipeCleanerTheme {
        SummaryRowComp(label = "Photos Kept", value = "42", color = Color(0xFF4CAF50))
    }
}
