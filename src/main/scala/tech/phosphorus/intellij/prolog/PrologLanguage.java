package tech.phosphorus.intellij.prolog;

import com.intellij.lang.Language;

public class PrologLanguage extends Language {

    public static final PrologLanguage INSTANCE = new PrologLanguage();

    protected PrologLanguage() {
        super("Prolog");
    }
}
