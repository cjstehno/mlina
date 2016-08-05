package mlina

import static org.apache.commons.lang3.SerializationUtils.deserialize
import static org.apache.commons.lang3.SerializationUtils.serialize
import static org.apache.commons.math3.util.FastMath.log

class Trees {

    static Tuple2<List<String>, List<List>> createDataSet() {
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

    static double calcShannonEntropy(List<List> dataSet) {
        int numEntries = dataSet.size()
        def labelCounts = dataSet.countBy { it[-1] }

        double shannonEnt = 0.0
        labelCounts.keySet().each { key ->
            double prob = ((double) labelCounts[key]) / numEntries
            shannonEnt -= prob * log(2.0, prob)
        }

        shannonEnt
    }

    static List<List> splitDataSet(List<List> dataSet, int axis, int value) {
        def resultingDataSet = []

        dataSet.each { featVec ->
            if (featVec[axis] == value) {
                def reducedFeatVec = featVec[0..<axis]
                reducedFeatVec << featVec[(axis + 1)..(-1)]
                resultingDataSet << reducedFeatVec.flatten()
            }
        }

        resultingDataSet
    }

    static int chooseBestFeatureToSplit(List<List> dataSet) {
        int numFeatures = dataSet[0].size() - 1 // omit last which is class
        def baseEntropy = calcShannonEntropy(dataSet)
        double bestInfoGain = 0
        int bestFeature = -1

        numFeatures.times { i ->
            def featList = dataSet.collect { it[i] }
            def uniqueVals = featList.unique()
            def newEntropy = 0.0
            uniqueVals.each { value ->
                def subDataSet = splitDataSet(dataSet, i, value)
                def prob = subDataSet.size() / ((float) dataSet.size())
                newEntropy += prob * calcShannonEntropy(subDataSet)
            }

            def infoGain = baseEntropy - newEntropy
            if (infoGain > bestInfoGain) {
                bestInfoGain = infoGain
                bestFeature = i
            }
        }

        bestFeature
    }

    private static String majorityCount(classList) {
        classList.countBy { it }.max { it.value }.key
    }

    static createTree(dataSet, labels) {
        def classList = dataSet.collect { it[-1] }
        if (classList.count { it == classList[0] } == classList.size()) {
            return classList[0]
        }
        if (dataSet[0].size() == 1) {
            return majorityCount(classList)
        }

        int bestFeat = chooseBestFeatureToSplit(dataSet)
        def bestFeatLabel = labels[bestFeat]

        def myTree = [(bestFeatLabel): [:]]
        labels.remove(bestFeat)

        dataSet.collect { it[bestFeat] }.unique().each { value ->
            def subLabels = labels.clone()
            myTree[bestFeatLabel][value] = createTree(splitDataSet(dataSet, bestFeat, value), subLabels)
        }

        myTree
    }

    static classify(inputTree, featLabels, testVec) {
        String firstStr = inputTree.keySet().iterator().next()
        def secondDict = inputTree[firstStr]
        int featIndex = featLabels.indexOf(firstStr)

        String classLabel
        secondDict.keySet().each { key ->
            if (testVec[featIndex] == key) {
                if (secondDict[key] instanceof Map) {
                    classLabel = classify(secondDict[key], featLabels, testVec)
                } else {
                    classLabel = secondDict[key]
                }
            }
        }
        classLabel
    }

    static retrieveTree(int i) {
        [
            ['no surfacing': [0: 'no', 1: ['flippers': [0: 'no', 1: 'yes']]]],
            ['no surfacing': [0: 'no', 1: ['flippers': [0: ['head': [0: 'no', 1: 'yes']], 1: 'no']]]]
        ][i]
    }

    static storeTree(inputTree, String filename) {
        new File(filename).bytes = serialize(inputTree)
    }

    static grabTree(String filename) {
        deserialize(new File(filename).bytes)
    }

}
