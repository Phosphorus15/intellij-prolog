package tech.phosphorus.intellij.prolog.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;

import javax.swing.*;

/**
 * Created by Phosphorus15 on 2020/3/8
 *
 * @author Phosphorus15
 */
public class PrologLanguageConfigurableGUI {

	public TextFieldWithBrowseButton toolchainLocation;
	private JPanel rootPanel;
	public JLabel toolchainStatus;
	public TextFieldWithBrowseButton stdlibLocation;

	public PrologLanguageConfigurableGUI () {
		toolchainStatus.setForeground(JBColor.RED);
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

}
