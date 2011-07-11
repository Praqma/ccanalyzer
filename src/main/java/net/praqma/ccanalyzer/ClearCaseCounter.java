package net.praqma.ccanalyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearCaseCounter {
    
    private String modifier;
    private String function;
    
    private static final Pattern rx_ = Pattern.compile( "^\\\\(?i:clearcase)\\((.*?)\\)\\\\(.*?)$" );
    
    public static ClearCaseCounter fromString( String c ) {
        Matcher m = rx_.matcher( c );
        
        if( m.find() ) {
            return new ClearCaseCounter( m.group(2), m.group(1) );
        } else {
            return null;
        }
    }
    
    public static void main( String[] args) {
        ClearCaseCounter ccc = ClearCaseCounter.fromString( "\\clearcase(praqma)\\number" );
        System.out.println(ccc);
    }
    
    public ClearCaseCounter( String function, String modifier ) {
        this.function = function;
        this.modifier = modifier;
    }

    public String getModifier() {
        return modifier;
    }

    public String getFunction() {
        return function;
    }
    
    public String toString() {
        return function + "/" + modifier;
    }
}
