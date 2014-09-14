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

import groovy.util.logging.Slf4j

/**
 * @author Keith Suderman
 */
@Slf4j
class MethodDelegate {
    Map contents = [:]
    Binding binding

    public MethodDelegate(Binding binding) {
        this.binding = binding
    }

    def propertyMissing(String name) {
        def value = binding[name]
        if (value == null) {
            throw new SchemaException("Undefined property ${name}")
        }
        log.info "Reading {}", name
        return value
    }
    void propertyMissing(String name, value) {
        throw new SchemaException("Do not use the assignment operator to set properties.")
    }

    void required(args) {
        if (args instanceof List) {
            contents.required = args
        }
        else {
            contents.required = [ args ]
        }
    }

    def methodMissing(String name, args) {
        log.debug "MethodDelegate.methoMissing {}", name
        def value = null
        if (args[0] instanceof Closure) {
            Closure cl = (Closure) args[0]
            cl.delegate = new MethodDelegate(binding)
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            value = cl.delegate.contents
        }
        else if (args.size() == 1) {
            value = args[0]
        }
        else {
            value = args
        }
        def current = contents[name]
        if (current == null) {
            log.debug "Adding new value for {}", name
            contents[name] = value
        }
        else if (current instanceof List) {
            log.debug "Adding value to list for {}", name
            current << value
        }
        else {
            log.debug "Creating new list for {}", name
            contents[name] = [ current, value ]
        }
        return contents
    }
}
