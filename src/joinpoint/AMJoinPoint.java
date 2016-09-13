package joinpoint;

public abstract class AMJoinPoint {
    protected final String enclosingFilename;
    protected final int startLineNumber;
    protected final int startColumnNumber;
}
