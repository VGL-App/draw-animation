package com.vgl.draw.animation.feature.projects

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/**
 * Drawn like `PrivacyOptionsButton` of :core:ads where the privacy options entry is required (plan T-056): tests have
 * no ads controller, so the real button draws nothing. [modifier] goes to the button, so the top bar's tag stays on it.
 */
@Composable
fun FakePrivacyButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text = stringResource(com.vgl.draw.animation.core.ads.R.string.ads_privacy_options))
    }
}
