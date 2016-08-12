@Grapes(
   @Grab('org.apache.commons:commons-math3:3.6.1')
)

import org.apache.commons.math3.linear.*

def formatter = new RealMatrixFormat('{', '}', '{', '}', ',\n ', ',\t')

def mat = new Array2DRowRealMatrix(4,3)
mat.setRow(0, [1.0, 2.0, 3.0] as double[])
mat.setColumn(1, [9.0, 8.0, 7.0, 6.0] as double[])

println formatter.format(mat)
println()

def seqMat = new Array2DRowRealMatrix([
    [1.0, 2.0, 3.0] as double[],
    [4.0, 5.0, 6.0] as double[],
    [7.0, 8.0, 9.0] as double[],
    [10.0, 11.0, 12.0] as double[]
] as double[][])

println formatter.format(seqMat)
println()

def sum = mat.add(seqMat)
println formatter.format(sum)
println()

def diff = seqMat.subtract(mat)
println formatter.format(diff)
println()

def prod = mat.scalarMultiply(2)
println formatter.format(prod)
println()

def trans = seqMat.transpose()
println formatter.format(trans)
println()

class MultiplicationVisitor extends DefaultRealMatrixChangingVisitor {

    double factor

    double visit(int row, int column, double value){
        value * factor
    }
}

mat.walkInOptimizedOrder(new MultiplicationVisitor(factor:2.0))
println formatter.format(mat)
println()

class CollectingVisitor extends DefaultRealMatrixPreservingVisitor {

    List values = []

    void visit(int row, int column, double value){
        values << value
    }
}

def collectingVisitor = new CollectingVisitor()
mat.walkInOptimizedOrder(collectingVisitor)
println collectingVisitor.values
println()