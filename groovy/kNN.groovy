@Grapes(
    @Grab(group = 'org.apache.commons', module = 'commons-math3', version = '3.6.1')
)
import org.apache.commons.math3.ml.distance.EuclideanDistance
@Grapes(
    @Grab(group = 'org.apache.commons', module = 'commons-math3', version = '3.6.1')
)

import org.apache.commons.math3.ml.distance.EuclideanDistance
@Grapes(
    @Grab(group = 'org.apache.commons', module = 'commons-math3', version = '3.6.1')
)
import org.apache.commons.math3.ml.distance.EuclideanDistance
@Grapes(
    @Grab(group = 'org.apache.commons', module = 'commons-math3', version = '3.6.1')
)
import org.apache.commons.math3.ml.distance.EuclideanDistance

def createDataSet() {
    [
        new Tuple2('A', [1.0, 1.1]),
        new Tuple2('A', [1.0, 1.0]),
        new Tuple2('B', [0.0, 0.0]),
        new Tuple2('B', [0.0, 0.1])
    ]
}

String classify0(inX, dataSet, int k) {
    dataSet.collect { d ->
        new Tuple2<>(
            d.first, new EuclideanDistance().compute(d.second as double[], inX as double[])
        )
    }.sort { it.second }.take(k).countBy { it.first }.max { it.value }.key
}

def dataSet = createDataSet()
println "Data set: $dataSet"

println "Result: ${classify0([0, 0], dataSet, 3)}"

/*
Data set: [[A, [1.0, 1.1]], [A, [1.0, 1.0]], [B, [0.0, 0.0]], [B, [0.0, 0.1]]]
Result: B
 */