package grimoire.ml.evaluate.conjure
/**
  * Created by Aron on 17-11-20.
  */
import grimoire.Implicits.jstr2JsValue
import grimoire.spark.globalSpark
import grimoire.spark.source.dataframe.HiveSource
import org.apache.spark.sql.SparkSession
import grimoire.ml.classify.source._
import grimoire.ml.classify.transform._
import grimoire.ml.evaluate.{DataFrameBinaryClassificationMetricsSpell, DataFrameRegressionMetricsSpell}
import grimoire.ml.exception.ModelSourceException
import grimoire.Implicits._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.io.PrintWriter

import grimoire.util.json.reader.JsonReaders

object RegressionMetrics {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("RegressionMetrics").
      enableHiveSupport().
      getOrCreate()

    val metrics = args(0) match {
      case "DecisionTreeRegression" => {
        (HiveSource(args(1)) :+ DecisionTreeRegressionModelSource(args(2))).
          cast(DataFrameDecisionTreeRegressorPredictSpell()).
          cast(DataFrameRegressionMetricsSpell(args(3))).conjure
      }
      case "LinearRegression" => {
        (HiveSource(args(1)) :+ LinearRegressionModelSource(args(2))).
          cast(DataFrameLinearRegressionPredictSpell()).
          cast(DataFrameRegressionMetricsSpell(args(3))).conjure
      }
      case "RandomForestRegressor" => {
        (HiveSource(args(1)) :+ RandomForestRegressionModelSource(args(2))).
          cast(DataFrameRandomForestRegressorPredictSpell()).
          cast(DataFrameRegressionMetricsSpell(args(3))).conjure
      }
    }

    val conf = new Configuration()
    val fs= FileSystem.get(conf)
    val output = fs.create(new Path(JsonReaders.stringReader.read(args(4),"OutPutJsonTarget")))
    val writer = new PrintWriter(output)
    try{
      writer.write(metrics.toString())
      writer.write("\n")
    }finally {
      writer.close()
    }
  }
}