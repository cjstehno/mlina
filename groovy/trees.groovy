@Grapes(
    value = [
        @Grab('org.apache.commons:commons-math3:3.6.1'),
        @Grab('org.jfree:jfreechart:1.0.19')
    ]
)

import static org.apache.commons.math3.util.FastMath.log

def createDataSet(){
    new Tuple2(
        ['no surfacing', 'flippers'],
        [
            [1, 1, 'yes'],
            [1, 1, 'yes'],
            [1, 0, 'no'],
            [0, 1, 'no'],
            [0, 1, 'no']
        ]
    )
}

def calcShannonEntropy(dataSet){
    int numEntries = dataSet.second.size()
    def labelCounts = dataSet.second.countBy { it[2] }
    double shannonEnt = 0.0
    labelCounts.keySet().each { key->
        def prob = ((double)labelCounts[key])/numEntries
        shannonEnt -= prob * log(2.0, prob)
    }
    shannonEnt
}

def data = createDataSet()
println calcShannonEntropy(data)