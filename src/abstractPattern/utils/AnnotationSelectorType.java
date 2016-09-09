package abstractPattern.utils;

import ast.Name;

/** an abstract representation on annotation pattern selectors */
public enum AnnotationSelectorType {
    /** selector to match a variable */                Var,
    /** selector to match a string literal */          Str,
    /** selector to match a numeric literal */         Num,
    /** an abstract representation of star wildcard */ StarWildcard,
    /** an abstract representation of dots wildcard */ DotsWildcard;

    /**
     * parse annotation selector from {@link Name} AST node
     * @param name {@link Name} AST node
     * @return parsed annotation selector type
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws IllegalArgumentException if {@code name} is not a valid annotation selector
     */
    public static AnnotationSelectorType valueOf(Name name) {
        if (name == null) throw new NullPointerException();
        String nameString = name.getID();
        switch (nameString) {
            case "var": return Var;
            case "str": return Str;
            case "num": return Num;
            case "*":   return StarWildcard;
            case "..":  return DotsWildcard;
        }
        /* control flow should not reach here */
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        switch (this) {
            case Var:           return "var";
            case Str:           return "str";
            case Num:           return "num";
            case StarWildcard:  return "*";
            case DotsWildcard:  return "..";
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}
