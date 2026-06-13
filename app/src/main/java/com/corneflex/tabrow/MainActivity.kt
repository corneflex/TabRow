package com.corneflex.tabrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.widthIn
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
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorPlacement
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentOptions
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentStyle
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentSwapPolicy
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentTransition
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorStyle
import com.corneflex.tabrow.ui.components.tabrow.config.TabRowMotion
import com.corneflex.tabrow.ui.components.tabrow.defaults.TabDefaults
import com.corneflex.tabrow.ui.components.tabrow.model.TabColors
import com.corneflex.tabrow.ui.components.tabrow.model.TabItem
import com.corneflex.tabrow.ui.components.tabrow.model.TabStyle
import com.corneflex.tabrow.ui.theme.TabRowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabRowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TabRowDemo(modifier = Modifier.padding(innerPadding))
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
    var previewPreset by remember { mutableStateOf(DemoPreviewPreset.Modern) }
    var theme by remember { mutableStateOf(DemoTheme.Brand) }
    var contentMode by remember { mutableStateOf(DemoContentMode.IconText) }
    var contentTransition by remember { mutableStateOf(DemoContentTransition.FadeScale) }
    var contentSwapPolicy by remember { mutableStateOf(DemoContentSwapPolicy.Coordinated) }
    var transitionPreview by remember { mutableStateOf(DemoTransitionPreview.CurrentMode) }
    var indicatorKind by remember { mutableStateOf(DemoIndicatorKind.Pill) }
    var indicatorMotion: IndicatorMotion by remember { mutableStateOf(IndicatorMotion.Slide) }
    var rowMotion: TabRowMotion by remember { mutableStateOf(TabRowMotion.Smooth) }
    var tabLook by remember { mutableStateOf(DemoTabLook.Transparent) }
    var tabGap by remember { mutableStateOf(DemoTabGap.Tight) }
    var placement by remember { mutableStateOf(IndicatorPlacement.BehindContent) }
    var indicatorPadding by remember { mutableStateOf(DemoIndicatorPadding.Medium) }

    val applyPreset: (DemoPreviewPreset) -> Unit = { preset ->
        previewPreset = preset
        theme = preset.theme
        contentMode = preset.contentMode
        contentTransition = preset.contentTransition
        contentSwapPolicy = preset.contentSwapPolicy
        transitionPreview = preset.transitionPreview
        indicatorKind = preset.indicatorKind
        indicatorMotion = preset.indicatorMotion
        rowMotion = preset.rowMotion
        tabLook = preset.tabLook
        tabGap = preset.tabGap
        placement = preset.placement
        indicatorPadding = preset.indicatorPadding
    }

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

    val tabColors = remember(tabLook, indicatorKind, placement, palette) {
        val selectedContentColor = if (
            placement == IndicatorPlacement.BehindContent && indicatorKind.hasFilledBackground
        ) palette.onAccent else palette.accent
        tabLook.colors(palette, selectedContentColor)
    }

    val labelSmall = MaterialTheme.typography.labelSmall
    val tabStyle = remember(tabGap, labelSmall) {
        TabDefaults.style(
            selectedTextStyle = labelSmall,
            unselectedTextStyle = labelSmall,
            horizontalPadding = tabGap.tabHorizontalPadding,
            itemSpacing = tabGap.spacing,
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
        DemoHeader(palette = palette)

        PreviewPanel(
            tabs = tabs,
            pagerState = pagerState,
            palette = palette,
            content = transitionPreview.resolve(contentMode),
            contentOptions = TabContentOptions(
                transition = contentTransition.transition,
                swapPolicy = contentSwapPolicy.policy,
            ),
            indicator = indicator,
            rowMotion = rowMotion,
            tabColors = tabColors,
            tabStyle = tabStyle,
            preset = previewPreset,
        )

        SettingsGroup(
            title = "Presets",
            subtitle = "Starting points for common tab row behaviors.",
            palette = palette,
        ) {
            PresetChoiceRow(
                presets = DemoPreviewPreset.entries,
                selected = previewPreset,
                palette = palette,
                onSelected = applyPreset,
            )
        }

        SettingsGroup(
            title = "Content",
            subtitle = "Choose what each tab renders and how selection changes are coordinated.",
            palette = palette,
        ) {
            SettingsSection(title = "Theme", palette = palette) {
                ChoiceRow(DemoTheme.entries, theme, { it.label }, palette) { theme = it }
            }
            SettingsSection(title = "Mode", palette = palette) {
                ChoiceRow(DemoContentMode.entries, contentMode, { it.label }, palette) { contentMode = it }
            }
            SettingsSection(title = "Preview", palette = palette) {
                ChoiceRow(DemoTransitionPreview.entries, transitionPreview, { it.label }, palette) { transitionPreview = it }
            }
            SettingsSection(title = "Content animation", palette = palette) {
                ChoiceRow(DemoContentTransition.entries, contentTransition, { it.label }, palette) { contentTransition = it }
            }
            SettingsSection(title = "Swap policy", palette = palette) {
                ChoiceRow(DemoContentSwapPolicy.entries, contentSwapPolicy, { it.label }, palette) { contentSwapPolicy = it }
            }
        }

        SettingsGroup(
            title = "Indicator",
            subtitle = "Tune the selected-state shape, placement and motion.",
            palette = palette,
        ) {
            SettingsSection(title = "Shape", palette = palette) {
                ChoiceRow(DemoIndicatorKind.entries, indicatorKind, { it.label }, palette) { indicatorKind = it }
            }
            SettingsSection(title = "Motion", palette = palette) {
                ChoiceRow(
                    items = DemoIndicatorMotionOption.entries,
                    selected = DemoIndicatorMotionOption.from(indicatorMotion),
                    label = { it.label },
                    palette = palette,
                ) { indicatorMotion = it.motion }
            }
            SettingsSection(title = "Padding", palette = palette) {
                ChoiceRow(DemoIndicatorPadding.entries, indicatorPadding, { it.label }, palette) { indicatorPadding = it }
            }
            SettingsSection(title = "Placement", palette = palette) {
                ChoiceRow(IndicatorPlacement.entries, placement, { it.label }, palette) { placement = it }
            }
        }

        SettingsGroup(
            title = "Layout",
            subtitle = "Adjust the row rhythm and tab treatment.",
            palette = palette,
        ) {
            SettingsSection(title = "Row motion", palette = palette) {
                ChoiceRow(
                    items = DemoRowMotionOption.entries,
                    selected = DemoRowMotionOption.from(rowMotion),
                    label = { it.label },
                    palette = palette,
                ) { rowMotion = it.motion }
            }
            SettingsSection(title = "Tab style", palette = palette) {
                ChoiceRow(DemoTabLook.entries, tabLook, { it.label }, palette) { tabLook = it }
            }
            SettingsSection(title = "Tab gap", palette = palette) {
                ChoiceRow(DemoTabGap.entries, tabGap, { it.label }, palette) { tabGap = it }
            }
        }
    }
}

@Composable
private fun DemoHeader(palette: DemoPalette) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Tab row studio",
            style = MaterialTheme.typography.headlineSmall,
            color = palette.content,
        )
        Text(
            text = "Compose playground for indicators, content morphing and pager-linked motion.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.muted,
        )
    }
}

@Composable
private fun PreviewPanel(
    tabs: List<TabItem>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    palette: DemoPalette,
    content: TabContentConfig,
    contentOptions: TabContentOptions,
    indicator: TabIndicatorConfig,
    rowMotion: TabRowMotion,
    tabColors: TabColors,
    tabStyle: TabStyle,
    preset: DemoPreviewPreset,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(palette.surface)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(preset.label, style = MaterialTheme.typography.titleMedium, color = palette.content)
                Text(preset.description, style = MaterialTheme.typography.bodySmall, color = palette.muted)
            }
            SignalDot(color = palette.accent)
        }

        CustomScrollableTabRow(
            tabs = tabs,
            pagerState = pagerState,
            content = content,
            contentOptions = contentOptions,
            indicator = indicator,
            motion = rowMotion,
            colors = tabColors,
            style = tabStyle,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.background)
                .height(188.dp),
        ) { page ->
            PagerPage(title = tabs[page].text.orEmpty(), color = palette.pageColors[page])
        }
    }
}

@Composable
private fun SignalDot(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        drawCircle(color = color.copy(alpha = 0.18f))
        drawCircle(color = color, radius = size.minDimension * 0.26f)
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    subtitle: String,
    palette: DemoPalette,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.content)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = palette.muted)
        }
        content()
    }
}

@Composable
private fun SettingsSection(
    title: String,
    palette: DemoPalette,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = palette.muted)
        content()
    }
}

@Composable
private fun PresetChoiceRow(
    presets: List<DemoPreviewPreset>,
    selected: DemoPreviewPreset,
    palette: DemoPalette,
    onSelected: (DemoPreviewPreset) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        presets.forEach { preset ->
            PresetChip(preset = preset, selected = preset == selected, palette = palette) { onSelected(preset) }
        }
    }
}

@Composable
private fun PresetChip(
    preset: DemoPreviewPreset,
    selected: Boolean,
    palette: DemoPalette,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        targetValue = if (selected) palette.accent.copy(alpha = 0.12f) else palette.background,
        label = "Preset background",
    )
    val border by animateColorAsState(
        targetValue = if (selected) palette.accent else palette.border,
        label = "Preset border",
    )
    Column(
        modifier = Modifier
            .widthIn(min = 150.dp, max = 190.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(BorderStroke(1.dp, border), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(preset.label, color = palette.content, style = MaterialTheme.typography.labelLarge)
        Text(preset.description, color = palette.muted, style = MaterialTheme.typography.bodySmall)
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
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            ChoiceChip(text = label(item), selected = item == selected, palette = palette) { onSelected(item) }
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
    val background by animateColorAsState(
        targetValue = if (selected) palette.accent else palette.background,
        label = "Chip background",
    )
    val content by animateColorAsState(
        targetValue = if (selected) palette.onAccent else palette.content,
        label = "Chip content",
    )
    val border by animateColorAsState(
        targetValue = if (selected) palette.accent else palette.border,
        label = "Chip border",
    )
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

// ─── Demo enums and data ──────────────────────────────────────────────────────

private enum class DemoTheme(val label: String) {
    Brand("Brand"), Ocean("Ocean"), Rose("Rose"), Mono("Mono"),
}

private enum class DemoPreviewPreset(
    val label: String,
    val description: String,
    val theme: DemoTheme,
    val contentMode: DemoContentMode,
    val contentTransition: DemoContentTransition,
    val contentSwapPolicy: DemoContentSwapPolicy,
    val transitionPreview: DemoTransitionPreview,
    val indicatorKind: DemoIndicatorKind,
    val indicatorMotion: IndicatorMotion,
    val rowMotion: TabRowMotion,
    val tabLook: DemoTabLook,
    val tabGap: DemoTabGap,
    val placement: IndicatorPlacement,
    val indicatorPadding: DemoIndicatorPadding,
) {
    Modern(
        label = "Modern", description = "Filled pill with balanced content.",
        theme = DemoTheme.Brand, contentMode = DemoContentMode.IconText,
        contentTransition = DemoContentTransition.FadeScale, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.CurrentMode, indicatorKind = DemoIndicatorKind.Pill,
        indicatorMotion = IndicatorMotion.Slide, rowMotion = TabRowMotion.Smooth,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Tight,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Medium,
    ),
    Minimal(
        label = "Minimal", description = "Text tabs with a quiet underline.",
        theme = DemoTheme.Mono, contentMode = DemoContentMode.Text,
        contentTransition = DemoContentTransition.Fade, contentSwapPolicy = DemoContentSwapPolicy.Together,
        transitionPreview = DemoTransitionPreview.CurrentMode, indicatorKind = DemoIndicatorKind.Underline,
        indicatorMotion = IndicatorMotion.Slide, rowMotion = TabRowMotion.Smooth,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Small,
        placement = IndicatorPlacement.Bottom, indicatorPadding = DemoIndicatorPadding.Medium,
    ),
    Playful(
        label = "Playful", description = "Text morphs into icons with snake motion.",
        theme = DemoTheme.Rose, contentMode = DemoContentMode.SelectedIcon,
        contentTransition = DemoContentTransition.ExpandFade, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.TextToIcon, indicatorKind = DemoIndicatorKind.Pill,
        indicatorMotion = IndicatorMotion.Snake, rowMotion = TabRowMotion.Playful,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Tight,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Small,
    ),
    Compact(
        label = "Compact", description = "Icon-only tabs with a bouncing dot.",
        theme = DemoTheme.Ocean, contentMode = DemoContentMode.Icon,
        contentTransition = DemoContentTransition.FadeScale, contentSwapPolicy = DemoContentSwapPolicy.Together,
        transitionPreview = DemoTransitionPreview.CurrentMode, indicatorKind = DemoIndicatorKind.Dot,
        indicatorMotion = IndicatorMotion.Bounce, rowMotion = TabRowMotion.Snappy,
        tabLook = DemoTabLook.Outlined, tabGap = DemoTabGap.None,
        placement = IndicatorPlacement.Bottom, indicatorPadding = DemoIndicatorPadding.None,
    ),
    Outline(
        label = "Outline", description = "Outlined selection with icon transitions.",
        theme = DemoTheme.Brand, contentMode = DemoContentMode.IconText,
        contentTransition = DemoContentTransition.SlideLeft, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.IconTextToIcon, indicatorKind = DemoIndicatorKind.Border,
        indicatorMotion = IndicatorMotion.Fade, rowMotion = TabRowMotion.Smooth,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Small,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Small,
    ),
    SideBorder(
        label = "Side border", description = "Flat top and bottom with rounded sides.",
        theme = DemoTheme.Brand, contentMode = DemoContentMode.SelectedIcon,
        contentTransition = DemoContentTransition.FadeScale, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.TextToIcon, indicatorKind = DemoIndicatorKind.SideRoundedBorder,
        indicatorMotion = IndicatorMotion.Slide, rowMotion = TabRowMotion.Smooth,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Tight,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Small,
    ),
    DualRail(
        label = "Dual rail", description = "Top and bottom rails around selected content.",
        theme = DemoTheme.Mono, contentMode = DemoContentMode.Text,
        contentTransition = DemoContentTransition.FadeThrough, contentSwapPolicy = DemoContentSwapPolicy.Together,
        transitionPreview = DemoTransitionPreview.CurrentMode, indicatorKind = DemoIndicatorKind.TopBottomBorder,
        indicatorMotion = IndicatorMotion.Fade, rowMotion = TabRowMotion.Snappy,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Small,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Medium,
    ),
    Segmented(
        label = "Segmented", description = "Filled tabs with a rectangular indicator.",
        theme = DemoTheme.Ocean, contentMode = DemoContentMode.IconText,
        contentTransition = DemoContentTransition.SlideRight, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.CurrentMode, indicatorKind = DemoIndicatorKind.Rectangle,
        indicatorMotion = IndicatorMotion.Slide, rowMotion = TabRowMotion.Smooth,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.None,
        placement = IndicatorPlacement.BehindContent, indicatorPadding = DemoIndicatorPadding.Small,
    ),
    TopLine(
        label = "Top line", description = "A compact indicator anchored above the row.",
        theme = DemoTheme.Rose, contentMode = DemoContentMode.IconText,
        contentTransition = DemoContentTransition.SlideUp, contentSwapPolicy = DemoContentSwapPolicy.Coordinated,
        transitionPreview = DemoTransitionPreview.IconToIconText, indicatorKind = DemoIndicatorKind.Dash,
        indicatorMotion = IndicatorMotion.Snake, rowMotion = TabRowMotion.Playful,
        tabLook = DemoTabLook.Transparent, tabGap = DemoTabGap.Small,
        placement = IndicatorPlacement.Top, indicatorPadding = DemoIndicatorPadding.Large,
    ),
}

private enum class DemoTransitionPreview(val label: String) {
    CurrentMode("Current"),
    TextToIcon("Text -> icon"),
    IconToText("Icon -> text"),
    IconTextToIcon("Icon text -> icon"),
    IconToIconText("Icon -> icon text");

    fun resolve(contentMode: DemoContentMode): TabContentConfig = when (this) {
        CurrentMode -> contentMode.config
        TextToIcon -> TabContentConfig.Adaptive(TabContentStyle.Text, TabContentStyle.Icon)
        IconToText -> TabContentConfig.Adaptive(TabContentStyle.Icon, TabContentStyle.Text)
        IconTextToIcon -> TabContentConfig.Adaptive(TabContentStyle.IconText, TabContentStyle.Icon)
        IconToIconText -> TabContentConfig.Adaptive(TabContentStyle.Icon, TabContentStyle.IconText)
    }
}

private enum class DemoContentMode(val label: String, val config: TabContentConfig) {
    Text("Text", TabContentConfig.Text),
    Icon("Icon", TabContentConfig.Icon),
    IconText("Icon text", TabContentConfig.IconText),
    SelectedIcon("Text -> icon", TabContentConfig.Adaptive(TabContentStyle.Text, TabContentStyle.Icon)),
}

private enum class DemoContentTransition(val label: String, val transition: TabContentTransition) {
    None("None", TabContentTransition.None),
    Fade("Fade", TabContentTransition.Fade),
    FadeScale("Fade scale", TabContentTransition.FadeScale),
    Scale("Scale", TabContentTransition.Scale),
    SlideLeft("Slide left", TabContentTransition.SlideLeft),
    SlideRight("Slide right", TabContentTransition.SlideRight),
    SlideUp("Slide up", TabContentTransition.SlideUp),
    SlideDown("Slide down", TabContentTransition.SlideDown),
    FadeThrough("Fade through", TabContentTransition.FadeThrough),
    Expand("Expand", TabContentTransition.Expand),
    ExpandFade("Expand fade", TabContentTransition.ExpandFade),
}

private enum class DemoContentSwapPolicy(val label: String, val policy: TabContentSwapPolicy) {
    Coordinated("Coordinated", TabContentSwapPolicy.Coordinated),
    Sequential("Sequential", TabContentSwapPolicy.DeselectThenSelect()),
    Together("Together", TabContentSwapPolicy.Together),
    SlowSequence("Slow sequence", TabContentSwapPolicy.DeselectThenSelect(delayMillis = 220L)),
}

private enum class DemoIndicatorMotionOption(val label: String, val motion: IndicatorMotion) {
    Slide("Slide", IndicatorMotion.Slide),
    Snake("Snake", IndicatorMotion.Snake),
    Bounce("Bounce", IndicatorMotion.Bounce),
    Fade("Fade", IndicatorMotion.Fade),
    None("None", IndicatorMotion.None);

    companion object {
        fun from(motion: IndicatorMotion) = entries.first { it.motion == motion }
    }
}

private enum class DemoRowMotionOption(val label: String, val motion: TabRowMotion) {
    Smooth("Smooth", TabRowMotion.Smooth),
    Snappy("Snappy", TabRowMotion.Snappy),
    Playful("Playful", TabRowMotion.Playful),
    None("None", TabRowMotion.None);

    companion object {
        fun from(motion: TabRowMotion) = entries.firstOrNull { it.motion == motion } ?: Smooth
    }
}

private enum class DemoIndicatorKind(val label: String) {
    Pill("Pill"), Rectangle("Rect"), Underline("Line"), Dash("Dash"),
    Dot("Dot"), Border("Border"), SideRoundedBorder("Side border"), TopBottomBorder("Top/bottom"),
}

private val DemoIndicatorKind.hasFilledBackground: Boolean
    get() = this == DemoIndicatorKind.Pill || this == DemoIndicatorKind.Rectangle

private enum class DemoIndicatorPadding(val label: String, val horizontal: androidx.compose.ui.unit.Dp) {
    None("None", 0.dp), Small("Small", 6.dp), Medium("Medium", 12.dp), Large("Large", 18.dp),
}

private enum class DemoTabLook(val label: String) {
    Transparent("Flat"), Filled("Filled"), Outlined("Outline"),
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

private fun DemoTheme.palette(): DemoPalette = when (this) {
    DemoTheme.Brand -> DemoPalette(
        background = Color(0xFFFAFBFF), surface = Color.White,
        content = Color(0xFF172033), muted = Color(0xFF667085),
        accent = Color(0xFF2D6CDF), onAccent = Color.White,
        border = Color(0xFFD6DCE8),
        pageColors = listOf(Color(0xFF2D6CDF), Color(0xFF009688), Color(0xFFE24A68), Color(0xFF7E57C2), Color(0xFF455A64), Color(0xFFEF6C00)),
    )
    DemoTheme.Ocean -> DemoPalette(
        background = Color(0xFFF2FAF9), surface = Color.White,
        content = Color(0xFF102A2A), muted = Color(0xFF58706F),
        accent = Color(0xFF00897B), onAccent = Color.White,
        border = Color(0xFFC8DFDC),
        pageColors = listOf(Color(0xFF00897B), Color(0xFF039BE5), Color(0xFF43A047), Color(0xFF546E7A), Color(0xFF00695C), Color(0xFF26A69A)),
    )
    DemoTheme.Rose -> DemoPalette(
        background = Color(0xFFFFF7F8), surface = Color.White,
        content = Color(0xFF331820), muted = Color(0xFF7A5861),
        accent = Color(0xFFD81B60), onAccent = Color.White,
        border = Color(0xFFEACDD6),
        pageColors = listOf(Color(0xFFD81B60), Color(0xFF8E24AA), Color(0xFFE53935), Color(0xFF5E35B1), Color(0xFFC2185B), Color(0xFFAD1457)),
    )
    DemoTheme.Mono -> DemoPalette(
        background = Color(0xFFF7F7F7), surface = Color.White,
        content = Color(0xFF191919), muted = Color(0xFF666666),
        accent = Color(0xFF202020), onAccent = Color.White,
        border = Color(0xFFD5D5D5),
        pageColors = listOf(Color(0xFF202020), Color(0xFF424242), Color(0xFF616161), Color(0xFF757575), Color(0xFF303030), Color(0xFF555555)),
    )
}

private fun DemoIndicatorKind.style(
    color: Color,
    borderColor: Color,
    placement: IndicatorPlacement,
    horizontalPadding: androidx.compose.ui.unit.Dp,
): TabIndicatorStyle = when (this) {
    DemoIndicatorKind.Pill -> TabIndicatorStyle.Pill(color = color, placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.Rectangle -> TabIndicatorStyle.Rectangle(color = color, placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.Underline -> TabIndicatorStyle.Underline(color = color, placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.Dash -> TabIndicatorStyle.Dash(color = color, placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.Dot -> TabIndicatorStyle.Dot(color = color, placement = placement)
    DemoIndicatorKind.Border -> TabIndicatorStyle.Border(border = BorderStroke(1.dp, borderColor), placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.SideRoundedBorder -> TabIndicatorStyle.SideRoundedBorder(border = BorderStroke(1.dp, borderColor), placement = placement, horizontalPadding = horizontalPadding)
    DemoIndicatorKind.TopBottomBorder -> TabIndicatorStyle.TopBottomBorder(lineColor = borderColor, lineWidth = 1.dp, placement = placement, horizontalPadding = horizontalPadding)
}

private fun DemoTabLook.colors(palette: DemoPalette, selectedContentColor: Color): TabColors = when (this) {
    DemoTabLook.Transparent -> TabDefaults.outlinedColors(
        selectedContentColor = selectedContentColor,
        unselectedContentColor = palette.muted,
    )
    DemoTabLook.Filled -> TabDefaults.filledColors(
        selectedContentColor = palette.onAccent,
        unselectedContentColor = palette.muted,
        selectedContainerColor = palette.accent,
    )
    DemoTabLook.Outlined -> TabDefaults.outlinedColors(
        selectedContentColor = palette.accent,
        unselectedContentColor = palette.muted,
    )
}

private val IndicatorPlacement.label: String
    get() = when (this) {
        IndicatorPlacement.Bottom -> "Bottom"
        IndicatorPlacement.Top -> "Top"
        IndicatorPlacement.BehindContent -> "Behind"
    }

@Composable
private fun PagerPage(title: String, color: Color) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
            Canvas(modifier = Modifier.size(12.dp)) { drawCircle(color = color) }
            Text(text = title, color = color, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabRowDemoPreview() {
    TabRowTheme { TabRowDemo() }
}

private object DemoIcons {
    val Home = simpleIcon("Home") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 3f); lineTo(3f, 10f); lineTo(5f, 10f); lineTo(5f, 20f)
            lineTo(10f, 20f); lineTo(10f, 14f); lineTo(14f, 14f); lineTo(14f, 20f)
            lineTo(19f, 20f); lineTo(19f, 10f); lineTo(21f, 10f); close()
        }
    }
    val Search = simpleIcon("Search") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(10f, 4f); lineTo(15f, 7f); lineTo(15f, 13f); lineTo(10f, 16f)
            lineTo(5f, 13f); lineTo(5f, 7f); close()
            moveTo(14f, 14f); lineTo(20f, 20f); lineTo(18.5f, 21.5f); lineTo(12.5f, 15.5f); close()
        }
    }
    val Grid = simpleIcon("Grid") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(4f, 4f); lineTo(10f, 4f); lineTo(10f, 10f); lineTo(4f, 10f); close()
            moveTo(14f, 4f); lineTo(20f, 4f); lineTo(20f, 10f); lineTo(14f, 10f); close()
            moveTo(4f, 14f); lineTo(10f, 14f); lineTo(10f, 20f); lineTo(4f, 20f); close()
            moveTo(14f, 14f); lineTo(20f, 14f); lineTo(20f, 20f); lineTo(14f, 20f); close()
        }
    }
    val Heart = simpleIcon("Heart") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 21f)
            curveTo(7f, 17f, 4f, 14f, 4f, 9f); curveTo(4f, 6f, 6f, 4f, 9f, 4f)
            curveTo(10.5f, 4f, 11.5f, 5f, 12f, 6f); curveTo(12.5f, 5f, 13.5f, 4f, 15f, 4f)
            curveTo(18f, 4f, 20f, 6f, 20f, 9f); curveTo(20f, 14f, 17f, 17f, 12f, 21f); close()
        }
    }
    val User = simpleIcon("User") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 4f); lineTo(16f, 8f); lineTo(14f, 13f); lineTo(10f, 13f); lineTo(8f, 8f); close()
            moveTo(5f, 21f); curveTo(6f, 16f, 8.5f, 14f, 12f, 14f); curveTo(15.5f, 14f, 18f, 16f, 19f, 21f); close()
        }
    }
    val Settings = simpleIcon("Settings") {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.Black)) {
            moveTo(12f, 2f); lineTo(14f, 5f); lineTo(18f, 5f); lineTo(17f, 9f); lineTo(20f, 12f)
            lineTo(17f, 15f); lineTo(18f, 19f); lineTo(14f, 19f); lineTo(12f, 22f); lineTo(10f, 19f)
            lineTo(6f, 19f); lineTo(7f, 15f); lineTo(4f, 12f); lineTo(7f, 9f); lineTo(6f, 5f); lineTo(10f, 5f); close()
            moveTo(10f, 10f); lineTo(14f, 10f); lineTo(14f, 14f); lineTo(10f, 14f); close()
        }
    }
}

private fun simpleIcon(name: String, block: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(block).build()
