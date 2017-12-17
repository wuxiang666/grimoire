package grimoire.ml.statistics.conjure
/**
  * Created by Aron on 17-11-20.
  */
import grimoire.Implicits.jstr2JsValue
import grimoire.spark.globalSpark
import grimoire.spark.source.dataframe.HiveSource
import org.apache.spark.sql.SparkSession
import grimoire.Implicits._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.io.PrintWriter

import grimoire.ml.statistics.DataFrameMultivariateStatisticalSummarySpell
import grimoire.util.json.reader.JsonReaders

object StatisticalSummary {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("StatisticalSummary").
      enableHiveSupport().
      getOrCreate()

    val jsonObject = HiveSource(args(0)).
      cast(DataFrameMultivariateStatisticalSummarySpell(args(1))).conjure

    val conf = new Configuration()
    val fs= FileSystem.get(conf)
    val output = fs.create(new Path(JsonReaders.stringReader.read(args(2),"OutPutJsonTarget")))
    val writer = new PrintWriter(output)
    try{
      writer.write(jsonObject.toString())
      writer.write("\n")
    }finally {
      writer.close()
    }
  }
}