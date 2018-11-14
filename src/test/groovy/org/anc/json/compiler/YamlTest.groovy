/*
 * Copyright (c) 2018 The American National Corpus
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
 */

package org.anc.json.compiler

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 *
 */
class YamlTest {

    SchemaCompiler compiler

    @Before
    void setup() {
        compiler = new SchemaCompiler()
        compiler.format = SchemaCompiler.Format.yaml
    }

    @After
    void teardown() {
        compiler = null
    }

    @Test
    void simpleYaml() {
        String script = '''
version "3"
services {
    web {
        name "Web Service"
    }
}
'''
        String yaml = compiler.compile(script)
        println yaml
    }

    @Test
    void docker_compose() {

        String script = '''
version "3"
services {
    web {
        image 'docker.lappsgrid.org/lappsgrid/stanford'
        deploy {
            replicas 3
            resources {
                limits {
                    cpus "0.5"
                    memory '3G'
                }
            }
            restart_policy {
                condition 'on-failure'
            }
        }
        ports "80:80", "443:443", "8000:8000"  
        networks 'webnet', 'public'   
    }
    networks {
        webnet null
    }
}
'''
        String yaml = compiler.compile(script)
        println yaml
    }
}
