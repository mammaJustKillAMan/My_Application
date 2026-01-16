package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    //background/surfaces
    background = Darkest,
    surface = DarkColdOpacity65,
    surfaceVariant = DarkCold,
    outline = BaseCold,

    //primary
    primary = BaseStrong,
    onPrimary = Darkest,
    primaryContainer = BaseStrongOpacity80,
    onPrimaryContainer = Darkest,

    //secondary
    secondary = PopOrange,
    onSecondary = Darkest,
    secondaryContainer = PopOrangeOpacity70,
    onSecondaryContainer = Darkest,

    //tertiary
    tertiary = PopGreen,
    onTertiary = Darkest,
    tertiaryContainer = PopGreenOpacity70,
    onTertiaryContainer = Darkest,

    //error
    error = PopRed,
    onError = Darkest,
    errorContainer = PopRedOpacity70,
    onErrorContainer = Darkest,
)

private val LightColorScheme = lightColorScheme(
    //background/surfaces
    background = BaseLight,           // very readable
    surface = BaseStrong,             // warm surface
    surfaceVariant = BaseStrongOpacity80,
    outline = DarkCold,               // dark blue/teal edge

    //primary
    primary = PopBlue,
    onPrimary = Color.White,
    primaryContainer = BaseCold,
    onPrimaryContainer = Darkest,

    //secondary
    secondary = PopGreen,
    onSecondary = Color.White,
    secondaryContainer = PopGreenOpacity70,
    onSecondaryContainer = Darkest,

    //tertiary
    tertiary = PopYellow,
    onTertiary = Darkest,
    tertiaryContainer = PopYellowOpacity70,
    onTertiaryContainer = Darkest,

    //error
    error = PopRed,
    onError = Color.White,
    errorContainer = DarkWormOpacity65,
    onErrorContainer = Color.White,
)

private val HighContrastLightScheme = lightColorScheme(
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.LightGray,
    outline = Color.Black,

    primary = ContrastBlue,
    onPrimary = Color.White,
    primaryContainer = ContrastBlueOpacity70,
    onPrimaryContainer = Color.White,

    secondary = ContrastGreen,
    onSecondary = Color.White,
    secondaryContainer = ContrastGreenOpacity70,
    onSecondaryContainer = Color.White,

    tertiary = ContrastYellow,
    onTertiary = Color.Black,
    tertiaryContainer = ContrastYellowOpacity70,
    onTertiaryContainer = Color.Black,

    error = ContrastRed,
    onError = Color.White,
    errorContainer = ContrastRedOpacity70,
    onErrorContainer = Color.White,
)

private val HighContrastDarkScheme = darkColorScheme(
    background = Darkest,
    surface = Darkest,
    surfaceVariant = Color.DarkGray,
    outline = Color.White,

    primary = ContrastBlue,
    onPrimary = Color.White,
    primaryContainer = ContrastBlueOpacity70,
    onPrimaryContainer = Color.White,

    secondary = ContrastGreen,
    onSecondary = Color.White,
    secondaryContainer = ContrastGreenOpacity70,
    onSecondaryContainer = Color.White,

    tertiary = ContrastYellow,
    onTertiary = Color.Black,
    tertiaryContainer = ContrastYellowOpacity70,
    onTertiaryContainer = Color.Black,

    error = ContrastRed,
    onError = Color.White,
    errorContainer = ContrastRedOpacity70,
    onErrorContainer = Color.White,
)

private val ColorBlindLightScheme = lightColorScheme(
    background = BaseLight,
    surface = BaseStrong,
    surfaceVariant = BaseStrongOpacity80,
    outline = Darkest,

    primary = ContrastBlue,
    onPrimary = Color.White,
    primaryContainer = ContrastBlueOpacity70,
    onPrimaryContainer = Color.White,

    secondary = ContrastOrange,
    onSecondary = Color.White,
    secondaryContainer = ContrastOrangeOpacity70,
    onSecondaryContainer = Color.White,

    tertiary = DarkCold,
    onTertiary = Color.White,
    tertiaryContainer = DarkColdOpacity65,
    onTertiaryContainer = Color.White,

    error = ContrastRed,
    onError = Color.White,
    errorContainer = ContrastRedOpacity70,
    onErrorContainer = Color.White,
)

private val ColorBlindDarkScheme = darkColorScheme(
    background = Darkest,
    surface = DarkCold,
    surfaceVariant = DarkColdOpacity65,
    outline = Color.White,

    primary = ContrastBlue,
    onPrimary = Color.White,
    primaryContainer = ContrastBlueOpacity70,
    onPrimaryContainer = Color.White,

    secondary = ContrastOrange,
    onSecondary = Color.Black,
    secondaryContainer = ContrastOrangeOpacity70,
    onSecondaryContainer = Color.Black,

    tertiary = BaseStrong,
    onTertiary = Darkest,
    tertiaryContainer = BaseStrongOpacity80,
    onTertiaryContainer = Darkest,

    error = ContrastRed,
    onError = Color.Black,
    errorContainer = ContrastRedOpacity70,
    onErrorContainer = Color.Black,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, //disabled dynamic colour (users own background)
    highContrast: Boolean = false,
    colorBlindSafe: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkScheme
        highContrast && !darkTheme -> HighContrastLightScheme

        colorBlindSafe && darkTheme -> ColorBlindDarkScheme
        colorBlindSafe && !darkTheme -> ColorBlindLightScheme

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalUsePatterns provides colorBlindSafe //patterns only when in colorBlindSafe
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}