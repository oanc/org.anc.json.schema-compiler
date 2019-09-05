package org.anc.json.compiler

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.anc.json.compiler.SchemaCompiler
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue
/**
 * @author Keith Suderman
 */
class TestCompiler {

    SchemaCompiler compiler

    @Before
    void setup() {
        compiler = new SchemaCompiler()
        compiler.prettyPrint = true
    }

    @After
    void cleanup() {
        compiler = null
    }

    Object compile(String source) {
        String json = compiler.compile(source)
        return new JsonSlurper().parseText(json)
    }


    @Test
    void titleTest() {
        def object = compile """
        title "This is the title"
"""
        assertTrue object.title == "This is the title"
    }

    @Test
    void typeTest() {
        def object = compile """
        title "This is the title"
        '\$schema' "http://www.anc.org"
        type object, array
        additionalProperties true
"""
        assertTrue object.type instanceof List
        assertTrue object.type.size() == 2
        assertTrue object.type[0] == "object"
        assertTrue object.type[1] == "array"
        assertTrue object.additionalProperties
    }

    @Test
    void nestedTest() {
        def object = compile """
definitions {
    context {
        additionalProperties true
        type object, string, array, nil
    }
}
"""
        assertTrue object.definitions.context.additionalProperties
        assertTrue object.definitions.context.type instanceof List
        List types = object.definitions.context.type
        assertTrue types.size() == 4
        assertTrue types == ['object', 'string', 'array', 'null']
    }

    @Test
    void requiredTest1() {
        def object = compile """
type object
properties {
    name { type string }
    age { type integer }
}
required 'name'
"""
        assertTrue object.required instanceof List
        assertTrue object.required.size() == 1
    }

    @Test
    void requiredTest2() {
        def object = compile """
type object
properties {
    name { type string }
    age { type integer }
}
required 'name', 'age'
"""
        assertTrue object.required instanceof List
        assertTrue object.required.size() == 2
        assertTrue 'name' == object.required[0]
        assertTrue 'age' == object.required[1]
    }

    @Test
    void refTest() {
        def object = compile """
\$ref "one"
"""
        assertTrue object['$ref'] == "one"

    }

    @Test
    void schemaTest() {
        String expected = "Schema value"
        def object = compile """
\$schema "$expected"
"""
        assertTrue object['$schema'] == expected
    }

    @Test
    void compileLifSchema() {
        InputStream istream = this.class.classLoader.getResourceAsStream('lif.schema')
        assert null != istream

        String schema = istream.text
        String json = compiler.compile(schema)
        println schema
        println json
        println JsonOutput.prettyPrint(json)
    }

    @Test
    void writeXml() {
        def source = """
document {
    type object
    properties {
        name { type string }
        age { type integer }
    }
    required 'name', 'age'
}
"""
        compiler.format = SchemaCompiler.Format.xml
        String xml = compiler.compile(source)
        println xml
    }


    @Test
    void readmeExample() {
        String schema = """
    type object
    properties {
        firstName { type string }
        lastName { type string }
        age {
            type integer
            minimum 0
        }
    }
    required 'firstName', 'lastName'
"""

        SchemaCompiler compiler = new SchemaCompiler()
        println compiler.compile(schema)    }
}




