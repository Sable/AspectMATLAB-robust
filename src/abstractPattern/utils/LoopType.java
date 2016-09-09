package abstractPattern.utils;

/** an abstract representation on MATLAB loops */
public enum LoopType {
    /** MATLAB for loop */                            For,
    /** MATLAB while loop */                          While,
    /** an abstract representation of [*] wildcard */ Any;

    /**
     * parse the loop type from string value
     * @param name string value
     * @return the parsed loop type
     * @throws IllegalArgumentException if the string is not a valid loop type in MATLAB
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public static LoopType fromString(String name) {
        if (name == null) throw new NullPointerException();
        switch (name) {
            case "for":     return For;
            case "while":   return While;
            case "*":       return Any;
            case "..":      return Any;
        }
        /* control flow should not reach here */
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        switch (this) {
            case For:   return "for";
            case While: return "while";
            case Any:   return "*";
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}
