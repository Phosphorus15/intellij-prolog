package tech.phosphorus.intellij.prolog.settings;

/**
 * Created by Phosphorus15 on 2020/3/8
 *
 * @author Phosphorus15
 */
public class PrologState {

	public String toolchain;

	public PrologState(String toolchain) {
		this.toolchain = toolchain;
	}

	public PrologState() {
		toolchain = "";
	}

}
