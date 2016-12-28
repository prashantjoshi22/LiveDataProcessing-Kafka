//Code to process the real time counting of words using Spark Streaming and Spark Core .
//By  Prashant Joshi

import java.util.HashMap
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

object Kafka_Consumer {
def main(args: Array[String]) {
// 4 command line argument will be passed through the terminal.
        if (args.length < 4) {
        System.err.println("Please pass the complete argument list")
        System.exit(1)
        }
//zkquorum = localhost:2181 ,group_id =0 ,topics = Reading_logs ,numThreads =2
        val Array(zkQuorum, group, topics, numThreads) = args
        //Array will contain zookeeper port number , group_id ,name of the created topics ,number of thread you want to launch to process the data.
        val sparkConf = new SparkConf().setMaster("local").setAppName("Kafka_fetching_logs")
        val ssc = new StreamingContext(sparkConf, Seconds(2))
        ssc.checkpoint("checkpoint")
        val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
        val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(data =>data._2)
        // A DSTREAM is created.
        //Here .map(data =>data._2) is used to extract 2nd value form DSTREAMS eg. (NULL,word)
        val word_split = lines.flatMap(data =>data.split(" "))
        val word_map = word_split.map(data =>(data ,1))
        val word_count = word_map.reduceByKey((accumulated_value,current_value)=>(accumulated_value+current_value))
        // This line will print all the words with its count in the terminal.
        word_count.print()
        //lines.saveAsTextFiles("file:///home/hduser/opp","text")
        // This will print the DSTREAM data in batches of 2 Second.   
        ssc.start()
        //This line will start the whole process.
        ssc.awaitTermination()
        }

        }
