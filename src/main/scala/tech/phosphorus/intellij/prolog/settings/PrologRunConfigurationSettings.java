package tech.phosphorus.intellij.prolog.settings;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import tech.phosphorus.intellij.prolog.project.PrologRunConfiguration;

import javax.swing.*;

public abstract class PrologRunConfigurationSettings extends SettingsEditor<PrologRunConfiguration> {
    protected TextFieldWithBrowseButton targetRunFile;
    protected JPanel rootPanel;
    public JTextField extraArgs;
}
