@file:Suppress("NOTHING_TO_INLINE")

package ru.morozovit.dncryptor101

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.morozovit.compat.ContentPaste
import ru.morozovit.compat.Encrypted
import ru.morozovit.compat.EncryptedOff
import ru.morozovit.compat.LocalSupportClipboardManager
import ru.morozovit.compat.StopCircle
import ru.morozovit.compat.WindowWidthSizeClass
import ru.morozovit.compat.clearFocusOnKeyboardDismiss
import ru.morozovit.compat.currentWindowAdaptiveInfo
import ru.morozovit.compat.currentWindowSize
import ru.morozovit.compat.left
import ru.morozovit.compat.link
import ru.morozovit.compat.plus
import ru.morozovit.compat.removeNavScrim
import ru.morozovit.compat.right
import ru.morozovit.dncryptor101.base.Solver
import ru.morozovit.dncryptor101.base.Solver.ALPHABET_RUSSIAN
import ru.morozovit.dncryptor101.ui.AppTheme
import ru.morozovit.dncryptor101.ui.NavigationItem
import ru.morozovit.dncryptor101.ui.SimpleNavigationBar
import ru.morozovit.dncryptor101.ui.SimpleNavigationRail
import ru.morozovit.dncryptor101.ui.Step
import kotlin.random.Random

val LocalSnackbar: ProvidableCompositionLocal<SnackbarHostState> = compositionLocalOf { throw NullPointerException() }

@Composable
inline fun TabBase(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    title: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit
) {

    val padding = if (
        currentWindowAdaptiveInfo()
            .windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    )
        (with(LocalDensity.current) { currentWindowSize().width.toDp().value } * 0.25).dp
    else
        0.dp

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(
                start = padding,
                end = padding
            ) + modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = horizontalAlignment
    ) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, bottom = 10.dp),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )
        }
        content()
    }
}

@Composable
//@Preview(
//    name = "Light Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    device = "id:pixel_8_pro",
//    showSystemUi = true,
//    showBackground = true
//)
//@Preview(
//    name = "Dark Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    device = "id:pixel_8_pro",
//    showSystemUi = true,
//    showBackground = true
//)
inline fun DecryptTab() {
    val clipboard = LocalSupportClipboardManager.current
    var clipboardHasText by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbar = LocalSnackbar.current
    coroutineScope.launch {
        clipboardHasText = clipboard.hasText()
    }
    clipboard.setTextListener {
        coroutineScope.launch {
            snackbar.showSnackbar("Copied!")
        }
    }

    var isStopFabVisible by remember { mutableStateOf(false) }

    var shouldStop by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .onFocusChanged {
                if (it.hasFocus) {
                    coroutineScope.launch {
                        clipboardHasText = clipboard.hasText()
                    }
                }
            },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isStopFabVisible,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { shouldStop = true },
                    icon = { Icon(Icons.Filled.StopCircle, "Stop") },
                    text = { Text(text = "Stop") },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        val scrollState = rememberScrollState()
        TabBase(
            scrollState = scrollState,
            title = "Decryptor"
        ) {
            var textToDecrypt by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = ""
                    )
                )
            }
            var shift by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = ""
                    )
                )
            }

            var variantsVisible by remember { mutableStateOf(false) }
            val decryptedVariants = remember { mutableStateMapOf<String, Boolean>() }

            var isTextInvalid by rememberSaveable { mutableStateOf(true) }

            val clearVariants = {
                variantsVisible = false
                coroutineScope.launch {
                    decryptedVariants.forEach { v ->
                        decryptedVariants[v.key] = false
                        delay(25)
                    }
                }
            }

            Step(
                number = 1,
                title = "Enter the text to decrypt",
                supportingText = "The program will try to decrypt this text",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                TextField(
                    value = textToDecrypt,
                    onValueChange = {
                        val txtChanged = it.text != textToDecrypt.text
                        textToDecrypt = it
                        if (txtChanged) {
                            isTextInvalid = it.text.isEmpty()
                            variantsVisible = false
                            clearVariants()
                        }
                    },
                    label = { Text("Text to decrypt") },
                    isError = isTextInvalid,
                    trailingIcon = {
                        if (clipboardHasText) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            if (clipboard.hasText()) {
                                                val clip: CharSequence = clipboard.getText()!!
                                                textToDecrypt = TextFieldValue(
                                                    text = "${textToDecrypt.text}$clip",
                                                    selection = TextRange(
                                                        textToDecrypt.text.length +
                                                                clip.length
                                                    )
                                                )
                                                isTextInvalid = textToDecrypt.text.isEmpty()
                                                clearVariants()
                                            }
                                        } catch (_: Exception) {
                                            coroutineScope.launch {
                                                snackbar.showSnackbar("Failed to paste text")
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.ContentPaste,
                                    contentDescription = "Paste"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearFocusOnKeyboardDismiss()
                )
            }
            Step(
                number = 2,
                title = "Enter a shift",
                supportingText = "Type a shift if you know it (optional)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                isOptional = true
            ) {
                TextField(
                    value = shift,
                    onValueChange = {
                        val txtChanged = it.text != shift.text
                        shift = it
                        if (txtChanged) {
                            clearVariants()
                        }
                    },
                    label = { Text("Shift") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearFocusOnKeyboardDismiss(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Step(
                number = 3,
                title = "Decrypt the text",
                supportingText = "Click to get all the possible variants",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = {
                        if (isTextInvalid) {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                            return@Button
                        }
                        val variants = if (shift.text.toIntOrNull() != null)
                            listOf(
                                Solver.decrypt(
                                    textToDecrypt.text,
                                    shift.text.toIntOrNull()!!
                                )
                            )
                        else
                            Solver.decrypt(textToDecrypt.text)
                        decryptedVariants.clear()
                        decryptedVariants.putAll(variants.associateWith { false })
                        variantsVisible = true
                        isStopFabVisible = true
                        coroutineScope.launch {
                            decryptedVariants.forEach {
                                if (shouldStop) {
                                    shouldStop = false
                                    isStopFabVisible = false
                                    return@launch
                                }
                                decryptedVariants[it.key] = true
                                delay(125)
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.maxValue)
                                }
                            }
                            isStopFabVisible = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Decrypt")
                }
            }

            AnimatedVisibility(visible = variantsVisible || LocalInspectionMode.current) {
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                    HorizontalDivider(
                        Modifier
                            .padding()
                            .padding(bottom = 10.dp)
                    )
                    Text(
                        text = "Possible variants:",
                        fontSize = 20.sp
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            ) {
                if (LocalInspectionMode.current) {
                    decryptedVariants["test1"] = false
                    decryptedVariants["test2"] = false
                    decryptedVariants["test3"] = false
                    decryptedVariants["test4"] = false
                    decryptedVariants["test5"] = false
                    decryptedVariants["test6"] = false
                    decryptedVariants["test7"] = false
                    decryptedVariants["test8"] = false
                }

                decryptedVariants.forEach {
                    AnimatedVisibility(visible = it.value) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        clipboard.setText(it.key)
                                    }
                                }
                        ) {
                            Text(
                                text = it.key,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
inline fun EncryptTab() {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalSupportClipboardManager.current
    val snackbar = LocalSnackbar.current
    var clipboardHasText by remember { mutableStateOf(false) }
    coroutineScope.launch {
        clipboardHasText = clipboard.hasText()
    }

    clipboard.setTextListener {
        coroutineScope.launch {
            snackbar.showSnackbar("Copied!")
        }
    }

    TabBase(
        scrollState = scrollState,
        title = "Encryptor",
        modifier = Modifier.onFocusChanged {
            if (it.hasFocus) {
                coroutineScope.launch {
                    clipboardHasText = clipboard.hasText()
                }
            }
        }
    ) {
        var textToEncrypt by remember {
            mutableStateOf(
                TextFieldValue(
                    text = ""
                )
            )
        }
        var shift by remember {
            mutableStateOf(
                TextFieldValue(
                    text = ""
                )
            )
        }

        var encryptedTextVisible by remember { mutableStateOf(false) }
        var encryptedText by remember { mutableStateOf<String?>(null) }

        var isTextInvalid by rememberSaveable { mutableStateOf(true) }

        var randomShift by remember { mutableIntStateOf(0) }

        Step(
            number = 1,
            title = "Enter the text to encrypt",
            supportingText = "This text will be encrypted with the specified shift",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            TextField(
                value = textToEncrypt,
                onValueChange = {
                    val txtChanged = it.text != textToEncrypt.text
                    textToEncrypt = it
                    if (txtChanged) {
                        isTextInvalid = textToEncrypt.text.isEmpty()
                        encryptedTextVisible = false
                    }
                },
                label = { Text("Text to encrypt") },
                isError = isTextInvalid,
                trailingIcon = {
                    if (clipboardHasText) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        if (clipboard.hasText()) {
                                            val clip = clipboard.getText()!!
                                            textToEncrypt = TextFieldValue(
                                                text = "${textToEncrypt.text}$clip",
                                                selection = TextRange(
                                                    textToEncrypt.text.length +
                                                            clip.length
                                                )
                                            )
                                            isTextInvalid = textToEncrypt.text.isEmpty()
                                        }
                                    } catch (_: Exception) {
                                        coroutineScope.launch {
                                            snackbar.showSnackbar("Failed to paste text")
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Outlined.ContentPaste,
                                contentDescription = "Paste"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss()
            )
        }
        Step(
            number = 2,
            title = "Enter a shift",
            supportingText = "Type a shift with which you want to encrypt your text (if you " +
                    "don't specify it, a random shift will be generated)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            isOptional = true
        ) {
            TextField(
                value = shift,
                onValueChange = {
                    val txtChanged = it.text != shift.text
                    shift = it
                    if (txtChanged) {
                        encryptedTextVisible = false
                    }
                },
                label = { Text("Shift") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clearFocusOnKeyboardDismiss(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        Step(
            number = 3,
            title = "Encrypt the text",
            supportingText = "Click to see your encrypted text",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                onClick = {
                    encryptedTextVisible = false
                    if (isTextInvalid) {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(0)
                        }
                        return@Button
                    }
                    coroutineScope.launch {
                        delay(250)
                        while (true) {
                            val text = Solver.encrypt(
                                textToEncrypt.text,
                                shift.text.toIntOrNull() ?: let {
                                    randomShift = Random.nextInt(
                                        -ALPHABET_RUSSIAN.length, ALPHABET_RUSSIAN.length
                                    )
                                    randomShift
                                }
                            )
                            if (text != textToEncrypt.text) {
                                encryptedText = text
                                break
                            }
                        }
                        encryptedTextVisible = true
                        delay(250)
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Encrypt")
            }
        }

        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            AnimatedVisibility(visible = encryptedTextVisible || LocalInspectionMode.current) {
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    HorizontalDivider(
                        Modifier
                            .padding()
                            .padding(bottom = 10.dp)
                    )
                    if (shift.text.toIntOrNull() == null) {
                        Text(
                            text = "Shift: $randomShift",
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "Your encrypted text:",
                        fontSize = 20.sp
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    clipboard.setText(encryptedText ?: "")
                                }
                            }
                    ) {
                        Text(
                            text = encryptedText ?: "",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
inline fun SettingsTab() {
    TabBase(title = "Settings") {
        TODO()
    }
}
@Composable
inline fun AboutTab() {
    TabBase(title = "About") {
        Text(
            text = "Dncryptor101",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 15.dp)
        )

        val version = /* runCatching {
            val context = LocalContext.current
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName
        }.getOrNull() ?: */ "[unknown]"

        Text(
            text = "Version $version",
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(text = "A program for decrypting & encrypting text")
        Button(
            onClick = {
                throw DebugException("Exception for debugging :)")
            }
        ) {
            Text(text = "Throw an exception (developers only)")
        }
    }
}

@Composable
//@Preview(
//    name = "Light Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    device = "id:pixel_8_pro",
//    showSystemUi = true,
//    showBackground = true
//)
//@Preview(
//    name = "Dark Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    device = "id:pixel_8_pro",
//    showSystemUi = true,
//    showBackground = true
//)
fun MainScreen() {
    AppTheme(consumeWindowInsets = true) {
        removeNavScrim()
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (pager, nav) = createRefs()

            var selectedItem by remember { mutableIntStateOf(0) }
            val pagerState = rememberPagerState { 4 }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect {
                    if (pagerState.currentPage == pagerState.targetPage) {
                        if (selectedItem != it) selectedItem = it
                    }
                }
            }

            val pages: @Composable PagerScope.(Int) -> Unit = { page ->
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }) {
                    CompositionLocalProvider(
                        LocalSnackbar provides snackbarHostState
                    ) {
                        when (page) {
                            0 -> EncryptTab()
                            1 -> DecryptTab()
                            // 2 -> SettingsTab()
                            /* 3 */ 2 -> AboutTab()
                        }
                    }
                }
            }

            val navItems = arrayOf(
                NavigationItem(
                    name = "Encrypt",
                    selectedIcon = Icons.Filled.Encrypted,
                    unselectedIcon = Icons.Outlined.Encrypted,
                    onClick = {
                        selectedItem = 0
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                ),
                NavigationItem(
                    name = "Decrypt",
                    selectedIcon = Icons.Filled.EncryptedOff,
                    unselectedIcon = Icons.Outlined.EncryptedOff,
                    onClick = {
                        selectedItem = 1
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                ),
                /* NavigationItem(
                    name = "Settings",
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    onClick = {
                        selectedItem = 2
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                ), */
                NavigationItem(
                    name = "About",
                    selectedIcon = Icons.Filled.Info,
                    unselectedIcon = Icons.Outlined.Info,
                    onClick = {
                        selectedItem = /* 3 */ 2
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(/* 3 */ 2)
                        }
                    }
                )
            )

            if (currentWindowAdaptiveInfo()
                    .windowSizeClass.let {
                        it.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
                                it.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
                    }
            ) {
                VerticalPager(
                    state = pagerState,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .constrainAs(pager) {
                            top link parent.top
                            bottom link parent.bottom
                            left link nav.right
                            right link parent.right
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        }
                        .windowInsetsPadding(
                            WindowInsets(
                                0,
                                topInset,
                                0,
                                bottomInset
                            )
                        ),
                    pageContent = pages,
                    userScrollEnabled = false
                )
                SimpleNavigationRail(
                    selectedItem = selectedItem,
                    modifier = Modifier
                        .constrainAs(nav) {
                            bottom link parent.bottom
                            top link parent.top
                            left link parent.left
                        },
                    items = navItems
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .constrainAs(pager) {
                            top link parent.top
                            bottom link nav.top
                            left link parent.left
                            right link parent.right
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .windowInsetsPadding(
                            WindowInsets(
                                0,
                                topInset,
                                0,
                                0
                            )
                        ),
                    pageContent = pages
                )
                SimpleNavigationBar(
                    selectedItem = selectedItem,
                    modifier = Modifier
                        .constrainAs(nav) {
                            bottom link parent.bottom
                        },
                    items = navItems
                )
            }
        }
    }
}