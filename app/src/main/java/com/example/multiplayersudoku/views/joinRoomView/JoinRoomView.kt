package com.example.multiplayersudoku.views.joinRoomView

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.multiplayersudoku.R
import com.example.multiplayersudoku.components.ExpressiveWavyDivider
import com.example.multiplayersudoku.ui.theme.FredokaFamily
import com.example.multiplayersudoku.views.lobbyView.LobbyArgs
import kotlinx.coroutines.launch

class RoomCodeInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().isDigitsOnly()) {
            revertAllChanges()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomView(
    onBack: () -> Unit,
    viewModel: JoinRoomViewModel = hiltViewModel(),
    onNavigateToLobby: (LobbyArgs) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val tooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()

    // Required permissions for Bluetooth scanning, advertising, and connection based on API level
    val hostPermissions = remember {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    var hasPermissions by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, context, hostPermissions) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            // After the user has navigated to the app again, check for permissions
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val currentGranted = hostPermissions.all { permission ->
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                }
                hasPermissions = currentGranted
                viewModel.setHasPermissions(currentGranted)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 2. Register the Permission Launcher Contract
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // Check if all requested permissions were approved by the user
        val allGranted = permissionsMap.values.all { it }
        hasPermissions = allGranted
        viewModel.setHasPermissions(allGranted)

        if (allGranted) {
            viewModel.startScanning()
        } else {
            val activity = context as? android.app.Activity
            val shouldShowRationale = hostPermissions.any { permission ->
                activity?.let {
                    androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                } ?: false
            }

            if (!shouldShowRationale) {
                Toast.makeText(
                    context,
                    "Please enable nearby devices permissions in App Settings to scan for games.",
                    Toast.LENGTH_LONG
                ).show()
                try {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback if settings cannot be opened
                }
            } else {
                Toast.makeText(
                    context,
                    "Bluetooth permissions are required to find games.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init(onNavigateToLobby)
    }

    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            viewModel.startScanning()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopScanning()
        }
    }

    fun hideNearbyScanning() {
        scope.launch {
            viewModel.setNearbyScanning(false)
            tooltipState.show()
        }
    }

    fun showNearbyScanning() {
        if (hasPermissions) {
            viewModel.setNearbyScanning(true)
            viewModel.startScanning()
        } else {
            permissionLauncher.launch(hostPermissions)
        }
    }

    fun stopScanning() {
        scope.launch {
            viewModel.stopScanning()
            tooltipState.show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Play versus",
                        style = TextStyle(
                            fontFamily = FredokaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
                navigationIcon = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { Text("Menu") } },
                        state = rememberTooltipState(),
                    ) {
                        FilledTonalIconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (!viewModel.showNearbyScanning) {
                        TooltipBox(
                            positionProvider = TooltipDefaults
                                .rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                            tooltip = {
                                PlainTooltip {
                                    Text("You can enable nearby scanning here")
                                }
                            },
                            state = tooltipState
                        ) {
                            IconButton(
                                onClick = { showNearbyScanning() },
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chess_queen),
                                    contentDescription = "Nearby",
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection)
                )
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(Modifier.weight(1f))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.roomCodeError.isNotEmpty(),
                        supportingText = {
                            if (viewModel.roomCodeError.isNotEmpty()) {
                                Text(
                                    text = viewModel.roomCodeError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        label = { Text("Room code") },
                        state = viewModel.roomCodeState,
                        inputTransformation = InputTransformation.maxLength(4)
                            .then(RoomCodeInputTransformation()),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = viewModel.roomCodeState.text.isNotEmpty(),
                                enter = scaleIn() + fadeIn(),
                                exit = scaleOut() + fadeOut()
                            ) {
                                FilledIconButton(
                                    enabled = viewModel.roomCodeState.text.length == 4,
                                    onClick = { viewModel.attemptJoinRoom() },
                                    shapes = IconButtonDefaults.shapes(),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.Send,
                                        contentDescription = ""
                                    )
                                }

                            }
                        },
                        onKeyboardAction = KeyboardActionHandler {
                            viewModel.attemptJoinRoom()
                        }
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpressiveWavyDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            text = "OR",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        ExpressiveWavyDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Button(
                        onClick = { viewModel.createRoom() },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = ButtonDefaults.MediumContainerHeight),
                        contentPadding = ButtonDefaults.MediumContentPadding
                    ) {
                        Text(
                            "Create room",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(Modifier.weight(1f))
                }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.animation.AnimatedVisibility(visible = viewModel.showNearbyScanning) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
                                    .padding(horizontal = if (viewModel.isScanning) 0.dp else 10.dp)
                                    .fillMaxWidth(),
                                tonalElevation = 3.dp
                            ) {
                                AnimatedContent(
                                    targetState = viewModel.isScanning,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                                            animationSpec = tween(220)
                                        )
                                    },
                                    label = "ComponentSwitch"
                                ) { showFirst ->
                                    if (showFirst) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentPadding = PaddingValues(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            item {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(10.dp)
                                                ) {
                                                    ContainedLoadingIndicator(
                                                        modifier = Modifier
                                                            .height(30.dp)
                                                            .width(30.dp)
                                                    )
                                                    Text(
                                                        "Searching for nearby games...",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(Modifier.weight(1f))
                                                    TextButton(
                                                        shapes = ButtonDefaults.shapes(),
                                                        onClick = { stopScanning() }
                                                    ) {
                                                        Text(
                                                            "Stop",
                                                            style = MaterialTheme.typography.bodyMediumEmphasized,
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    }
                                                }
                                            }
                                            itemsIndexed(
                                                items = viewModel.nearbyGames,
                                                key = { _, game -> game.roomCode }
                                            ) { index, game ->
                                                key(game.roomCode) {
                                                    val isLast = index == viewModel.nearbyGames.lastIndex
                                                    val isFirst = index == 0

                                                    val bottomRadius by animateDpAsState(
                                                        targetValue = if (isLast) 16.dp else 4.dp,
                                                        animationSpec = tween(durationMillis = 300),
                                                        label = "BottomRadiusAnimation"
                                                    )

                                                    val topRadius by animateDpAsState(
                                                        targetValue = if (isFirst) 16.dp else 4.dp,
                                                        animationSpec = tween(durationMillis = 300),
                                                        label = "TopRadiusAnimation"
                                                    )

                                                    var isVisible by remember { mutableStateOf(false) }
                                                    LaunchedEffect(Unit) { isVisible = true }

                                                    androidx.compose.animation.AnimatedVisibility(visible = isVisible) {
                                                        Surface(
                                                            modifier = Modifier
                                                                .fillMaxWidth(),
                                                            shape = RoundedCornerShape(
                                                                topStart = topRadius,
                                                                topEnd = topRadius,
                                                                bottomStart = bottomRadius,
                                                                bottomEnd = bottomRadius
                                                            ),
                                                            tonalElevation = (-3).dp,
                                                            onClick = { viewModel.joinNearbyGame(game) },
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(
                                                                    horizontal = 16.dp,
                                                                    vertical = 12.dp
                                                                ),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Column {
                                                                    Text(
                                                                        text = game.hostUsername,
                                                                        style = MaterialTheme.typography.bodyLargeEmphasized
                                                                    )
                                                                    Text(
                                                                        text = "Room code: ${game.roomCode}",
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                                            alpha = 0.8f
                                                                        )
                                                                    )
                                                                }
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Default.Send,
                                                                    contentDescription = "Join game",
                                                                    tint = MaterialTheme.colorScheme.primary
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            item {
                                                Spacer(Modifier.height(ButtonDefaults.MediumContainerHeight))
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.primaryContainer,
                                                            shape = MaterialShapes.Cookie12Sided.toShape(),
                                                        )
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_chess_queen),
                                                        contentDescription = "Nearby",
                                                        modifier = Modifier.padding(4.dp),
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                                Text(
                                                    "Find nearby games",
                                                    style = MaterialTheme.typography.titleLargeEmphasized
                                                )
                                            }
                                            Text(
                                                "Allow joining nearby games using bluetooth",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.67f)
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { hideNearbyScanning() },
                                                    shapes = ButtonDefaults.shapes(),
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    contentPadding = ButtonDefaults.SmallContentPadding
                                                ) {
                                                    Text(
                                                        "Hide",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        if (hasPermissions) {
                                                            viewModel.startScanning()
                                                        } else {
                                                            permissionLauncher.launch(hostPermissions)
                                                        }
                                                    },
                                                    shapes = ButtonDefaults.shapes(),
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    contentPadding = ButtonDefaults.SmallContentPadding
                                                ) {
                                                    Text(
                                                        "Find",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonDefaults.MediumContainerHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    }
}