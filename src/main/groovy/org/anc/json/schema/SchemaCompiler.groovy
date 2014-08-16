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
package org.anc.json.schema

import groovy.json.JsonBuilder
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import groovy.util.logging.Slf4j

/**
 * @author Keith Suderman
 */
@Slf4j
class SchemaCompiler {
    static final String EXTENSION = ".schema"

    Set<String> included = new HashSet<String>()
    File parentDir
    Binding bindings = new Binding()
    Map contents = [:]

    String compile(File file, args) {
        parentDir = file.parentFile
        return compile(file.text, args)
    }

    String compile(File file) {
        parentDir = file.parentFile
        return compile(file.text, null)
    }

    String compile(String source) {
        return compile(source, null)
    }

    String compile(String scriptString, args) {
        ClassLoader loader = getLoader()
        CompilerConfiguration configuration = getCompilerConfiguration()
        GroovyShell shell = new GroovyShell(loader, bindings, configuration)

        Script script = shell.parse(scriptString)
        if (args != null && args.size() > 0) {
            // Parse any command line arguements into a HashMap that will
            // be passed in to the user's script.
            def params = [:]
            args.each { arg ->
                String[] parts = arg.split('=')
                String name = parts[0].startsWith('-') ? parts[0][1..-1] : parts[0]
                String value = parts.size() > 1 ? parts[1] : Boolean.TRUE
                params[name] = value
            }
            script.binding.setVariable("args", params)
        }
        else {
            script.binding.setVariable("args", [:])
        }
        ["object", "string", "array", "number", "boolean", "integer", "null"].each { script.binding.setVariable(it, it) }
        script.binding.with {
            setVariable "nil", "null"
//            setVariable "schema", '$schema'
//            setVariable "ref", '$ref'
        }
        script.binding.setVariable("nil", "null")

//        script.binding.setVariable("schema", "schema")
        script.metaClass = getMetaClass(script.class, shell)
        try {
            // Running the DSL script creates the objects needed to generate the HTML
            script.run()
        }
        catch (Exception e) {
            println()
            println "Script execution threw an exception:"
            e.printStackTrace()
            println()
        }

//        println "Contents ${contents.size()}"
//        contents.each { name,object ->
//            if (object instanceof UndeclaredReference) {
//                println "UndeclaredReference"
//            }
//            else {
//                println "${name}='${object}'"
//            }
//        }
//        String json = JsonOutput.toJson(contents)
//        println json
//        println JsonOutput.prettyPrint(json)
//        println "Using JsonBuilder"
        return new JsonBuilder(contents).toPrettyString()
    }

    ClassLoader getLoader() {
        ClassLoader loader = Thread.currentThread().contextClassLoader;
        if (loader == null) {
            loader = this.class.classLoader
        }
        return loader
    }

    CompilerConfiguration getCompilerConfiguration() {
        ImportCustomizer customizer = new ImportCustomizer()
        /*
         * Custom imports can be defined in the ImportCustomizer.
         * For example:
         *   customizer.addImport("org.anc.xml.Parser")
         *   customizer.addStarImports("org.anc.util")
         *
         * The jar files for any packages imported this way must be
         * declared as Maven dependencies so they will be available
         * at runtime.
         */

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(customizer)
        return configuration
    }


    MetaClass getMetaClass(Class<?> theClass, GroovyShell shell) {
        ExpandoMetaClass meta = new ExpandoMetaClass(theClass, false)
        meta.methodMissing = { String name, args ->
            log.info "Missing method {}", name
            def value = contents[name]
            if (value != null) {
//                if (!(value instanceof UndeclaredReference)) {
//                    throw new SchemaException("Redefinition of $name ${value.class.name}")
//                }
                log.warn "Redefinition of {} {}", name, value.class.name
            }

            if (args == null || args.size() == 0) {
                throw new SchemaException("No parameters passed to ${name}.")
            }
            if ((args[0] instanceof Closure)) {
//                throw new SchemaException("Parameter to ${name} must be a Closure.")
                log.debug "Running closure()"
                Closure cl = (Closure) args[0]
                cl.delegate = new MethodDelegate(bindings)
                cl.resolveStrategy = Closure.DELEGATE_FIRST
                cl()
                contents[name] = cl.delegate.contents
            }
            else {
                if (args.size() == 1) {
                    value = args[0]
                }
                else {
                    value = args
                }
                log.debug "Setting contents for {} to {}", name, value
                contents[name] = value
                log.debug "Contents size is {}", contents.size()
            }
            //println "Missing method $name"
        }

        meta.include = { String filename ->
            // Make sure we can find the file. The default behaviour is to
            // look in the same directory as the source script.
            // TODO Allow an absolute path to be specified.

            def filemaker
            if (parentDir != null) {
                filemaker = { String name ->
                    return new File(parentDir, name)
                }
            }
            else {
                filemaker = { String name ->
                    new File(name)
                }
            }

            File file = filemaker(filename)
            if (!file.exists() || file.isDirectory()) {
                file = filemaker(filename + EXTENSION)
                if (!file.exists()) {
                    throw new FileNotFoundException(filename)
                }
            }
            // Don't include the same file multiple times.
            if (included.contains(filename)) {
                return
            }
            included.add(filename)


            // Parse and run the script.
            Script included = shell.parse(file)
            included.metaClass = getMetaClass(included.class, shell)
            included.run()
        }


        meta.initialize()
        return meta
    }

    static void usage() {
        println """
USAGE

java -jar jsonc-${Version.version}.jar /path/to/input /path/to/output/file"

"""
    }

    static void main(args) {
        if (args.size() == 0) {
            usage()
            return
        }

        if (args[0] == '-version') {
            println()
            println "LAPPS JSON Schema Compiler v${Version.version}"
            println "Copyright 2014 American National Corpus"
            println()
            return
        }
        else if (args.size() < 2) {
            usage()
            return
        }
        def argv = null
        if (args.size() > 2) {
            argv = args[2..-1]
        }
        String json = new SchemaCompiler().compile(new File(args[0]), argv)
        new File(args[1]).text = json
    }

}
