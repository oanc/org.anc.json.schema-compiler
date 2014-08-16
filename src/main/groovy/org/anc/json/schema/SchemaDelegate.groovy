package org.anc.json.schema

/**
 * @author Keith Suderman
 */
class SchemaDelegate {

    Map handlers = [:]
    Map values = [:]

    def stringHandler = { name,value -> values[name] = value }
    def integerHandler = { name, value -> values[name] = value }
    def booleanHandler = { name, value -> values[name] = value }
    def arrayHandler = { name, value -> values[name] = value }

    public SchemaDelegate() {
        ['title', 'id', 'description', 'pattern', 'type'].each {
            handlers[it] = stringHandler.curry(it)
        }
        ['multipleOf', 'minimum', 'exclusiveMinimum', 'maximum', 'exclusiveMaximum',
        'maxLength', 'minLength', 'maxItems', 'minItems'].each {
            handlers[it] = integerHandler.curry(it)
        }
        ['items', 'additionalItems'].each {
            handlers[it] = arrayHandler.curry(it)
        }
    }

    def methodMissing(String name, args) {
        Closure closure = handlers[name]
        if (closure == null) {
            throw new MissingMethodException(name, SchemaDelegate.class, args)
        }
        closure(name, args)
    }

}
