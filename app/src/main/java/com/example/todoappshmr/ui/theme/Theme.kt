import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.todoappshmr.ui.theme.*
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val lightColors = lightColorScheme(
        primary = LightPrimary,
        secondary = LightSecondary,
        background = LightBackgroundPrimary,
        surface = LightBackgroundSecondary,
        onPrimary = LightWhite,
        onSecondary = LightPrimary,
        onBackground = LightPrimary,
        onSurface = LightPrimary,

        error = LightRed,
        tertiary = LightGreen,
        primaryContainer = LightBlue,
        secondaryContainer = LightGray,
        tertiaryContainer = LightLabelPrimary
    )

    val darkColors = darkColorScheme(
        primary = DarkPrimary,
        secondary = DarkSecondary,
        background = DarkBackgroundPrimary,
        surface = DarkBackgroundSecondary,
        onPrimary = DarkBackgroundPrimary,
        onSecondary = DarkWhite,
        onBackground = DarkWhite,
        onSurface = DarkWhite,
        tertiaryContainer = DarkLabelPrimary,


        error = DarkRed,
        tertiary = DarkGreen,
        primaryContainer = DarkBlue,
        secondaryContainer = DarkGray
    )

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
