package com.corneflex.tabrow.ui.components.tabrow.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentOptions
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentStyle
import com.corneflex.tabrow.ui.components.tabrow.model.TabItem

@Composable
internal fun TabContent(
    item: TabItem,
    style: TabContentStyle,
    contentColor: Color,
    textStyle: TextStyle,
    options: TabContentOptions,
) {
    when (style) {
        TabContentStyle.Text -> item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
            )
        }
        TabContentStyle.Icon -> item.icon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.contentDescription,
                tint = contentColor,
                modifier = Modifier
                    .padding(horizontal = options.iconOnlyHorizontalPadding)
                    .size(20.dp),
            )
        }
        TabContentStyle.Image -> item.image?.let {
            Image(
                painter = it,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(24.dp),
            )
        }
        TabContentStyle.IconText -> IconTextContent(item, contentColor, textStyle)
        TabContentStyle.ImageText -> ImageTextContent(item, contentColor, textStyle)
    }
}

@Composable
private fun IconTextContent(
    item: TabItem,
    contentColor: Color,
    textStyle: TextStyle,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.icon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.contentDescription,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
        }
        item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
            )
        }
    }
}

@Composable
private fun ImageTextContent(
    item: TabItem,
    contentColor: Color,
    textStyle: TextStyle,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.image?.let {
            Image(
                painter = it,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(24.dp),
            )
        }
        item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
            )
        }
    }
}

/** Falls back to the best available style when the preferred one lacks the required data. */
internal fun TabItem.resolveContentStyle(preferred: TabContentStyle): TabContentStyle {
    val hasText = text != null
    val hasIcon = icon != null
    val hasImage = image != null
    return when {
        preferred == TabContentStyle.Text && hasText -> preferred
        preferred == TabContentStyle.Icon && hasIcon -> preferred
        preferred == TabContentStyle.Image && hasImage -> preferred
        preferred == TabContentStyle.IconText && hasIcon && hasText -> preferred
        preferred == TabContentStyle.ImageText && hasImage && hasText -> preferred
        hasText -> TabContentStyle.Text
        hasIcon -> TabContentStyle.Icon
        hasImage -> TabContentStyle.Image
        else -> TabContentStyle.Text
    }
}
