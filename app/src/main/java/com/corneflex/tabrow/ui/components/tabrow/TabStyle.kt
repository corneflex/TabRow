package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TabStyle(
    val selectedContentColor: Color,
    val unselectedContentColor: Color,
    val selectedContainerColor: Color = Color.Transparent,
    val unselectedContainerColor: Color = Color.Transparent,
    val selectedBorder: BorderStroke? = null,
    val unselectedBorder: BorderStroke? = null,
    val shape: Shape = RoundedCornerShape(999.dp),
    val minHeight: Dp = 44.dp,
    val horizontalPadding: Dp = 16.dp,
    val itemSpacing: Dp = 8.dp,
) {
    companion object {
        fun default(
            selectedContentColor: Color,
            unselectedContentColor: Color,
        ) = TabStyle(
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
        )

        fun filled(
            selectedContentColor: Color,
            unselectedContentColor: Color,
            selectedContainerColor: Color,
        ) = TabStyle(
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
            selectedContainerColor = selectedContainerColor,
        )

        fun outlined(
            selectedContentColor: Color,
            unselectedContentColor: Color,
            selectedBorderColor: Color,
            unselectedBorderColor: Color,
        ) = TabStyle(
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
            selectedBorder = BorderStroke(1.dp, selectedBorderColor),
            unselectedBorder = BorderStroke(1.dp, unselectedBorderColor),
        )
    }
}
