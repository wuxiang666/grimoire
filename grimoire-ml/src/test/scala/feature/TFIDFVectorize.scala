package feature
/**
  * Created by Aron on 17-11-6.
  */
import grimoire.ml.feature.source.IDFModelSource
import grimoire.ml.feature.transform.DataFrameTFIDFVectorizeSpell
import grimoire.spark.globalSpark
import grimoire.spark.source.dataframe.HiveSource
import grimoire.spark.target.DataFrameTarget
import org.apache.spark.sql.SparkSession
import grimoire.Implicits._

object TFIDFVectorize {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("TFIDFVectorize").
      master("local[1]").
      enableHiveSupport().
      getOrCreate()

    (HiveSource("""{"InputPath":"test2"}""") :+ IDFModelSource("""{"InputPath":"hdfs:///user/model/idf2"}""")).
      cast(DataFrameTFIDFVectorizeSpell("""{"InputCol":"tf", "OutputCol":"tfidf"}""")).
      cast(DataFrameTarget("""{"TableName":"TFIDFVectorize"}""")).conjure
  }
}
