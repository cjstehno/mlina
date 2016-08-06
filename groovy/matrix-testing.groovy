@Grapes(
   @Grab('org.apache.commons:commons-math3:3.6.1')
)

import org.apache.commons.math3.linear.*

class Outputter {

    static output(matrix){
        int rows = matrix.rowDimension 
        int cols = matrix.columnDimension
        rows.times { row->
            double[] values = matrix.getRow(row)
            println values
        }
    }
}

// 4 rows, 3 cols
def mat = new Array2DRowRealMatrix(4,3)
Outputter.output mat

println ''

def input = new Array2DRowRealMatrix([
    [1,2,3],
    [4,5,6],
    [7,8,9],
    [10,11,12]
] as double[][])
Outputter.output input

println ''

def twos = new Array2DRowRealMatrix([
    [2,2,2],
    [2,2,2],
    [2,2,2],
    [2,2,2]
] as double[][])
Outputter.output twos

println ''

def sum = mat.add(input)
Outputter.output sum

println ''

def smult = sum.scalarMultiply(4d)
Outputter.output smult

println ''

def vec = new ArrayRealVector([10,100,1000] as double[])
println smult.operate(vec)