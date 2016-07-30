@Grapes(
    @Grab(group='org.jscience', module='jscience', version='4.3.1')
)

import org.jscience.mathematics.vector.*

double[][] rand(int rowCount, int colCount){
    def rows = []
    rowCount.times { r->
        def cols = []
        colCount.times { c->
            cols << Math.random()
        }
        rows << (cols as double[])
    }
    rows as double[][]
}

def mat = Float64Matrix.valueOf(rand(4,4))
println "Matrix: $mat\n"

def imat = mat.inverse()
println "Inverse: $imat\n"

def result = mat.times(imat)
println "Multiplied: $result"