package aspectMATLAB.abstractPattern.utils;

import ast.Name;

/** an abstract representation on MATLAB scopes */
public enum ScopeType {
    /** MATLAB function */                            Function,
    /** MATLAB script */                              Script,
    /** MATLAB class */                               Class,
    /** AspectMATLAB aspects */                       Aspect,
    /** MATLAB for loop and while loop */             Loop,
    /** an abstract representation of [*] wildcard */ Any;

    @Override
    public String toString() {
        switch (this) {
            case Function:  return "function";
            case Script:    return "script";
            case Aspect:    return "aspect";
            case Loop:      return "loop";
            case Any:       return "*";
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * parse scope type from AST identifier
     * @param name the scope identifier in scope patternExpand
     * @return {@code ScopeType} of such identifier
     * @throws IllegalArgumentException if such identifier is not a valid scope type
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public static ScopeType valueOf(Name name) {
        if (name == null) throw new NullPointerException();
        String nameString = name.getID();
        switch (nameString) {
            case "function":    return Function;
            case "script":      return Script;
            case "aspect":      return Aspect;
            case "loop":        return Loop;
            case "*":           return Any;
            case "":          return Any;
        }
        /* control flow should not reach here */
        throw new IllegalArgumentException();
    }
}
