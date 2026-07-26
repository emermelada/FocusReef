package com.emermeladas.focusreef.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.components.FocusReefDialog
import com.emermeladas.focusreef.ui.components.ReefContentCard
import com.emermeladas.focusreef.ui.components.ReefPrimaryButton
import com.emermeladas.focusreef.ui.components.ReefScreenScaffold
import com.emermeladas.focusreef.ui.components.ReefSecondaryButton
import com.emermeladas.focusreef.ui.components.ReefTextAction
import com.emermeladas.focusreef.ui.components.ReefToast
import com.emermeladas.focusreef.ui.theme.ReefSpacing
import com.emermeladas.focusreef.ui.theme.ThemeMode

/**
 * Settings — reached from the gear in any screen's top bar, not from a fourth
 * tab: the bottom bar is for the three places the player *lives*, and one of
 * them would have to give up its slot to something visited twice a year.
 *
 * Four groups, in the order they matter: the desk connection (the only
 * setting that can stop the app working), appearance, about, and the single
 * destructive action, kept visually last and behind a confirmation.
 *
 * @param outerPadding insets from the app scaffold, spent as content padding.
 */
@Composable
fun SettingsScreen(
    outerPadding: PaddingValues,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes by viewModel.userMessageRes.collectAsStateWithLifecycle()
    var confirmingReset by remember { mutableStateOf(false) }

    ReefScreenScaffold(
        title = stringResource(R.string.settings_title),
        outerPadding = outerPadding,
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(ReefSpacing.md),
            ) {
                DeskSection(uiState = uiState, viewModel = viewModel)
                AppearanceSection(uiState = uiState, viewModel = viewModel)
                AboutSection(uiState = uiState)
                DangerSection(onReset = { confirmingReset = true })
            }

            ReefToast(
                messageRes = messageRes,
                onShown = viewModel::onMessageShown,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = outerPadding.calculateBottomPadding()),
            )
        }
    }

    if (confirmingReset) {
        FocusReefDialog(
            title = stringResource(R.string.settings_reset_confirm_title),
            onDismiss = { confirmingReset = false },
            buttons = {
                // The accent button is the *safe* option, on purpose. The
                // destructive one stays a quiet text action: nothing about
                // this dialog should invite a reflexive tap on "Empty it".
                ReefTextAction(
                    text = stringResource(R.string.settings_reset_action),
                    onClick = {
                        viewModel.resetAquarium()
                        confirmingReset = false
                    },
                )
                ReefPrimaryButton(
                    text = stringResource(R.string.settings_reset_cancel),
                    onClick = { confirmingReset = false },
                )
            },
        ) {
            Text(
                text = stringResource(R.string.settings_reset_confirm_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** The NAS address: the one setting that can stop the app working. */
@Composable
private fun DeskSection(uiState: SettingsUiState, viewModel: SettingsViewModel) {
    SettingsSection(titleRes = R.string.settings_section_desk) {
        OutlinedTextField(
            value = uiState.nasUrlDraft,
            onValueChange = viewModel::onNasUrlChanged,
            label = { Text(stringResource(R.string.settings_nas_label)) },
            placeholder = { Text(stringResource(R.string.settings_nas_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.settings_nas_explainer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(ReefSpacing.sm)) {
            ReefSecondaryButton(
                text = stringResource(R.string.settings_nas_save),
                onClick = viewModel::saveNasUrl,
                enabled = uiState.hasUnsavedUrl,
            )
            ReefSecondaryButton(
                text = stringResource(R.string.settings_nas_test),
                onClick = viewModel::testConnection,
                enabled = !uiState.isTestingConnection,
            )
            if (uiState.isTestingConnection) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(20.dp),
                )
            }
        }
        uiState.connectionReport?.let { report ->
            Text(
                text = when (report) {
                    is ConnectionReport.Reached -> pluralStringResource(
                        R.plurals.settings_nas_test_ok,
                        report.blockCount,
                        report.blockCount,
                    )

                    is ConnectionReport.Failed -> stringResource(report.messageRes)
                },
                style = MaterialTheme.typography.bodySmall,
                color = when (report) {
                    is ConnectionReport.Reached -> MaterialTheme.colorScheme.primary
                    is ConnectionReport.Failed -> MaterialTheme.colorScheme.error
                },
                // The verdict lands seconds after the button is pressed, with
                // nothing moving focus to it.
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

/** Theme override and the still-reef switch. */
@Composable
private fun AppearanceSection(uiState: SettingsUiState, viewModel: SettingsViewModel) {
    SettingsSection(titleRes = R.string.settings_section_appearance) {
        Text(
            text = stringResource(R.string.settings_theme_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.themeMode == mode,
                    onClick = { viewModel.setThemeMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size,
                    ),
                ) {
                    Text(stringResource(mode.labelRes))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReefSpacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_reduce_motion_label),
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = uiState.reduceMotion,
                onCheckedChange = viewModel::setReduceMotion,
            )
        }
        Text(
            text = stringResource(R.string.settings_reduce_motion_explainer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Version, so a bug report can say which build it came from. */
@Composable
private fun AboutSection(uiState: SettingsUiState) {
    SettingsSection(titleRes = R.string.settings_section_about) {
        Text(
            text = stringResource(R.string.settings_version, uiState.appVersion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The one irreversible action, kept last and behind a confirmation. */
@Composable
private fun DangerSection(onReset: () -> Unit) {
    SettingsSection(titleRes = R.string.settings_section_danger) {
        Text(
            text = stringResource(R.string.settings_reset_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.settings_reset_explainer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReefSecondaryButton(
            text = stringResource(R.string.settings_reset_action),
            onClick = onReset,
        )
    }
}

/**
 * A titled group of settings in a card. The title is a real heading so
 * TalkBack can jump between groups instead of swiping every control.
 */
@Composable
private fun SettingsSection(
    titleRes: Int,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ReefSpacing.sm)) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = ReefSpacing.xs)
                .semantics { heading() },
        )
        ReefContentCard(
            padding = ReefSpacing.md,
            spacing = ReefSpacing.sm,
            modifier = Modifier.fillMaxWidth(),
            contentModifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}
