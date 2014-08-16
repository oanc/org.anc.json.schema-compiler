package org.anc.lapps.json.schema

/**
 * @author Keith Suderman
 */
class ListDelegate {
    List elements = []

    void methodMissing(String name, args) {
        elements << new ListElement(name:name, parameter:args)
    }

    public static class ListElement {
        String name
        def parameter
    }
}
