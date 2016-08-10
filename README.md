# MLINA 

The contents of this project are based on the code developed during the reading of 
"[Machine Learning in Action](https://www.manning.com/books/machine-learning-in-action)" by Peter Harrington, from Manning books.

The example code in the book is written in Python. While I am learning the concepts and working along with the Python examples, I am also converting them to 
Groovy so that I have something I might actually use and understand at a later date.

I have made every effort to keep the Python to Groovy conversion as true to the original source code
as possible; however, since this learning effort is focussed more on the concepts than on the implementations, 
there may be some errors in the Groovy implementations - I would recommend finding existing implementations
or doing a thorough investigation of the code against the algorithm to ensure adequacy before using in
any real application.

## Useful Libraries

Some Java libraries that came in handy:

* http://jscience.org/api/index.html - Java science library
* http://commons.apache.org/proper/commons-math/ - Java advanced math library
* http://www.jfree.org/jfreechart/api.html - Java charting/graphing library
* http://www.graphviz.org/ - diagram rendering language and tool

## Book Errata

https://manning-content.s3.amazonaws.com/download/8/94d527f-01ea-4fe5-926a-9d0b77bed50e/Harrington_MachineLearninginAction_Err2.htm

## Python

The python examples are simple scripts, though some blocks were commented out or changed while working through the examples. In general they should be run via command line
in the `python` directory of the project and they should print out relevant information.

## Groovy

The Groovy version of the code started out as simple scripts with Grape imports; however, as the code progressed, I converted to a Gradle-based project format to make better use 
of the available libraries and to get better code reuse.
 
If there are any scripts left when you read this, they are simply executed in the `groovy` directory of the project, on the command line.

The Gradle-based project version of the code should be build with tests - the tests are the code examples, based on the Python version of the code.

    ./gradlew clean build test
    

