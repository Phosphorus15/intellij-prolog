package tech.phosphorus.intellij.prolog.settings;

/**
 * Created by Phosphorus15 on 2020/3/8
 *
 * @author Phosphorus15
 */
public class PrologState {

	public String toolchain;

	public String stdLibrary;

	public PrologState(String toolchain, String stdLibrary) {
		this.toolchain = toolchain;
		this.stdLibrary = stdLibrary;
	}

	public PrologState(String toolchain) {
		this.toolchain = toolchain;
	}

	public PrologState() {
		toolchain = "";
	}

}
