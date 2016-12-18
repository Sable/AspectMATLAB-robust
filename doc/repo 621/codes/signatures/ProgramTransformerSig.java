public class CopyProgramTransformer
        <TStmt extends CopyStmtTransformer, TPattern extends CopyPatternTransformer>
        extends AbstractProgramTransformer<TStmt, TPattern>;
public class InplaceProgramTransformer
        <TStmt extends InplaceStmtTransformer, TPattern extends InplacePatternTransformer>
        extends AbstractProgramTransformer<TStmt, TPattern>;
