package tech.phosphorus.intellij.prolog;

import com.intellij.lang.Language;

public class PrologLanguage extends Language {

    /// have to use java to do this =-=
    public static final PrologLanguage INSTANCE = new PrologLanguage();

    protected PrologLanguage() {
        super("IJProlog");
    }

}
