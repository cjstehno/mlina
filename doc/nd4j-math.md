# Array Math: ND4J

In my last post, I discussed the matrix operations support provided by the Apache Commons Math API. In doing my research I also stumbled on a library that is much closer in functionality to the Python NumPy library. The ND4J library is (from their site):

> ND4J and ND4S are scientific computing libraries for the JVM. They are meant to be used in production environments, which means routines are designed to run fast with minimum RAM requirements.

The main draw I had at this point was that their support for array-style element-by-element operations was much deeper than standard matrix operations and much closer to what I was seeing in the Python code I was working with.

With NumPy in Python you can multiple two arrays such that the result is the multiplication of each value of the array by the corresponding value in the second array. This is not so simple with matrices (as shown in my last post)

With ND4J, it becomes much simpler:

```groovy
def arrA = Nd4j.create([1.0, 2.0, 3.0] as double[])
def arrB = Nd4j.create([2.0, 4.0, 6.0] as double[])
def arrC = arrA.mul(arrB)
println "$arrA + $arrB = $arrC"
```

will result in:

```
[1.00, 2.00, 3.00] * [2.00, 4.00, 6.00] = [ 2.00,  8.00, 18.00]
```

which is as we would expect from the Python case. ND4J also has the ability to do two-dimensional (matrix-style) arrays:

```groovy
def matA = Nd4j.create([
    [1.0, 2.0, 3.0] as double[],
    [4.0, 5.0, 6.0] as double[]
] as double[][])
println "Matrix: $matA\n"
```

which will yield:

```
Matrix: [[1.00, 2.00, 3.00],
 [4.00, 5.00, 6.00]]
```

All of the other mathematical operations I mentioned in the previous post are available and with data structures that feel a lot more rich and refined for general use.

This is barely scratching the surface of the available functionality. Also, the underlying code is native-based and has support for running on CUDA for higher performance. This library is definitely one to keep in mind for cases when you have a lot of array and matrix-based operations.