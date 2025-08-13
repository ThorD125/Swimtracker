package com.thor.swim.tracker.screens.components.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max
import androidx.compose.ui.unit.Density
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class NumberEntryUi(val value: Int, val date: LocalDate)

private val DateFmt = DateTimeFormatter.ofPattern("dd_MM_yyyy")

enum class ChartRange(val key: String, val label: String) {
    WEEK("WEEK", "Last 7d"),
    MONTH("MONTH", "Last 30d"),
    ALL("ALL", "All")
}

val Context.dataStore by preferencesDataStore(name = "chart_prefs")
private val RANGE_KEY = stringPreferencesKey("chart_range")

private fun Int.niceStep(maxValue: Float): Float {
    if (maxValue <= 0f) return 1f
    val raw = maxValue / this
    val pow10 = 10f.pow(floor(kotlin.math.log10(raw)))
    val base = raw / pow10
    val niceBase = when {
        base <= 1f -> 1f
        base <= 2f -> 2f
        base <= 5f -> 5f
        else -> 10f
    }
    return niceBase * pow10
}

private fun niceCeil(value: Float, step: Float): Float {
    if (step == 0f) return value
    val n = ceil(value / step)
    return n * step
}

@Composable
fun LineChart(
    entries: List<NumberEntryUi>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.primary,
    axisColor: Color = Color.Gray,
    labelStyle: TextStyle = TextStyle(fontSize = 12.sp)
) {
    val appContext = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()

    val persistedRangeOrNull by remember {
        appContext.dataStore.data.map { prefs ->
            when (prefs[RANGE_KEY]) {
                ChartRange.WEEK.key -> ChartRange.WEEK
                ChartRange.ALL.key -> ChartRange.ALL
                ChartRange.MONTH.key -> ChartRange.MONTH
                else -> ChartRange.MONTH
            }
        }
    }.collectAsState(initial = null)

    var range by remember { mutableStateOf<ChartRange?>(null) }

    LaunchedEffect(persistedRangeOrNull) {
        if (persistedRangeOrNull != null && range == null) {
            range = persistedRangeOrNull
        }
    }

    if (range == null) {
        Row(
            modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) { Text("Loading…", style = labelStyle) }
        return
    }

    fun selectRange(new: ChartRange) {
        range = new
        scope.launch { appContext.dataStore.edit { it[RANGE_KEY] = new.key } }
    }

    val filtered = remember(entries, range) {
        if (entries.isEmpty()) emptyList() else {
            val pts = entries.map { it.date to it.value }.sortedBy { it.first }
            val maxDate = pts.last().first
            val cutoff = when (range!!) {
                ChartRange.WEEK -> maxDate.minusDays(6)
                ChartRange.MONTH -> maxDate.minusDays(29)
                ChartRange.ALL -> pts.first().first
            }
            pts.filter { it.first >= cutoff }
                .map { NumberEntryUi(it.second, it.first) }
        }
    }

    Column(modifier) {
        Row(Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RangeChip(current = range!!, target = ChartRange.WEEK) { selectRange(it) }
            RangeChip(current = range!!, target = ChartRange.MONTH) { selectRange(it) }
            RangeChip(current = range!!, target = ChartRange.ALL) { selectRange(it) }
        }
        Spacer(Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            Text("No data in this range", style = labelStyle)
        } else {
            LineChartCanvas(
                entries = filtered,
                lineColor = lineColor,
                pointColor = pointColor,
                axisColor = axisColor,
                labelStyle = labelStyle
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeChip(
    current: ChartRange,
    target: ChartRange,
    onSelect: (ChartRange) -> Unit
) {
    FilterChip(
        selected = current == target,
        onClick = { onSelect(target) },
        label = { Text(target.label) }
    )
}

@Composable
private fun LineChartCanvas(
    entries: List<NumberEntryUi>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    lineColor: Color,
    pointColor: Color,
    axisColor: Color,
    labelStyle: TextStyle
) {
    val points = entries
        .map { it.date to it.value }
        .sortedBy { it.first }

    val minDate = points.first().first
    val maxDate = points.last().first
    val totalDays = max(1L, ChronoUnit.DAYS.between(minDate, maxDate))

    val maxYData = max(1, points.maxOf { it.second })
    val stepGuess = 4.niceStep(maxYData.toFloat() / 0.85f)
    val yMax = niceCeil((maxYData / 0.85f), stepGuess).toInt().coerceAtLeast(1)
    val yStep = 4.niceStep(yMax.toFloat())
    val yTicks = buildList {
        var v = 0f
        while (v <= yMax + 0.001f) {
            add(v); v += yStep
        }
        if (last() < yMax) add(yMax.toFloat())
    }

    val composeDensity = androidx.compose.ui.platform.LocalDensity.current

    Column {
        Canvas(
            modifier = modifier
                .padding(22.dp)
        ) {
            val w = size.width
            val h = size.height

            fun xFor(date: LocalDate): Float {
                val daysFromStart = ChronoUnit.DAYS.between(minDate, date).toFloat()
                return if (totalDays == 0L) 0f else (daysFromStart / totalDays) * w
            }

            fun yFor(value: Int): Float {
                val ratio = value.toFloat() / yMax.toFloat()
                return h - (ratio * h)
            }

            drawLine(axisColor, Offset(0f, h), Offset(w, h), strokeWidth = 2f)
            drawLine(axisColor, Offset(0f, 0f), Offset(0f, h), strokeWidth = 2f)

            yTicks.forEach { tick ->
                val y = yFor(tick.toInt())
                drawLine(
                    color = axisColor.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                drawTextAt(
                    text = tick.toInt().toString(),
                    x = -8f,
                    y = y,
                    alignRight = true,
                    density = composeDensity,
                    style = labelStyle,
                    baselineAdjust = true
                )
            }

            val path = Path()
            points.forEachIndexed { idx, (d, v) ->
                val x = xFor(d)
                val y = yFor(v)
                if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            points.forEach { (d, v) ->
                val x = xFor(d)
                val y = yFor(v)
                drawCircle(color = pointColor, radius = 5f, center = Offset(x, y))
            }

            val desiredLabels = 4
            val intervals = desiredLabels - 1
            repeat(desiredLabels) { i ->
                val dayOffset = (totalDays * i / intervals.toFloat()).toLong()
                val date = minDate.plusDays(dayOffset)
                val x = xFor(date)
                drawLine(
                    color = axisColor,
                    start = Offset(x, h),
                    end = Offset(x, h + 6f),
                    strokeWidth = 2f
                )
                drawTextAt(
                    text = date.format(DateFmt),
                    x = x,
                    y = h + 18f,
                    center = true,
                    density = composeDensity,
                    style = labelStyle
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTextAt(
    text: String,
    x: Float,
    y: Float,
    alignRight: Boolean = false,
    center: Boolean = false,
    baselineAdjust: Boolean = false,
    density: Density,
    style: TextStyle
) {
    drawContext.canvas.nativeCanvas.apply {
        with(density) {
            val paint = android.graphics.Paint().apply {
                isAntiAlias = true
                textSize = style.fontSize.toPx()
                color = android.graphics.Color.argb(255, 80, 80, 80)
            }
            val width = paint.measureText(text)
            val fm = paint.fontMetrics
            val baseY = if (baselineAdjust) y else y - fm.ascent
            val drawX = when {
                center -> x - width / 2f
                alignRight -> x - width
                else -> x
            }
            drawText(text, drawX, baseY, paint)
        }
    }
}
