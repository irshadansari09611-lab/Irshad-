package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.IPLog
import java.text.SimpleDateFormat
import java.util.*

// Style Color Constants matching Bento themes
val BentoBg = Color(0xFFF3F4F9)
val BentoDark = Color(0xFF1B1B1F)
val BentoLightBlue = Color(0xFFDDE1FF)
val BentoPrimary = Color(0xFF005AC1)
val BentoPrimaryContainer = Color(0xFF001D49)
val BentoBorder = Color(0xFFE1E2EC)
val BentoTextGray = Color(0xFF44474E)
val BentoGreen = Color(0xFF006E1C)
val BentoRed = Color(0xFFBA1A1A)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IPAppScreen(viewModel: IPAgentViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isRotating by viewModel.isRotating.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMsg by viewModel.errorMessage.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        containerColor = BentoBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "IP Agent",
                            color = BentoDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.fetchActiveIP() },
                        modifier = Modifier
                            .testTag("refresh_top_button")
                            .border(1.dp, BentoBorder, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Info",
                            tint = BentoTextGray,
                            modifier = Modifier.rotate(if (isLoading) 360f else 0f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoBg,
                    titleContentColor = BentoDark
                )
            )
        },
        bottomBar = {
            Column {
                Divider(color = BentoBorder.copy(alpha = 0.5f))
                NavigationBar(
                    containerColor = BentoBg,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == "home",
                        onClick = { viewModel.selectTab("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoPrimaryContainer,
                            selectedTextColor = BentoPrimaryContainer,
                            indicatorColor = BentoLightBlue,
                            unselectedIconColor = BentoTextGray.copy(alpha = 0.7f),
                            unselectedTextColor = BentoTextGray.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == "global",
                        onClick = { viewModel.selectTab("global") },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Global Lookup") },
                        label = { Text("Lookup") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoPrimaryContainer,
                            selectedTextColor = BentoPrimaryContainer,
                            indicatorColor = BentoLightBlue,
                            unselectedIconColor = BentoTextGray.copy(alpha = 0.7f),
                            unselectedTextColor = BentoTextGray.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_lookup")
                    )
                    NavigationBarItem(
                        selected = selectedTab == "logs",
                        onClick = { viewModel.selectTab("logs") },
                        icon = { Icon(Icons.Default.List, contentDescription = "History Logs") },
                        label = { Text("Logs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoPrimaryContainer,
                            selectedTextColor = BentoPrimaryContainer,
                            indicatorColor = BentoLightBlue,
                            unselectedIconColor = BentoTextGray.copy(alpha = 0.7f),
                            unselectedTextColor = BentoTextGray.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_logs")
                    )
                    NavigationBarItem(
                        selected = selectedTab == "profile",
                        onClick = { viewModel.selectTab("profile") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile Configuration") },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoPrimaryContainer,
                            selectedTextColor = BentoPrimaryContainer,
                            indicatorColor = BentoLightBlue,
                            unselectedIconColor = BentoTextGray.copy(alpha = 0.7f),
                            unselectedTextColor = BentoTextGray.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Display error message at top with fade-in/out
                AnimatedVisibility(
                    visible = errorMsg != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    errorMsg?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoRed.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, BentoRed.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = BentoRed)
                                Text(
                                    text = msg,
                                    color = BentoRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Inner content matching tab
                when (selectedTab) {
                    "home" -> HomeTabContent(viewModel, isRotating)
                    "global" -> GlobalTabContent(viewModel)
                    "logs" -> LogsTabContent(viewModel)
                    "profile" -> ProfileTabContent(viewModel)
                }
            }

            // Connecting/Rotating Loader Overlay
            AnimatedVisibility(
                visible = isRotating,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoDark),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = BentoLightBlue,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Rotating Proxy Client...",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Generating new secure interface path...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabContent(viewModel: IPAgentViewModel, isRotating: Boolean) {
    val currentIP by viewModel.currentIP.collectAsStateWithLifecycle()
    val city by viewModel.city.collectAsStateWithLifecycle()
    val country by viewModel.country.collectAsStateWithLifecycle()
    val provider by viewModel.provider.collectAsStateWithLifecycle()
    val latency by viewModel.latency.collectAsStateWithLifecycle()
    val protocol by viewModel.protocol.collectAsStateWithLifecycle()
    val autoRotation by viewModel.autoRotation.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
            .testTag("home_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active IP Hero Bento Card
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoLightBlue),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .testTag("active_ip_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE IP",
                        color = BentoPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BentoPrimary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SECURED",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = currentIP,
                    color = BentoPrimaryContainer,
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("display_ip_text")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing green dot
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(BentoGreen.copy(alpha = pulseAlpha), CircleShape)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = "Proxy active: $city, $country",
                        color = BentoPrimaryContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bento 2x2 Grid Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cell 1: Ping Latency Card (Clickable to run ping check simulation)
            BentoMiniGridCell(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.fetchActiveIP() }
                    .testTag("ping_cell"),
                icon = Icons.Default.Speed,
                title = "Ping Latency",
                value = "$latency ms",
                iconTint = BentoPrimary
            )

            // Cell 2: Provider Card
            BentoMiniGridCell(
                modifier = Modifier
                    .weight(1f)
                    .testTag("provider_cell"),
                icon = Icons.Default.Public,
                title = "Network Owner",
                value = provider,
                iconTint = BentoPrimary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cell 3: Protocol Card
            BentoMiniGridCell(
                modifier = Modifier
                    .weight(1f)
                    .testTag("protocol_cell"),
                icon = Icons.Default.Shield,
                title = "Crypto Suite",
                value = protocol,
                iconTint = BentoPrimary
            )

            // Cell 4: Auto Rotation Custom Dark Cell
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoDark),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
                    .clickable { viewModel.toggleAutoRotation() }
                    .testTag("auto_rotation_cell")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rotate",
                        tint = BentoLightBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = if (autoRotation) "Auto Enabled" else "Auto Manual",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (autoRotation) "Checking each 5m" else "Tap client to spin",
                            color = BentoLightBlue.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Action Trigger Card
        Button(
            onClick = { viewModel.rotateIP() },
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .testTag("trigger_rotate_button")
        ) {
            Icon(Icons.Default.SyncAlt, contentDescription = "Swap Link", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Rotate Secure Connection Now",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun BentoMiniGridCell(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    iconTint: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = value,
                    color = BentoDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title.uppercase(),
                    color = BentoTextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun GlobalTabContent(viewModel: IPAgentViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
            .testTag("lookup_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Geo-IP Tracker",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BentoDark,
            letterSpacing = (-0.5).sp
        )

        Text(
            text = "Query details for any IPv4 or domain directly across deep network routing nodes.",
            fontSize = 14.sp,
            color = BentoTextGray
        )

        // Lookup Inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("e.g. 1.1.1.1 or 8.8.8.8") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    viewModel.performCustomLookup()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ip_lookup_input")
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.performCustomLookup()
                },
                enabled = searchQuery.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.testTag("ip_lookup_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Scan")
                }
            }
        }

        // Custom Results Grid (Presented as aesthetic Bento sections)
        AnimatedVisibility(
            visible = searchResult != null,
            enter = fadeIn() + expandVertically()
        ) {
            searchResult?.let { result ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoLightBlue),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "TARGET HOST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimaryContainer.copy(alpha = 0.5f)
                            )
                            Text(
                                text = result.ip ?: "N/A",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Located in: ${result.city ?: "Unknown"}, ${result.countryName ?: "Unknown"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoPrimaryContainer
                            )
                        }
                    }

                    // Bento info cells for results
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LogIndicatorCell(
                            modifier = Modifier.weight(1f),
                            title = "Autonomous System",
                            value = result.asn ?: "AS15169"
                        )
                        LogIndicatorCell(
                            modifier = Modifier.weight(1f),
                            title = "Timezone",
                            value = result.timezone ?: "UTC"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LogIndicatorCell(
                            modifier = Modifier.weight(1f),
                            title = "Latitude",
                            value = result.latitude?.toString() ?: "0.0"
                        )
                        LogIndicatorCell(
                            modifier = Modifier.weight(1f),
                            title = "Longitude",
                            value = result.longitude?.toString() ?: "0.0"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LogIndicatorCell(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BentoDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LogsTabContent(viewModel: IPAgentViewModel) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
            .testTag("logs_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Agent Logs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoDark
                )
                Text(
                    text = "${logs.size} connection checkpoints",
                    fontSize = 12.sp,
                    color = BentoTextGray
                )
            }

            if (logs.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", color = BentoRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "No checkpoints",
                        tint = BentoTextGray.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "History Ledger Empty",
                        color = BentoDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Your connection security history is logged locally right here.",
                        color = BentoTextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("logs_list")
            ) {
                items(logs) { log ->
                    LogItemRow(log)
                }
            }
        }
    }
}

@Composable
fun LogItemRow(log: IPLog) {
    val sdf = remember { SimpleDateFormat("HH:mm:ss · MMM dd, yyyu", Locale.US) }
    val timeString = remember(log.date) { sdf.format(Date(log.date)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = log.ip,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BentoDark
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BentoLightBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.country,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = BentoPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$timeString  ·  ${log.city}",
                    fontSize = 12.sp,
                    color = BentoTextGray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.latency}ms",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (log.latency < 25) BentoGreen else BentoTextGray
                )
                Text(
                    text = log.protocol,
                    fontSize = 10.sp,
                    color = BentoTextGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProfileTabContent(viewModel: IPAgentViewModel) {
    val activeProtocol by viewModel.protocol.collectAsStateWithLifecycle()
    val autoRotation by viewModel.autoRotation.collectAsStateWithLifecycle()

    var showProtocolDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
            .testTag("profile_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile & Cryptography",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BentoDark
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = BentoLightBlue),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Active Seal", tint = BentoPrimary)
                }

                Column {
                    Text(
                        text = "IP Agent VIP Client",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BentoPrimaryContainer
                    )
                    Text(
                        text = "Local Sandboxing Mode Active",
                        fontSize = 12.sp,
                        color = BentoPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Text(
            text = "AGENT PREFERENCES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Preference item 1: Selected Protocol
        PreferenceSettingItem(
            title = "Security Protocol",
            subtitle = "Active cipher tunnel: $activeProtocol",
            icon = Icons.Default.Security,
            onClick = { showProtocolDialog = true },
            modifier = Modifier.testTag("crypto_protocol_setting")
        )

        // Preference item 2: Auto rotation toggling
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rotation_toggle_setting")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Update, contentDescription = null, tint = BentoPrimary)
                    Column {
                        Text("Auto-IP Rotation Cycle", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BentoDark)
                        Text(
                            text = "Spin node endpoints dynamic schedules",
                            fontSize = 12.sp,
                            color = BentoTextGray
                        )
                    }
                }
                Switch(
                    checked = autoRotation,
                    onCheckedChange = { viewModel.toggleAutoRotation() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BentoPrimary
                    ),
                    modifier = Modifier.testTag("rotation_switch")
                )
            }
        }

        // Preference item 3: System Status
        LogIndicatorCell(
            title = "Hardware Sandbox Seal",
            value = "System Verified OK (API Level 36)"
        )

        // Protocol Selection Dialog
        if (showProtocolDialog) {
            AlertDialog(
                onDismissRequest = { showProtocolDialog = false },
                title = { Text("Select Network Suite") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val protocols = listOf("UDP-256", "TCP-443", "WireGuard Secure", "Shadowsocks AES")
                        protocols.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateProtocol(p)
                                        showProtocolDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(p, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                if (p == activeProtocol) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = BentoPrimary)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showProtocolDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PreferenceSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BentoBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BentoPrimary)
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BentoDark)
                    Text(text = subtitle, fontSize = 12.sp, color = BentoTextGray)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Edit", tint = BentoTextGray)
        }
    }
}
