package de.terrestris.shoguncore.enumeration;

public enum RuleType {
    ALLOW("ALLOW"),
    DENY("DENY"),
    MODIFY("MODIFY");

    private final String type;

    private RuleType(String type) {
        this.type = type;
    }
}
