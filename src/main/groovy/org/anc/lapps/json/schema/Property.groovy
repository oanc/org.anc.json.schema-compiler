package org.anc.lapps.json.schema

/**
 * @author Keith Suderman
 */
class Property {
    String name
    String type

    public Property(Map params) {
        this.name = params.name
        this.type = params.type
    }

    public Property(String name, String type) {
        this.name = name
    }

    String toString() {
        return "$name $type"
    }
}
