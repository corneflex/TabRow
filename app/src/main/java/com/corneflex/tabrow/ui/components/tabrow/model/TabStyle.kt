package com.corneflex.tabrow.ui.components.tabrow.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TabStyle(
    val shape: Shape = RoundedCornerShape(999.dp),
    val selectedTextStyle: TextStyle? = null,
    val unselectedTextStyle: TextStyle? = null,
    val selectedBorder: BorderStroke? = null,
    val unselectedBorder: BorderStroke? = null,
    val minHeight: Dp = 44.dp,
    val horizontalPadding: Dp = 16.dp,
    val itemSpacing: Dp = 8.dp,
)
