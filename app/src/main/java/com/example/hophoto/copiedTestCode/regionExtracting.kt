package com.example.hophoto.copiedTestCode

import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.createBitmap
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.pixels.Pixel
import java.io.File
import kotlin.collections.forEach
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

var Edges = mutableListOf<Edge>()

fun to2d(image: List<Int>,x : Int, y : Int) : List<List<Int>>{
    val returnImage = List<MutableList<Int>>(y) { mutableListOf<Int>() }
    for (i in 0 until y)
    {
        for (j in 0 until x)
        {
            returnImage[i].add(image[j + i*x])
//            println("y $i  x ${j+i*x}")
        }
    }
    return returnImage

}


@OptIn(ExperimentalStdlibApi::class)
fun test1(listTriple : MutableList<Triple<Int,Int,Int>>, height: Int, width: Int): IntArray {

    //maybe colored segmentation??

    //temp
    //temp

//    val imageFile = File("src/main/resources/mathTestSmall.png")
//    var inputImage = ImmutableImage.loader().fromFile(imageFile)
//    val listImage = mutableListOf<Int>()
//    val filter = GaussianBlurFilter(1)
//    inputImage = inputImage.filter(filter)
    Log.i("test","inside of test1")

    val listImage = mutableListOf<Int>()

    //var greyedImage = inputImage.map { p -> java.awt.Color(inputImage.pixel(p.x,p.y).toAverageGrayscale().toInt()) }
    for (i in 0 until height)
    {
        for (j in 0 until width)
        {
            listImage.add((listTriple[j + i * width].first * 0.2162 + listTriple[j + i * width].second * 0.7252 + listTriple[j + i * width].third * 0.0722).toInt())
        }
    }
    Log.i("test","inside of test1")
    var newImage=to2d(listImage,width,height)

    var segmentedGraph = (fastImageSegmentation(newImage as MutableList<MutableList<Int>>,100))
    var segmentedList = vertsToList(segmentedGraph,height,width)
    var listImageTriple = mutableListOf<Triple<Int,Int,Int>>()
    for (i in 0 until height)
    {
        for (j in 0 until width)
        {
            listImageTriple.add(Triple(segmentedList[i][j].first,segmentedList[i][j].second,segmentedList[i][j].third))
        }
    }

    val argbFormat = HexFormat {
        number {
            removeLeadingZeros = true
            prefix = "0x"
        }
        upperCase = true
        bytes {
            bytesPerGroup = 2
        }
    }

/*    Log.i("test",newImage.toString())
    Log.i("test",("FF534232".hexToInt(HexFormat.UpperCase).toString()))
    Log.i("test",250.toHexString(HexFormat.UpperCase).trimStart('0'))*/

    return listImageTriple.map {
        ("FF" + (it.first.toHexString(HexFormat.UpperCase).trimStart('0'))
                + (it.second.toHexString(HexFormat.UpperCase).trimStart('0'))
                + (it.third.toHexString(HexFormat.UpperCase).trimStart('0'))).toString().hexToInt(HexFormat.UpperCase)
    }.toIntArray()
/*
    return listImage.map {
        ("FF" + (it.toHexString(HexFormat.UpperCase).trimStart('0'))
                + (it.toHexString(HexFormat.UpperCase).trimStart('0'))
                + (it.toHexString(HexFormat.UpperCase).trimStart('0'))).toString().hexToInt(HexFormat.UpperCase)
    }.toIntArray()*/



}


@OptIn(ExperimentalStdlibApi::class)
fun test2(listTriple : IntArray, height: Int, width: Int): IntArray {

    Log.i("test", "inside of test2")

    val size = height*width

    val coloredImage = IntArray(size) //because we save it as ARGB_8888 so 32 bits like an Int

    for (i in 0 until size)
    {
        val redChannel = (listTriple[i] shr 16) and 0xFF //it makes 0000 RRRR 0000 0000 to 0000 0000 0000 RRRR to RRRR
        val greenChannel = (listTriple[i] shr 8) and 0xFF
        val blueChannel = (listTriple[i]) and 0xFF

        coloredImage[i] = (0xFF shl 24) or (redChannel shl 16) or (greenChannel shl 8) or (blueChannel shl 0) //getting back to 32 bits for one color

    }
    return coloredImage
}


fun vertsToList(
    segmentedGraph: MutableList<MutableList<Pair<Int, Int>>>,
    height: Int,
    width: Int
) : MutableList<MutableList<Triple<Int,Int,Int>>>
{
    val list = MutableList(height) { MutableList(width){ Triple(0,0,0) }}

    segmentedGraph.forEach {
        val color = Triple(Random.nextInt(10,250),Random.nextInt(10,250),Random.nextInt(10,250))
        it.forEach {
            list[it.first][it.second] = color
        } }

    return list
}

fun graphToList(graph : MutableList<MutableList<Edge>>,height : Int, width: Int) : MutableList<MutableList<Triple<Int,Int,Int>>>
{
    val list = MutableList(height) { MutableList(width){ Triple(0,0,0) }}

    graph.forEach {
        val color = Triple(Random.nextInt(10,250),Random.nextInt(10,250),Random.nextInt(10,250))
        it.forEach {
            list[it.v1.first][it.v1.second] = color
            list[it.v2.first][it.v2.second] = color
        } }

    return list
}


fun test2()
{
    val box1 = BoundingBox(20,10,40,40)
    val box2 = BoundingBox(30,20,80,60)
    println(intersectionOverUnion(box1, box2))
}



fun intersectionOverUnion(box1 : BoundingBox, box2 : BoundingBox) : Double
{
    val box1Area = box1.area()
    val box2Area = box2.area()
    val intersectionArea = box1.intersection(box2).area()
    val IOU : Double = (intersectionArea) / (box1Area + box2Area - intersectionArea).toDouble()
    return IOU
}







class BoundingBox(var x1 :Int, var y1 :Int, var x2 :Int, var y2 :Int) {
    fun intersection(other: BoundingBox): BoundingBox {
        return BoundingBox(
            x1 = max(x1,other.x1), //left top ig
            y1 = max(y1,other.y1),
            x2 = min(x2,other.x2), //right bottom ig
            y2 = min(y2,other.y2)
        )
    }
    fun area() : Int
    {
        return if(x2 < x1 || y2 < y1) 0
        else (x2-x1) * (y2-y1)
    }
}

data class Edge(
    val v1 : Pair<Int,Int>,
    val v2 : Pair<Int,Int>,
    val weight : Int

) {
    override fun toString(): String {
        return "v1=$v1, v2=$v2, weight=$weight"
    }
}

/*data class Vertice(
    val first : Pair<Int,Int>,
    val second : Pair<Int,Int>
)
{}*/


fun fastImageSegmentation(image : MutableList<MutableList<Int>>,k : Int): MutableList<MutableList<Pair<Int, Int>>> { //implemented with union find
    val height = image.size  //maybe the other way
    val width = image[0].size
//later might add ranking to make tree shallow and fasten thing even more
//    var components: List<Pair<MutableList<Pair<Int, Int>>,Int>> // it is a list of pairs containing list of vertices and a threshold number
    val edges = mutableListOf<Edge>()   //list of edges
    var parenthood = mutableListOf<Int>()  //list of parent indexes
    var sizes = mutableListOf<Int>()  //list 'families' sizes
    var thresholds = mutableListOf<Int>()  //list of thresholds
    println("starting segmentation")
    for (i in 0 until height)
    {
        for (j in 0 until width)
        {
            if(i<height - 1) edges.add(Edge(Pair(i,j),Pair(i+1,j), edgeWeight(image[i][j],image[i+1][j])))
            if(j<width - 1) edges.add(Edge(Pair(i,j),Pair(i,j+1), edgeWeight(image[i][j],image[i][j+1])))
        }
    }
    edges.sortBy { it.weight }
    for (i in 0 until height*width) {
        parenthood.add(i)
        thresholds.add(k)
        sizes.add(1)
    }
    for (i in 0 until edges.size) {
        var currentParent = edges[i].v1.first * width + edges[i].v1.second
        while (parenthood[currentParent] != currentParent) //finding function
        {
            currentParent = parenthood[currentParent]
        }
        val parent1 = currentParent

        currentParent = edges[i].v2.first * width + edges[i].v2.second
        while (parenthood[currentParent] != currentParent) //finding function
        {
            currentParent = parenthood[currentParent]
        }
        val parent2 = currentParent

        if (parent1==parent2) continue
        if(edges[i].weight > thresholds[parent1] || edges[i].weight > thresholds[parent2]) continue

        parenthood[parent1] = parent2
        sizes[parent2] += sizes[parent1]
        thresholds[parent2] = edges[i].weight + k/sizes[parent2]

    }

    val tempMap = mutableMapOf<Int,MutableList<Pair<Int,Int>>>()
    for (i in 0 until height)
    {
        for (j in 0 until width)
        {
            var currentParent = i * width + j
            while (parenthood[currentParent] != currentParent) //finding function
            {
                currentParent = parenthood[currentParent]
            }
            tempMap.getOrPut(currentParent) { mutableListOf() }.add(Pair(i,j))
        }
    }
    println("segmentation finished")
    Edges = edges
    return tempMap.values.toMutableList()
}

fun findEdges(
    vertices: MutableList<Pair<Int, Int>>,
    edges: MutableList<Edge>,

    ): MutableList<Edge> {
    return edges.filter {edge -> vertices.find { it == edge.v1 } != null  && vertices.find { it == edge.v2 }!= null  }.toMutableList()
}



fun MIntDiff(C1: MutableList<Edge>, C2: MutableList<Edge>, k: Int) : Int
{
//    println(tau(C1,k))
    return min(internalDifference(C1) + tau(C1,k),internalDifference(C2) + tau(C2,k))
}
fun internalDifference(C : MutableList<Edge>) : Int
{
    if(C.size == 1) return 0
    return minimumSpanningTree(C).maxBy { it.weight }.weight
}
fun tau(C : MutableList<Edge>, k: Int) : Int {
    return k / C.size
} //return should be Int?

fun edgeWeight(i1: Int, i2: Int): Int {
    return abs(i1-i2) //change that later ig
}
fun minimumSpanningTree(C: MutableList<Edge>) : MutableList<Edge>
{
    val visitedEdges = mutableListOf<Edge>()
    val D = C/*.map { mutableListOf(it) }*/.sortedBy { it/*[0]*/.weight }.toMutableList()
    val unvisitedVertices = (D.map { it.v1 } + D.map { it.v2 }).distinct().toMutableList()
    visitedEdges.add(D.first())
    unvisitedVertices.remove(D.first().v1)
    unvisitedVertices.remove(D.first().v2)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        D.removeFirst()
    }
    while(unvisitedVertices.isNotEmpty()) {
        val next1 = D.first { first -> visitedEdges.find { it.v1 == first.v1 || it.v1 == first.v2 || it.v2 == first.v1 || it.v2 == first.v2} != null && (unvisitedVertices.find { first.v1 == it } != null || unvisitedVertices.find { first.v2 == it } != null)}
        //println("next $next1     $unvisitedVertices")
        visitedEdges.add(next1)
        unvisitedVertices.remove(next1.v1)
        unvisitedVertices.remove(next1.v2)
        D.remove(next1)



    }
    return visitedEdges
}

fun similarityMerging(components : MutableList<MutableList<Pair<Int, Int>>>,image : List<List<Int>>,edges: MutableList<Edge>) : MutableList<MutableList<Pair<Int, Int>>>
{


    var neighbourList = mutableListOf<Pair<MutableList<Pair<Int, Int>>, MutableList<Pair<Int,Int>>>>()   // list containing all components and their neighbours(by first elements reference)
    var similarities = mutableListOf<Triple<Pair<Int, Int>,Pair<Int, Int>,Int>>()  //reference 1, reference 2, similarity

    for(j in 0 until components.size) {
        neighbourList.add(Pair(components[j], mutableListOf()))
    }
/*    edges.forEach { (v1, v2, weight) -> if(components.find {it.find { it == v1 } != null} != components.find {it.find { it == v2 } != null})
        neighbourList.find { t -> t.first[0] == components.find{it[0]==t.first[0]} }
        neighbourList[j].second.add(component[0])
        neighbourList[j].second.add(component[0])
    }*/

    /*    for(j in 0 until components.size)
        {
            neighbourList.add(Pair(components[j],mutableListOf()))
            components.forEachIndexed {index, component -> if(index!=j) {
                var bb = getBoundingBox((component + components[j]) as MutableList<Pair<Int, Int>>)
                var b1 = getBoundingBox(components[j])
                var b2 = getBoundingBox(component)
                if(bb.area() != 0 && bb.area() < b1.area() + b2.area())  //change neighbor condition
                {
                    neighbourList[j].second.add(component[0])
                }
                }
            }
        }*/
    var visitedSimilarities = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    neighbourList.forEach {nL ->
        nL.second.forEach {neighbour -> if(visitedSimilarities.find {sim -> nL.first[0]==sim.first && neighbour==sim.second || nL.first[0]==sim.second && neighbour==sim.first} == null)
        {
            similarities.add(Triple(nL.first[0],neighbour,calculateSimilarity(nL.first,neighbourList.first{it.first[0]==neighbour}.first    ,image)))
        } }
    }

    while(similarities.isNotEmpty())
    {
        var highestSimilarity = similarities.maxBy { it.third }
        var forMerge = neighbourList.find { it.first[0] == highestSimilarity.second }
        neighbourList.remove(forMerge)
        neighbourList.find { it.first[0] == highestSimilarity.first }?.first?.addAll(forMerge!!.first)
//        neighbourList.find { it.first[0] == highestSimilarity.first }?.second?.addAll(forMerge!!.second)
        similarities.removeIf { it.first == highestSimilarity.first || it.second == highestSimilarity.first || it.first == highestSimilarity.second || it.second == highestSimilarity.second }

        visitedSimilarities.removeAll(visitedSimilarities)
        neighbourList.forEach {nL ->
            if(nL.first[0]==highestSimilarity.first || nL.first[0]==highestSimilarity.second)
                nL.second.forEach {neighbour -> if(visitedSimilarities.find {sim -> nL.first[0]==sim.first && neighbour==sim.second || nL.first[0]==sim.second && neighbour==sim.first} == null)
                {
                    try {
                        similarities.add(Triple(nL.first[0],neighbour,calculateSimilarity(nL.first,neighbourList.first{it.first[0]==neighbour}.first    ,image)))
                    }
                    catch (e : Exception){}
                } }
        }


    }
    return neighbourList.map { it.first }.toMutableList() //temp ig
}


fun calculateSimilarity(
    component1: MutableList<Pair<Int, Int>>,
    component2: MutableList<Pair<Int, Int>>,
    image: List<List<Int>>
): Int {
    var similairty = 0.0

    //color similarity
    var histogram1 = MutableList<Int>(30) {0} //30 bins where each represents a brightness
    component1.forEach {histogram1[((image[it.first][it.second])/8.5).toInt()]++}
    var histogram2 = MutableList<Int>(30) {0}
    component2.forEach {histogram2[((image[it.first][it.second])/8.5).toInt()]++}

//    println(histogram1)

    histogram1.forEachIndexed { index, it ->   similairty += 10 * min(it/30,histogram2[index])/30}//  /30 - ?

    //texture similarity skip for now

    //size similarity
    similairty += 1 * (1 - ((component1.size + component2.size)/(image.size*image[0].size)))

    //fill similarity
    similairty += 1 - (((getBoundingBox((component1+component2) as MutableList<Pair<Int, Int>>).area()) - component1.size - component2.size)/(image.size*image[0].size))


    return similairty.toInt()
}


fun getBoundingBox(
    component: MutableList<Pair<Int, Int>>,
) : BoundingBox
{
    var maxBoundingBox = BoundingBox(100000,100000,-1,-1)//1 should be top left so it's first set opposite
    component.forEach {
        if (it.first < maxBoundingBox.x1) maxBoundingBox.x1 = it.first
        if (it.first > maxBoundingBox.x2) maxBoundingBox.x2 = it.first

        if (it.second < maxBoundingBox.y1) maxBoundingBox.y1 = it.second
        if (it.second > maxBoundingBox.y2) maxBoundingBox.y2 = it.second

    }
    return maxBoundingBox
}



