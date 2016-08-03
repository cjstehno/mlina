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
    int numEntries = dataSet.size()
    def labelCounts = dataSet.countBy { it[-1] }
    double shannonEnt = 0.0
    labelCounts.keySet().each { key->
        def prob = ((double)labelCounts[key])/numEntries
        shannonEnt -= prob * log(2.0, prob)
    }
    shannonEnt
}

def splitDataSet(dataSet, axis, value){
    def resultingDataSet = []
    dataSet.each { featVec->
        if( featVec[axis] == value){
            def reducedFeatVec = featVec[0..<axis]
            reducedFeatVec << featVec[(axis+1)..(-1)]
            resultingDataSet << reducedFeatVec.flatten()
        }
    }
    resultingDataSet
}

def chooseBestFeatureToSplit(dataSet){
    int numFeatures = dataSet[0].size() -1 // omit last which is class
    def baseEntropy = calcShannonEntropy(dataSet)
    double bestInfoGain = 0
    int bestFeature = -1

    numFeatures.times { i->
        def featList = dataSet.collect { it[i] }
        def uniqueVals = featList.unique()
        def newEntropy = 0.0
        uniqueVals.each { value->
            def subDataSet = splitDataSet(dataSet, i, value)
            def prob = subDataSet.size() / ((float)dataSet.size())
            newEntropy += prob * calcShannonEntropy(subDataSet)
        }

        def infoGain = baseEntropy - newEntropy
        if( infoGain > bestInfoGain){
            bestInfoGain = infoGain
            bestFeature = i
        }
    }

    bestFeature
}

def majorityCount(classList){
    classList.countBy { it }.max { it.value }.key
}

def createTree(dataSet, labels){
    def classList = dataSet.collect { it[-1] }
    if( classList.count { it == classList[0] } == classList.size() ){
        return classList[0]
    }
    if( dataSet[0].size() == 1){
        return majorityCount(classList)
    }

    int bestFeat = chooseBestFeatureToSplit(dataSet)
    def bestFeatLabel = labels[bestFeat]

    def myTree = [(bestFeatLabel):[:]]
    labels.remove(bestFeat)

    def featValues = dataSet.collect { it[bestFeat] }
    def uniqeVals = featValues.unique()
    uniqeVals.each { value->
        def subLabels = labels.clone()
        myTree[bestFeatLabel][value] = createTree(splitDataSet(dataSet, bestFeat, value), subLabels)
    }
    myTree
}

def data = createDataSet()
//println calcShannonEntropy(data)

//println splitDataSet(data.second, 0,1)
//println splitDataSet(data.second, 0,0)

//println chooseBestFeatureToSplit(data.second)

println createTree(data.second, data.first)
