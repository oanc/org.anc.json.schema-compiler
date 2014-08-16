package org.anc.lapps.json.schema

/**
 * @author Keith Suderman
 */
class NonTerminal {
    String name

    public NonTerminal(Map params) {
        this.name = params.name
    }

    public NonTerminal(String name) {
        this.name = name
    }

    String toString() {
        return name
    }
}
