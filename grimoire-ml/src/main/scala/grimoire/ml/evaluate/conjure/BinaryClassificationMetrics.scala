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
import grimoire.ml.evaluate.DataFrameBinaryClassificationMetricsSpell
import grimoire.ml.exception.ModelSourceException
import grimoire.Implicits._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.io.PrintWriter

import grimoire.util.json.reader.JsonReaders


object BinaryClassificationMetrics {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("BinaryClassificationMetrics").
      enableHiveSupport().
      getOrCreate()

    val metrics = args(0) match {
      case "DecisionTree" => {
        (HiveSource(args(1)) :+ DecisionTreeModelSource(args(2))).
          cast(DataFrameDecisionTreeClassifierPredictSpell()).
          cast(DataFrameBinaryClassificationMetricsSpell(args(3))).conjure
      }
      case "LogisticRegression" => {
        (HiveSource(args(1)) :+ LogisticRegressionModelSource(args(2))).
          cast(DataFrameLogisticRegressionPredictSpell()).
          cast(DataFrameBinaryClassificationMetricsSpell(args(3))).conjure
      }
      case "RandomForest" => {
        (HiveSource(args(1)) :+ RandomForestModelSource(args(2))).
          cast(DataFrameRandomForestClassifierPredictSpell()).
          cast(DataFrameBinaryClassificationMetricsSpell(args(3))).conjure
      }
      case "GBTC" => {
        (HiveSource(args(1)) :+ GDBCModelSource(args(2))).
          cast(DataFrameGBTClassifierPredictSpell()).
          cast(DataFrameBinaryClassificationMetricsSpell(args(3))).conjure
      }
      case "NaiveBayes" => {
        (HiveSource(args(1)) :+ NaiveBayesModelSource(args(2))).
          cast(DataFrameNaiveBayesPredictSpell()).
          cast(DataFrameBinaryClassificationMetricsSpell(args(3))).conjure
      }
      case _ => throw ModelSourceException("error in model params.please check!")
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
