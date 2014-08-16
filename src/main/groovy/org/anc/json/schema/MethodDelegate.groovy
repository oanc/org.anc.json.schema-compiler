package org.anc.json.schema

import groovy.util.logging.Slf4j

/**
 * @author Keith Suderman
 */
@Slf4j
class MethodDelegate {
    //Map binding
    //List contents = []
    Map contents = [:]
    Binding binding

    public MethodDelegate(Binding binding) {
        this.binding = binding
    }

    def propertyMissing(String name) {
        def value = binding[name]
        if (value == null) {
//            value = new UndeclaredReference(name)
//            binding[name] = value
            throw new SchemaException("Undefined property ${name}")
        }
        //contents << name
        log.info "Reading {}", name
        return value
    }
    void propertyMissing(String name, value) {
        throw new SchemaException("Do not use the assignment operator to set properties.")
//        println "Writing $name"
    }

    def methodMissing(String name, args) {
        log.debug "MethodDelegate.methoMissing {}", name
        def value = null
        if (args[0] instanceof Closure) {
            Closure cl = (Closure) args[0]
            cl.delegate = new MethodDelegate(binding)
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
//            def value = contents[name]
//            if (value == null) {
//                contents[name] = cl.delegate.contents
//            }
//            else if (value instanceof List) {
//                contents[name] << cl.delegate.contents
//            }
//            else {
//                contents[name] = [ value, cl.delegate.contents ]
//            }
//            contents[name] = cl.delegate.contents
            value = cl.delegate.contents
        }
        else if (args.size() == 1) {
//            contents[name] = args[0]
            value = args[0]
        }
        else {
//            contents[name] = args
            value = args
        }
        def current = contents[name]
        if (current == null) {
            contents[name] = value
        }
        else if (current instanceof List) {
            current << value
        }
        else {
            contents[name] = [ current, value ]
        }
    }

//    void string(String name) {
//        println "${name}:String"
//        contents << new Property(name, 'String')
//    }
//
//    void object(String name) {
//        println "${name}:Object"
//        contents << new Property(name, 'Object')
//    }
//
//    void number(String name) {
//        println "${name}:Number"
//        binding[name] = new Property(name, 'Number')
//    }

//    String toString() {
//        return contents.join(", ")
//    }
}
