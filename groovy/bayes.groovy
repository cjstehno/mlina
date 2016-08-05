@Grapes(
    value = [
        @Grab('org.apache.commons:commons-math3:3.6.1')
    ]
)
import org.apache.commons.math3.linear.Array2DRowRealMatrix

def loadDataSet() {
    new Tuple2(
        [
            ['my', 'dog', 'has', 'flea', 'problems', 'help', 'please'],
            ['maybe', 'not', 'take', 'him', 'to', 'dog', 'park', 'stupid'],
            ['my', 'dalmatian', 'is', 'so', 'cute', 'I', 'love', 'him'],
            ['stop', 'posting', 'stupid', 'worthless', 'garbage'],
            ['mr', 'licks', 'ate', 'my', 'steak', 'how', 'to', 'stop', 'him'],
            ['quit', 'buying', 'worthless', 'dog', 'food', 'stupid']
        ],
        [0, 1, 0, 1, 0, 1] // 1 is abusive, 0 not
    )
}

def createVocabList(dataSet) {
    def vocabSet = [] as Set
    dataSet.each { document ->
        vocabSet.addAll(document)
    }
    return vocabSet as List
}

def setOfWords2Vec(vocabList, inputSet) {
    def returnVec = new int[vocabList.size()]
    inputSet.each { word ->
        if (word in vocabList) {
            returnVec[vocabList.indexOf(word)] = 1
        } else {
            println "the word: $word is not in my Vocabulary!"
        }
    }
    returnVec
}

def trainNB0(trainMatrix, trainCategory) {
    int numTrainDocs = trainMatrix.size()
    int numWords = trainMatrix[0].size()
    def pAbusive = trainCategory.sum() / numTrainDocs
    def p0Num = new Array2DRowRealMatrix(1,numWords)
    def p1Num = new Array2DRowRealMatrix(1,numWords)
    def p0Denom = 0.0
    def p1Denom = 0.0

    numTrainDocs.times { i ->
        if (trainCategory[i] == 1) {
            p1Num = p1Num.add( singleRow(trainMatrix[i], numWords ))
            p1Denom += trainMatrix[i].sum()
        } else {
            p0Num = p0Num.add( singleRow(trainMatrix[i], numWords ))
            p0Denom += trainMatrix[i].sum()
        }
    }

    def p1Vect = p1Num / p1Denom // TODO: these need to be matricized
    def p0Vect = p0Num / p0Denom // TODO: these need to be matricized

    [p0Vect, p1Vect, pAbusive]
}

Array2DRowRealMatrix singleRow(row, int colCount){
    def matrix = new Array2DRowRealMatrix(1,colCount)
    matrix.setRow(0, row as double[])
}

def data = loadDataSet()
def myVocabList = createVocabList(data.first)
//println myVocabList
//println myVocabList.size()

//println setOfWords2Vec(myVocabList, data.first[0])
//println setOfWords2Vec(myVocabList, data.first[3])

def trainMat = []
data.first.each { postinDoc ->
    trainMat << setOfWords2Vec(myVocabList, postinDoc)
}

def (p0V, p1V, pAb) = trainNB0(trainMat, data.second)
println p0V
println p1V
println pAb