LAPPS Schema Compiler
============================

**NOTE** This is still a work in progress and likely contains misleading and/or
downright false information...

### Build Status
[![Master build status](http://grid.anc.org:9080/travis/svg/oanc/org.anc.json.schema-compiler?branch=master)](https://travis-ci.org/oanc/org.anc.lapps.schema-compiler)
[![Develop build status](http://grid.anc.org:9080/travis/svg/oanc/org.anc.json.schema-compiler?branch=develop)](https://travis-ci.org/oanc/org.anc.lapps.schema-compiler)

The LAPPS (Language Application Grid) Schema compiler generates a
[JSON Schema](http://json-schema.org) from the LAPPS alternate syntax. The alternate
syntax is **not** a new schema language, it is simply a simplified syntax for 
representing the objects that make up a JSON schema.

## Maven

```xml
<dependency>
    <groupId>org.anc.json</groupId>
    <artifactId>compiler</artifactId>
    <version>${see below}</version>
</dependency>
```

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.anc.json/compiler/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.anc.json/compiler)

## Examples

Example schemata can be found at:

1. [http://vocab.lappsgrid.org/schema/lif.schema](http://vocab.lappsgrid.org/schema/lif.schema)
1. [http://vocab.lappsgrid.org/schema/metadata.schema](http://vocab.lappsgrid.org/schema/metadata.schema)

# Alternate Syntax

Since a schema using the alternate syntax is really just a [Groovy](http://groovy.codehaus.org) script a basic 
understanding of the Groovy language is recommended, but not required. 

The same set of JSON Schema keywords and constructs is used so the alternate syntax looks 
almost exactly the same as the Javascript version; except with far fewer double 
quotes, colons, and braces. Please refer to the [JSON Schema specification](http://json-schema.org) for a description
of the JSON Schema syntax.

### Built in variables

The following variables are declared automatically and made available by the compiler:

* object
* array
* number
* integer
* string
* nil *(since null is already a keyword in Groovy)*

Using a built in variable is exactly the same as using the variable name 
 surrounded by double quotes.
 
    type object
    // is exactly the same as
    type "object"
    
## Name/Value pairs

In the alternate syntax the *name* in a name/value pair does not need to be enclosed in 
double quotes, unless it contains characters that are not allowed in a Groovy (Java) 
variable name:

In Javascript:

    {
        "type": "integer",
        "minimum": 1
    }
    
in the alternate syntax:

    type integer
    minimum 1
    
**NOTE**

1. The outer braces are not required.
1. No double quotes around the keywords *type*, *integer*, or *minimum*.
1. No colon between the name and the value.
1. No comma required to separate statements (see comments below).

Multiple statements can be combined on a single line by separating them with
semi-colons.

    type integer; minimum 1
        

## JSON Objects

In the alternate syntax a JSON object is simply a name/value pair where the value
 is another sequence of name/value pairs wrapped in curly braces.
  
     properties {
         name {
             type string
         }
         age {
             type integer
             minimum 0
         }
     }
     
## JSON Arrays

In the alternate syntax any comma separated list of values is converted into a JSON
array object

    type object, string, number

This is equivalent to the Javascript

    "type":["object", "string", "number"]
 
To create an array with containing a single element the array syntax must be used 
explicitly. In addition, since the Groovy parser will attempt to parse 

    type [ object ]
    
as an array access we also need to include the array in parenthesis:

    type([ object ])
    
The exception to this is the *required* keyword, which will always parse the value
as an array regardless of the number of items in the list.

## JSON Values

JSON values are represented in exactly the same way as they are in Javascript.

**NOTE** Due to possible differences in Groovy/Javascript floating point representations
and other issues there may be some differences in some types.

# Programming Constructs

One important factor to keep in mind is that under the hood the LAPPS alternate syntax 
is just a Groovy script (Java program).  It is the execution of the script that 
generates the Groovy (Java) object that is serialized into JSON using the the 
[Jackson](http://wiki.fasterxml.com/JacksonHome) library. This means it is possible to
use arbitrary Groovy/Java programming constructs in schema definition.

## Variables

New variables can be defined at any point in an alternate syntax schemsa. New variable
definitions do not need to specify a type.

    root = 'http://json.example.com'
    
    title 'Variables example.'
    id root
    properties {
        firstName { type string; id "$root/firstName" }
        lastName { type string; id "$root/lastName }
    }
    

**Groovy Note** Variable substitution does not happen in strings enclosed in single 
quotes. For the above to work double quotes MUST be used.


## Flow of Control

Any Groovy flow of control structure can be used in an alternate syntax schema

    root = 'http://json.example.com'
    hasAge = true
    props = [ 'firstName', 'lastName', 'address' ]
    id root
    properties {
        props.each {
            myId = "$root/$it"
            "$it" { type string; id myId }
        }
        if (hasAge) {
            age { type integer; minimum 0; id "$root/age" }
        }
    }
    
This generates the schema:

    {
        "id": "http://json.example.com",
        "properties": {
            "firstName": {
                "type": "string",
                "id": "http://json.example.com/firstName"
            },
            "lastName": {
                "type": "string",
                "id": "http://json.example.com/lastName"
            },
            "address": {
                "type": "string",
                "id": "http://json.example.com/address"
            },
            "age": {
                "type": "integer",
                "minimum": 0,
                "id": "http://json.example.com/age"
            }
        }
    }

**Groovy Note** The variable *it* is the default name used by Groovy when passing
parameters to closures.

## Imports and @Grab

Give examples that import other Groovy/Java modules and use @Grab to fetch a 
dependency from a Maven repository.

While I can not imagine a use-case where these features would be useful (and they might 
actually be harmful) I try to not let my lack of imagination limit what others mighht
do.


# Caveats
    
List known limitations and problems here:
    
- the current implementation accepts any input and generates output. If the output
is not a valid JSON schema... que sera sera...
- multipleOf, anyOf, oneOf, et al. need work.

# Command Line

Show how to run the standalone jar file.

# As a Library

Provide code that shows how to use the SchemaCompiler class.

    String schema = """
        type object
        properties {
            firstName { type string; required true }
            lastName { type string; required true }
            age {
                type integer
                minimum 0
            }
        }
    """
            
    SchemaCompiler compiler = new SchemaCompiler()
    println compiler.compile(schema)
    
## Maven Dependency

    <dependency>
        <groupId>org.anc.lapps.json</groupId>
        <artifactId>schema-compiler</artifactId>
        <version>${schema-compiler-version}</version>
    </dependency>

**NOTE**
  
These modules are not yet on Maven Central and must be retrieved
from the ANC's Nexus repositories

    <repositories>
        ...
        <repository>
          <id>anc-releases</id>
          <url>http://www.anc.org:8080/nexus/content/repositories/releases/</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>anc-snapshots</id>
          <url>http://www.anc.org:8080/nexus/content/repositories/snapshots/</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </snapshots>
        </repository>
     </repositories>
