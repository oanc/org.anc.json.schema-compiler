/*-
 * Copyright 2014 The American National Corpus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anc.json.compiler

/**
 * This class is not used and is intended for future development.
 *
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
