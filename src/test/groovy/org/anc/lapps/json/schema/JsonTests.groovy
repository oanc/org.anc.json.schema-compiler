package org.anc.lapps.json.schema

import groovy.json.JsonSlurper
import org.junit.Test

/**
 * @author Keith Suderman
 */
class JsonTests {

    Indent indent

    String getType(object) {
        if (object instanceof String) return 'string'
        if (object instanceof List) return 'list'
        if (object instanceof Map) return 'map'
        return 'Unknown'
    }

    void out(List list) {
        println "["
        ++indent
        list.each { out(it) }
        --indent
        println "${indent}]"
    }

    void out(Map map) {
        println "{"
        ++indent
        map.each { name, value ->
            print "${indent}${name}:"
            out(value)
        }
        --indent
        println "${indent}}"
    }

    void out(String string) {
        println " \"${string}\""
    }

    void out(Object o) {
        println o
    }

    @Test
    void parseJsonLdSchema() {
        File file = new File("/Users/suderman/Downloads/schema")
        def object = new JsonSlurper().parse(file)
        indent = new Indent()
        out(object)
    }

}

class Indent {
    int size

    Indent next() {
        more()
        return this
    }

    Indent previous() {
        less()
        return this
    }

    String toString() {
        ' ' * (size * 4)
    }

    void more() {
        ++size
    }

    void less() {
        if (size > 0) {
            --size
        }
    }
}
