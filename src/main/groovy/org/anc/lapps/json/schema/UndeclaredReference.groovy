package org.anc.lapps.json.schema

/**
 * @author Keith Suderman
 */
class UndeclaredReference {
    String name

    public UndeclaredReference(String name) {
        this.name = name
    }

    public UndeclaredReference(Map map) {
        this.name = map.name
    }

    String toString() {
        return name
    }
}
