package org.anc.lapps.json.schema

import org.anc.json.schema.SchemaCompiler
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test


/**
 * @author Keith Suderman
 */
class TestCompiler {

    SchemaCompiler compiler

    @Before
    void setup() {
        compiler = new SchemaCompiler()
    }

    @After
    void cleanup() {
        compiler = null
    }

    @Ignore
    void test0() {
        compiler.compile "println 'Hello world.'"
    }

    @Ignore
    void argsTest() {
        compiler.compile """
        println args.size()
"""
    }

    @Ignore
    void titleTest() {
        compiler.compile """
        title "This is the title"
        schema "http://www.anc.org"
"""
    }

    @Ignore
    void typeTest() {
        compiler.compile """
        title "This is the title"
        '\$schema' "http://www.anc.org"
        type object, array
        additionalProperties true
"""
    }

    @Ignore
    void nestedTest() {
        compiler.compile """
title "This is a test"
definitions {
    context {
        additionalProperties true
        type object, string, array, nil
    }
}
"""
    }

    @Ignore
    void lappsTest() {
        compiler.compile """
title "LAPPS Interchange Format"
description "Data structures exchanged by LAPPS web services."
type object
properties {
    metadata {
        type object
        additionalProperties true
    }
    processingSteps {
        description "A list of processing steps that have modified the document."
        type array
        items {
            type object
            description "A single processing step that modified the document."
            properties {
                id {
                    type string
                }
                metadata {
                    type object
                    additionalPropeties true
                }
            }
        }
    }
}
"""
    }

    @Ignore
    void refTest() {
        compiler.compile """
title "Reference test."
definitions {
    common {
        properties {
            foo { type string }
            bar { type string }
        }
    }
    anyOf {
        ref "#/definitions/common/foo"
        ref "#/definitions/common/bar"
    }
}
"""
    }
    @Ignore
    void testJsonLdSchema() {
        ClassLoader loader = Thread.currentThread().contextClassLoader
        if (loader == null) {
            loader = this.class.classLoader
        }
        URL url = loader.getResource('jsonld-schema.schema')
        compiler.compile url.text
    }

    @Ignore
    void testLif() {
        File file = new File('/Users/suderman/Projects/LAPPS/json/lif.schema')
        compiler.compile file.text
    }

    @Test
    void testServiceMetadata() {
        File file = new File('/Users/suderman/Projects/LAPPS/json/service-metadata.schema')
        compiler.compile file.text
    }

//    ClassLoader getLoader() {
//        ClassLoader loader = Thread.currentThread().getContextClassLoader()
//        if (loader == null) {
//            loader = TestCompiler.classLoader
//        }
//        return loader
//    }
//
//    void compile(String source) {
//        Binding binding = new Binding()
//
//        GroovyShell shell = new GroovyShell(getLoader(), binding)
//        Script script = shell.parse(source)
//        ['string', 'object', 'number'].each { type ->
//            script.binding.setVariable(type, type)
//        }
//        script.metaClass = getMetaClass(script.class, shell)
//        script.run()
//    }
//
//    MetaClass getMetaClass(Class<?> theClass, GroovyShell shell) {
//        ExpandoMetaClass meta = new ExpandoMetaClass(theClass, false)
//
//        meta.initialize()
//        return meta
//    }

}




