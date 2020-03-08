package tech.phosphorus.intellij.prolog.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;

/**
 * Created by Phosphorus15 on 2020/3/8
 *
 * @author Phosphorus15
 */
public class PrologLanguageConfigurableGUI {

	public TextFieldWithBrowseButton toolchainLocation;
	private JPanel rootPanel;

	public JPanel getRootPanel() {
		return rootPanel;
	}

}
