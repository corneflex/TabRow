package com.corneflex.tabrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corneflex.tabrow.ui.components.tabrow.CustomScrollableTabRow
import com.corneflex.tabrow.ui.components.tabrow.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.IndicatorPlacement
import com.corneflex.tabrow.ui.components.tabrow.TabContentConfig
import com.corneflex.tabrow.ui.components.tabrow.TabContentStyle
import com.corneflex.tabrow.ui.components.tabrow.TabIndicatorConfig
import com.corneflex.tabrow.ui.components.tabrow.TabIndicatorStyle
import com.corneflex.tabrow.ui.components.tabrow.TabItem
import com.corneflex.tabrow.ui.components.tabrow.TabRowMotion
import com.corneflex.tabrow.ui.components.tabrow.TabStyle
import com.corneflex.tabrow.ui.theme.TabRowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabRowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TabRowDemo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TabRowDemo(modifier: Modifier = Modifier) {
    val tabs = listOf(
        TabItem(text = "Home", icon = DemoIcons.Home),
        TabItem(text = "Search", icon = DemoIcons.Search),
        TabItem(text = "Library", icon = DemoIcons.Grid),
        TabItem(text = "Favorites", icon = DemoIcons.Heart),
        TabItem(text = "Profile", icon = DemoIcons.User),
        TabItem(text = "Settings", icon = DemoIcons.Settings),
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var theme by remember { mutableStateOf(DemoTheme.Brand) }
    var contentMode by remember { mutableStateOf(DemoContentMode.IconText) }
    var indicatorKind by remember { mutableStateOf(DemoIndicatorKind.Pill) }
    var indicatorMotion: IndicatorMotion by remember { mutableStateOf(IndicatorMotion.Slide) }
    var rowMotion: TabRowMotion by remember { mutableStateOf(TabRowMotion.Smooth) }
    var tabLook by remember { mutableStateOf(DemoTabLook.Transparent) }
    var tabGap by remember { mutableStateOf(DemoTabGap.Tight) }
    var placement by remember { mutableStateOf(IndicatorPlacement.BehindContent) }
    var indicatorPadding by remember { mutableStateOf(DemoIndicatorPadding.Medium) }

    val palette = theme.palette()
    val indicator = remember(indicatorKind, indicatorMotion, indicatorPadding, placement, palette) {
        TabIndicatorConfig(
            style = indicatorKind.style(
                color = palette.accent,
                borderColor = palette.border,
                placement = placement,
                horizontalPadding = indicatorPadding.horizontal,
            ),
            motion = indicatorMotion,
        )
    }
    val tabStyle = remember(tabLook, tabGap, indicatorKind, placement, palette) {
        val selectedContentColor = if (
            placement == IndicatorPlacement.BehindContent &&
            indicatorKind != DemoIndicatorKind.Border
        ) {
            palette.onAccent
        } else {
            palette.accent
        }
        tabLook.style(
            palette = palette,
            selectedContentColor = selectedContentColor,
            itemSpacing = tabGap.spacing,
            tabHorizontalPadding = tabGap.tabHorizontalPadding,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Tab row playground",
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = palette.content,
        )

        SettingsSection(title = "Theme", palette = palette) {
            ChoiceRow(
                items = DemoTheme.entries,
                selected = theme,
                label = { it.label },
                palette = palette,
                onSelected = { theme = it },
            )
        }

        SettingsSection(title = "Mode", palette = palette) {
            ChoiceRow(
                items = DemoContentMode.entries,
                selected = contentMode,
                label = { it.label },
                palette = palette,
                onSelected = { contentMode = it },
            )
        }

        SettingsSection(title = "Indicator", palette = palette) {
            ChoiceRow(
                items = DemoIndicatorKind.entries,
                selected = indicatorKind,
                label = { it.label },
                palette = palette,
                onSelected = { indicatorKind = it },
            )
        }

        SettingsSection(title = "Indicator motion", palette = palette) {
            ChoiceRow(
                items = listOf(
                    IndicatorMotion.Slide,
                    IndicatorMotion.Snake,
                    IndicatorMotion.Bounce,
                    IndicatorMotion.Fade,
                    IndicatorMotion.None,
                ),
                selected = indicatorMotion,
                label = { it.label },
                palette = palette,
                onSelected = { indicatorMotion = it },
            )
        }

        SettingsSection(title = "Indicator padding", palette = palette) {
            ChoiceRow(
                items = DemoIndicatorPadding.entries,
                selected = indicatorPadding,
                label = { it.label },
                palette = palette,
                onSelected = { indicatorPadding = it },
            )
        }

        SettingsSection(title = "Row motion", palette = palette) {
            ChoiceRow(
                items = listOf(
                    TabRowMotion.Smooth,
                    TabRowMotion.Snappy,
                    TabRowMotion.Playful,
                    TabRowMotion.None,
                ),
                selected = rowMotion,
                label = { it.label },
                palette = palette,
                onSelected = { rowMotion = it },
            )
        }

        SettingsSection(title = "Tab style", palette = palette) {
            ChoiceRow(
                items = DemoTabLook.entries,
                selected = tabLook,
                label = { it.label },
                palette = palette,
                onSelected = { tabLook = it },
            )
        }

        SettingsSection(title = "Tab gap", palette = palette) {
            ChoiceRow(
                items = DemoTabGap.entries,
                selected = tabGap,
                label = { it.label },
                palette = palette,
                onSelected = { tabGap = it },
            )
        }

        SettingsSection(title = "Placement", palette = palette) {
            ChoiceRow(
                items = IndicatorPlacement.entries,
                selected = placement,
                label = { it.label },
                palette = palette,
                onSelected = { placement = it },
            )
        }

        Text(
            text = "Preview",
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.titleMedium,
            color = palette.content,
        )

        CustomScrollableTabRow(
            tabs = tabs,
            pagerState = pagerState,
            content = contentMode.config,
            indicator = indicator,
            motion = rowMotion,
            tabStyle = tabStyle,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(240.dp),
        ) { page ->
            PagerPage(
                title = tabs[page].text.orEmpty(),
                color = palette.pageColors[page],
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    palette: DemoPalette,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = palette.muted,
        )
        content()
    }
}

@Composable
private fun <T> ChoiceRow(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    palette: DemoPalette,
    onSelected: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            ChoiceChip(
                text = label(item),
                selected = item == selected,
                palette = palette,
                onClick = { onSelected(item) },
            )
        }
    }
}

@Composable
private fun ChoiceChip(
    text: String,
    selected: Boolean,
    palette: DemoPalette,
    onClick: () -> Unit,
) {
    val background = if (selected) palette.accent else palette.surface
    val content = if (selected) palette.onAccent else palette.content
    val border = if (selected) palette.accent else palette.border

    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(BorderStroke(1.dp, border), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = content,
        style = MaterialTheme.typography.labelMedium,
    )
}

private enum class DemoTheme(val label: String) {
    Brand("Brand"),
    Ocean("Ocean"),
    Rose("Rose"),
    Mono("Mono"),
}

private enum class DemoContentMode(
    val label: String,
    val config: TabContentConfig,
) {
    Text("Text", TabContentConfig.Text),
    Icon("Icon", TabContentConfig.Icon),
    IconText("Icon text", TabContentConfig.IconText),
    SelectedIcon(
        "Text -> icon",
        TabContentConfig.Adaptive(
            unselected = TabContentStyle.Text,
            selected = TabContentStyle.Icon,
        ),
    ),
}

private enum class DemoIndicatorKind(val label: String) {
    Pill("Pill"),
    Rectangle("Rect"),
    Underline("Line"),
    Dash("Dash"),
    Dot("Dot"),
    Border("Border"),
}

private enum class DemoIndicatorPadding(
    val label: String,
    val horizontal: androidx.compose.ui.unit.Dp,
) {
    None("None", 0.dp),
    Small("Small", 6.dp),
    Medium("Medium", 12.dp),
    Large("Large", 18.dp),
}

private enum class DemoTabLook(val label: String) {
    Transparent("Flat"),
    Filled("Filled"),
    Outlined("Outline"),
}

private enum class DemoTabGap(
    val label: String,
    val spacing: androidx.compose.ui.unit.Dp,
    val tabHorizontalPadding: androidx.compose.ui.unit.Dp,
) {
    None("None", 0.dp, 4.dp),
    Tight("Tight", 1.dp, 6.dp),
    Small("Small", 3.dp, 8.dp),
    Medium("Medium", 8.dp, 12.dp),
    Large("Large", 14.dp, 16.dp),
}

private data class DemoPalette(
    val background: Color,
    val surface: Color,
    val content: Color,
    val muted: Color,
    val accent: Color,
    val onAccent: Color,
    val border: Color,
    val pageColors: List<Color>,
)

private fun DemoTheme.palette(): DemoPalette {
    return when (this) {
        DemoTheme.Brand -> DemoPalette(
            background = Color(0xFFFAFBFF),
            surface = Color.White,
            content = Color(0xFF172033),
            muted = Color(0xFF667085),
            accent = Color(0xFF2D6CDF),
            onAccent = Color.White,
            border = Color(0xFFD6DCE8),
            pageColors = listOf(
                Color(0xFF2D6CDF),
                Color(0xFF009688),
                Color(0xFFE24A68),
                Color(0xFF7E57C2),
                Color(0xFF455A64),
                Color(0xFFEF6C00),
            ),
        )

        DemoTheme.Ocean -> DemoPalette(
            background = Color(0xFFF2FAF9),
            surface = Color.White,
            content = Color(0xFF102A2A),
            muted = Color(0xFF58706F),
            accent = Color(0xFF00897B),
            onAccent = Color.White,
            border = Color(0xFFC8DFDC),
            pageColors = listOf(
                Color(0xFF00897B),
                Color(0xFF039BE5),
                Color(0xFF43A047),
                Color(0xFF546E7A),
                Color(0xFF00695C),
                Color(0xFF26A69A),
            ),
        )

        DemoTheme.Rose -> DemoPalette(
            background = Color(0xFFFFF7F8),
            surface = Color.White,
            content = Color(0xFF331820),
            muted = Color(0xFF7A5861),
            accent = Color(0xFFD81B60),
            onAccent = Color.White,
            border = Color(0xFFEACDD6),
            pageColors = listOf(
                Color(0xFFD81B60),
                Color(0xFF8E24AA),
                Color(0xFFE53935),
                Color(0xFF5E35B1),
                Color(0xFFC2185B),
                Color(0xFFAD1457),
            ),
        )

        DemoTheme.Mono -> DemoPalette(
            background = Color(0xFFF7F7F7),
            surface = Color.White,
            content = Color(0xFF191919),
            muted = Color(0xFF666666),
            accent = Color(0xFF202020),
            onAccent = Color.White,
            border = Color(0xFFD5D5D5),
            pageColors = listOf(
                Color(0xFF202020),
                Color(0xFF424242),
                Color(0xFF616161),
                Color(0xFF757575),
                Color(0xFF303030),
                Color(0xFF555555),
            ),
        )
    }
}

private fun DemoIndicatorKind.style(
    color: Color,
    borderColor: Color,
    placement: IndicatorPlacement,
    horizontalPadding: androidx.compose.ui.unit.Dp,
): TabIndicatorStyle {
    return when (this) {
        DemoIndicatorKind.Pill -> TabIndicatorStyle.Pill(
            color = color,
            placement = placement,
            horizontalPadding = horizontalPadding,
        )

        DemoIndicatorKind.Rectangle -> TabIndicatorStyle.Rectangle(
            color = color,
            placement = placement,
            horizontalPadding = horizontalPadding,
        )

        DemoIndicatorKind.Underline -> TabIndicatorStyle.Underline(
            color = color,
            placement = placement,
            horizontalPadding = horizontalPadding,
        )

        DemoIndicatorKind.Dash -> TabIndicatorStyle.Dash(
            color = color,
            placement = placement,
            horizontalPadding = horizontalPadding,
        )

        DemoIndicatorKind.Dot -> TabIndicatorStyle.Dot(
            color = color,
            placement = placement,
        )

        DemoIndicatorKind.Border -> TabIndicatorStyle.Border(
            border = BorderStroke(1.dp, borderColor),
            placement = placement,
            horizontalPadding = horizontalPadding,
        )
    }
}

private fun DemoTabLook.style(
    palette: DemoPalette,
    selectedContentColor: Color,
    itemSpacing: androidx.compose.ui.unit.Dp,
    tabHorizontalPadding: androidx.compose.ui.unit.Dp,
): TabStyle {
    return when (this) {
        DemoTabLook.Transparent -> TabStyle.default(
            selectedContentColor = selectedContentColor,
            unselectedContentColor = palette.muted,
        )

        DemoTabLook.Filled -> TabStyle.filled(
            selectedContentColor = palette.onAccent,
            unselectedContentColor = palette.muted,
            selectedContainerColor = palette.accent,
        )

        DemoTabLook.Outlined -> TabStyle.outlined(
            selectedContentColor = palette.accent,
            unselectedContentColor = palette.muted,
            selectedBorderColor = palette.accent,
            unselectedBorderColor = palette.border,
        )
    }.copy(
        horizontalPadding = tabHorizontalPadding,
        itemSpacing = itemSpacing,
    )
}

private val IndicatorMotion.label: String
    get() = when (this) {
        IndicatorMotion.Slide -> "Slide"
        IndicatorMotion.Snake -> "Snake"
        IndicatorMotion.Bounce -> "Bounce"
        IndicatorMotion.Fade -> "Fade"
        IndicatorMotion.None -> "None"
    }

private val TabRowMotion.label: String
    get() = when (this) {
        TabRowMotion.Smooth -> "Smooth"
        TabRowMotion.Snappy -> "Snappy"
        TabRowMotion.Playful -> "Playful"
        TabRowMotion.None -> "None"
        is TabRowMotion.Custom -> "Custom"
    }

private val IndicatorPlacement.label: String
    get() = when (this) {
        IndicatorPlacement.Bottom -> "Bottom"
        IndicatorPlacement.Top -> "Top"
        IndicatorPlacement.BehindContent -> "Behind"
    }

@Composable
private fun PagerPage(
    title: String,
    color: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f))
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(color = color)
            }
            Text(
                text = title,
                color = color,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabRowDemoPreview() {
    TabRowTheme {
        TabRowDemo()
    }
}

private object DemoIcons {
    val Home = simpleIcon("Home") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 3f)
            lineTo(3f, 10f)
            lineTo(5f, 10f)
            lineTo(5f, 20f)
            lineTo(10f, 20f)
            lineTo(10f, 14f)
            lineTo(14f, 14f)
            lineTo(14f, 20f)
            lineTo(19f, 20f)
            lineTo(19f, 10f)
            lineTo(21f, 10f)
            close()
        }
    }

    val Search = simpleIcon("Search") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(10f, 4f)
            lineTo(15f, 7f)
            lineTo(15f, 13f)
            lineTo(10f, 16f)
            lineTo(5f, 13f)
            lineTo(5f, 7f)
            close()
            moveTo(14f, 14f)
            lineTo(20f, 20f)
            lineTo(18.5f, 21.5f)
            lineTo(12.5f, 15.5f)
            close()
        }
    }

    val Grid = simpleIcon("Grid") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(4f, 4f)
            lineTo(10f, 4f)
            lineTo(10f, 10f)
            lineTo(4f, 10f)
            close()
            moveTo(14f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 10f)
            lineTo(14f, 10f)
            close()
            moveTo(4f, 14f)
            lineTo(10f, 14f)
            lineTo(10f, 20f)
            lineTo(4f, 20f)
            close()
            moveTo(14f, 14f)
            lineTo(20f, 14f)
            lineTo(20f, 20f)
            lineTo(14f, 20f)
            close()
        }
    }

    val Heart = simpleIcon("Heart") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 21f)
            curveTo(7f, 17f, 4f, 14f, 4f, 9f)
            curveTo(4f, 6f, 6f, 4f, 9f, 4f)
            curveTo(10.5f, 4f, 11.5f, 5f, 12f, 6f)
            curveTo(12.5f, 5f, 13.5f, 4f, 15f, 4f)
            curveTo(18f, 4f, 20f, 6f, 20f, 9f)
            curveTo(20f, 14f, 17f, 17f, 12f, 21f)
            close()
        }
    }

    val User = simpleIcon("User") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 4f)
            lineTo(16f, 8f)
            lineTo(14f, 13f)
            lineTo(10f, 13f)
            lineTo(8f, 8f)
            close()
            moveTo(5f, 21f)
            curveTo(6f, 16f, 8.5f, 14f, 12f, 14f)
            curveTo(15.5f, 14f, 18f, 16f, 19f, 21f)
            close()
        }
    }

    val Settings = simpleIcon("Settings") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            lineTo(14f, 5f)
            lineTo(18f, 5f)
            lineTo(17f, 9f)
            lineTo(20f, 12f)
            lineTo(17f, 15f)
            lineTo(18f, 19f)
            lineTo(14f, 19f)
            lineTo(12f, 22f)
            lineTo(10f, 19f)
            lineTo(6f, 19f)
            lineTo(7f, 15f)
            lineTo(4f, 12f)
            lineTo(7f, 9f)
            lineTo(6f, 5f)
            lineTo(10f, 5f)
            close()
            moveTo(10f, 10f)
            lineTo(14f, 10f)
            lineTo(14f, 14f)
            lineTo(10f, 14f)
            close()
        }
    }
}

private fun simpleIcon(
    name: String,
    block: ImageVector.Builder.() -> Unit,
): ImageVector {
    return ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(block).build()
}
