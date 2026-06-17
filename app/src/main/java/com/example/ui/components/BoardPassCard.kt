package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Client
import com.example.data.CollectionStamp
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenSecondary

@Composable
fun BoardPassCard(
    client: Client,
    stamps: List<CollectionStamp>,
    modifier: Modifier = Modifier,
    onStampClick: ((Int) -> Unit)? = null // If provided, allows quick toggling (for collectors)
) {
    val totalStamps = 25
    val stampedIndices = stamps.map { it.stampIndex }.toSet()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("board_pass_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp) // African pattern at the very bottom
        ) {
            // Header Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Eco Beta logo representation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(EcoGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "β",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.offset(y = (-1).dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ECO BETA",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = EcoGreenPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "LIXO NA PORTA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ID Indicator
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.testTag("client_badge_id")
                ) {
                    Text(
                        text = "ID : ${client.clientCode.uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Divider Dash Line
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
            ) {
                drawLine(
                    color = EcoGreenPrimary.copy(alpha = 0.2f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            // Central block (Information Left, Grid Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: Core client info
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        value = client.address,
                        testTag = "info_address"
                    )
                    InfoRow(
                        icon = Icons.Default.Phone,
                        value = client.phone,
                        testTag = "info_phone"
                    )
                    InfoRow(
                        icon = Icons.Default.Home,
                        value = "Plano ${client.feeType} (${client.feeAmount.toInt()} AOA)",
                        testTag = "info_plan"
                    )
                }

                // Right Column: 5x5 Stamps Board Grid
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .border(
                            width = 1.dp,
                            color = EcoGreenPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CONTROLO RECOLHAS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // 5x3 or 5x5 Grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (col in 0 until 5) {
                                    val index = row * 5 + col
                                    val isStamped = stampedIndices.contains(index)
                                    val stampDetails = stamps.find { it.stampIndex == index }

                                    StampCell(
                                        index = index + 1,
                                        isStamped = isStamped,
                                        isClickable = onStampClick != null,
                                        onClick = { onStampClick?.invoke(index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Stripe / Tribal Geometric Pattern
            TribalPatternBorder(modifier = Modifier.fillMaxWidth())

            // Footer Contact Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "www.ecobeta.inc@outlook.com",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.testTag("footer_email")
                )
                Text(
                    text = "+244 925281345",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary,
                    modifier = Modifier.testTag("footer_phone")
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    value: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EcoGreenPrimary,
            modifier = Modifier
                .size(20.dp)
                .offset(y = 2.dp)
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            // dotted underline representing handwritten placeholder from the printed ticket
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(1.dp)
            ) {
                drawLine(
                    color = EcoGreenPrimary.copy(alpha = 0.15f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Composable
fun StampCell(
    index: Int,
    isStamped: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeDp = 26.dp
    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(
                if (isStamped) EcoGreenPrimary else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isStamped) EcoGreenPrimary else EcoGreenPrimary.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(enabled = isClickable) { onClick() }
            .testTag("stamp_cell_$index"),
        contentAlignment = Alignment.Center
    ) {
        if (isStamped) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Recolha $index Concluída",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Recolha $index Pendente",
                tint = EcoGreenPrimary.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun TribalPatternBorder(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val width = size.width
        val height = size.height

        // Draw background
        drawRect(color = Color(0xFFF9F7F2))

        // Draw a simple beautiful black/dark geometric sub-Saharan tribal line pattern
        val patternWidth = 12f
        var x = 0f
        val darkColor = Color(0xFF1B2E1E)

        while (x < width) {
            // Draw a tiny geometric box and chevron pattern
            drawLine(
                color = darkColor,
                start = Offset(x, 0f),
                end = Offset(x + patternWidth / 2, height),
                strokeWidth = 3f
            )
            drawLine(
                color = darkColor,
                start = Offset(x + patternWidth / 2, height),
                end = Offset(x + patternWidth, 0f),
                strokeWidth = 3f
            )

            // Draw little horizontal details in between
            drawRect(
                color = EcoGreenSecondary.copy(alpha = 0.4f),
                topLeft = Offset(x + patternWidth/4, height/3),
                size = androidx.compose.ui.geometry.Size(3f, 3f)
            )

            x += patternWidth
        }

        // Top line
        drawLine(
            color = darkColor,
            start = Offset(0f, 0f),
            end = Offset(width, 0f),
            strokeWidth = 4f
        )
        // Bottom line
        drawLine(
            color = darkColor,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 4f
        )
    }
}
