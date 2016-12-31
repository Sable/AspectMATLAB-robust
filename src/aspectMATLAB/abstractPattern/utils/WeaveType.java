package aspectMATLAB.abstractPattern.utils;

import java.util.Optional;

public enum WeaveType {
    Before,
    After,
    Around;

    public static WeaveType fromString(String string) {
        switch (Optional.ofNullable(string).orElseThrow(NullPointerException::new)) {
            case "before":      return WeaveType.Before;
            case "after":       return WeaveType.After;
            case "around":      return WeaveType.Around;
            default:            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Before:        return "before";
            case After:         return "after";
            case Around:        return "around";
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}
