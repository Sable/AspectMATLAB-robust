package transformer;

import ast.PatternName;

public class UnboundedIdentifier extends Exception {
    private final PatternName site;

    public UnboundedIdentifier(PatternName site) {
        this.site = site;
    }

    public PatternName getUnboundedIdentifierSite() {
        return site;
    }
}
