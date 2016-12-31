package aspectMATLAB.abstractPattern.utils;

/** an abstract representation on MATLAB operators
 * <a>http://www.mathworks.com/help/matlab/matlab_prog/array-vs-matrix-operations.html</a>
 */
public enum OperatorType {
    /** MATLAB plus operator (+)*/                  Plus,
    /** MATLAB minus operator (-)*/                 Minus,
    /** MATLAB matrix multiplication (*) */         mTimes,
    /** MATLAB element-wise multiplication (.*) */  Time,
    /** MATLAB matrix right division (/) */         mrDivide,
    /** MATLAB right array division (./) */         rDivide,
    /** MATLAB matrix left division (\) */          mlDivide,
    /** MATLAB left array division (.\) */          lDivide,
    /** MATLAB matrix power (^) */                  mPower,
    /** MATLAB element-wise power (.^) */           Power,
    /** MATLAB array transpose (.') */              Transpose,
    /** MATLAB complex conjugate transpose (') */   mTranspose;

    /**
     * parse {@link OperatorType} from {@link String}
     * @param operatorString {@link String} to parse
     * @return the corresponding {@link OperatorType}
     * @throws IllegalArgumentException if {@code operatorString} is not a valid operator
     * @throws NullPointerException if {@code operatorString} is {@code null}
     */
    public static OperatorType fromString(String operatorString) {
        if (operatorString == null) throw new NullPointerException();
        switch (operatorString) {
            case "+":       return Plus;
            case "-":       return Minus;
            case "*":       return mTimes;
            case ".*":      return Time;
            case "/":       return mrDivide;
            case "./":      return rDivide;
            case "\\":      return mlDivide;
            case ".\\":     return lDivide;
            case "^":       return mPower;
            case ".^":      return Power;
            case ".'":      return Transpose;
            case "'":       return mTranspose;
        }
        /* control flow should not reach here */
        throw new IllegalArgumentException();
    }

    /**
     * get the number of operands of this operator, i.e. if operator is a binary operator, 2 will be returned, and if
     * operator is a unary operator, then 1 will be returned
     * @return number of operands
     */
    public int getNumOperands() {
        switch (this) {
            case Plus:      return 2;
            case Minus:     return 2;
            case mTimes:    return 2;
            case Time:      return 2;
            case mrDivide:  return 2;
            case rDivide:   return 2;
            case mlDivide:  return 2;
            case lDivide:   return 2;
            case mPower:    return 2;
            case Power:     return 2;
            case Transpose: return 1;
            case mTranspose:return 1;
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    @Override
    public String toString() {
        switch (this) {
            case Plus:      return "+";
            case Minus:     return "-";
            case mTimes:    return "*";
            case Time:      return ".*";
            case mrDivide:  return "/";
            case rDivide:   return "./";
            case mlDivide:  return "\\";
            case lDivide:   return ".\\";
            case mPower:    return "^";
            case Power:     return ".^";
            case Transpose: return ".'";
            case mTranspose:return "'";
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}
