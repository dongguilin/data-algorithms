package org.dataalgorithms.chap06.scala

import org.apache.spark.{Partitioner, SparkConf, SparkContext}

import scala.collection.mutable

/*
 * Moving Average in Scala sses "secondary sorting" via
 * repartitionAndSortWithinPartitions()
 *
 * One question on using scala.collection.mutable.Queue data structure
 * for the moving average solution: will this scale out? The answer is
 * yes, since the queue size is almost fixed and will not grow beyond
 * the “window” size, it will scale out: if even we use thousands or
 * 10’s of thousands of it.  If at the reducer level we use unbounded
 * data structures, then we are looking for OOM error: in our case,
 * it is well guarded by the window size.
 *
 *
 * @author Gaurav Bhardwaj (gauravbhardwajemail@gmail.com)
 *
 * @editor Mahmoud Parsian (mahmoud.parsian@yahoo.com)
 *
 */
object MovingAverage {

  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      println("Usage: MovingAverage <window> <number-of-partitions> <input-dir> <output-dir>")
      sys.exit(1)
    }

    val sparkConf = new SparkConf().setAppName("MovingAverage")
    val sc = new SparkContext(sparkConf)

    val window = args(0).toInt
    // number of partitions in secondary sorting, choose a high value
    val numPartitions = args(1).toInt
    val input = args(2)
    val output = args(3)

    val broadcastWindow = sc.broadcast(window)

    val rawData = sc.textFile(input)

    val valueToKey = rawData.map(line => {
      val tokens = line.split(",")
      val timestamp = tokens(1)
      (CompositeKey(tokens(0), timestamp), TimeSeriesData(timestamp, tokens(2).toDouble))
    })

    //Secondary sorting
    val sortedData = valueToKey.repartitionAndSortWithinPartitions(new CompositeKeyPartitioner(numPartitions))

    val keyValue = sortedData.map(k => (k._1.stockSymbol, (k._2)))

    val groupByStockSymbol = keyValue.groupByKey()

    val movingAverage = groupByStockSymbol.mapValues(values => {
      val queue = new mutable.Queue[Double]()
      for (timeseriesData <- values) yield {
        queue.enqueue(timeseriesData.closingStockPrice)
        if (queue.size > broadcastWindow.value) {
          queue.dequeue
        }
        (timeseriesData.timeStamp, (queue.sum / queue.size))
      }
    })

    //output will be in CSV format
    // <stock_symbol><,><date><,><moving_average>
    val formattedResult = movingAverage.flatMap(kv => {
      kv._2.map(v => (kv._1 + "," + v._1 + "," + v._2.toString))
    })

    formattedResult.saveAsTextFile(output)

    // done
    sc.stop()

  }

}

//Case class comes handy
case class CompositeKey(stockSymbol: String, timeStamp: String)

case class TimeSeriesData(timeStamp: String, closingStockPrice: Double)

//Define ordering
object CompositeKey {
  implicit def ordering[A <: CompositeKey]: Ordering[A] = {
    Ordering.by(fk => (fk.stockSymbol, fk.timeStamp))
  }
}

//---------------------------------------------------------
// the following class defines a custom partitioner by
// extending abstract class org.apache.spark.Partitioner
//---------------------------------------------------------
class CompositeKeyPartitioner(partitions: Int) extends Partitioner {
  require(partitions >= 0, s"Number of partitions ($partitions) cannot be negative.")

  override def numPartitions: Int = partitions

  override def getPartition(key: Any): Int = key match {
    case k: CompositeKey => math.abs(k.stockSymbol.hashCode % numPartitions)
    case null => 0
    case _ => math.abs(key.hashCode() % numPartitions)
  }

  override def equals(other: Any): Boolean = other match {
    case h: CompositeKeyPartitioner => h.numPartitions == numPartitions
    case _ => false
  }
}
