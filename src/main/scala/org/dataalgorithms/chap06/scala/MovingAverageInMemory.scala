package org.dataalgorithms.chap06.scala

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
  * Moving Avergae in Scala using Sort In Memory
  *
  * This example is analogous of to the SortInMemory_MovingAverageDriver
  * (https://github.com/mahmoudparsian/data-algorithms-book/blob/master/src/main/java/org/dataalgorithms/chap06/memorysort/SortInMemory_MovingAverageDriver.java)
  *
  * Please note that this soultion will not scale with large data
  * (if you cluster nodes have a limited amout of memory/RAM for
  * sorting).  For large datasets use MovingAverage.scala which
  * uses secondary sorting via repartitionAndSortWithinPartitions().
  *
  * @author Gaurav Bhardwaj (gauravbhardwajemail@gmail.com)
  *
  *
  */
object MovingAverageInMemory {

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage: MovingAverageInMemory <window> <input-dir> <output-dir>")
      sys.exit(1)
    }

    val sparkConf = new SparkConf().setAppName("MovingAverageInMemory")
    val sc = new SparkContext(sparkConf)

    val window = args(0).toInt
    val input = args(1)
    val output = args(2)

    val broadcastWindow = sc.broadcast(window)

    val rawData = sc.textFile(input)
    val keyValue = rawData.map(line => {
      val tokens = line.split(",")
      (tokens(0), (tokens(1), tokens(2).toDouble))
    })

    // Key being stock symbol like IBM, GOOG, AAPL, etc
    val groupByStockSymbol = keyValue.groupByKey()
    val result = groupByStockSymbol.mapValues(values => {
      // in-memory sorting, will not scale with large datasets
      val sortedValues = values.toSeq.sortBy(_._1)
      val queue = new mutable.Queue[Double]()
      for (tup <- sortedValues) yield {
        queue.enqueue(tup._2)
        if (queue.size > broadcastWindow.value) {
          queue.dequeue
        }
        (tup._1, (queue.sum / queue.size))
      }
    })

    // output will be in CSV format
    // <stock_symbol><,><date><,><moving_average>
    val formattedResult = result.flatMap(kv => {
      kv._2.map(v => (kv._1 + "," + v._1 + "," + v._2.toString))
    })
    formattedResult.saveAsTextFile(output)

    //done
    sc.stop()

  }

}
