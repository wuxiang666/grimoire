package grimoire.ml.feature.conjure
/**
  * Created by Aron on 17-11-20.
  */
import grimoire.Implicits.jstr2JsValue
import grimoire.ml.feature.transform.DataFrameMinMaxScalerSpell
import grimoire.spark.globalSpark
import grimoire.spark.source.dataframe.HiveSource
import grimoire.spark.target.DataFrameTarget
import org.apache.spark.sql.SparkSession
import grimoire.Implicits._

object MinMaxScaler {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("MinMaxScaler").
      enableHiveSupport().
      getOrCreate()

    HiveSource(args(0)).
      cast(DataFrameMinMaxScalerSpell(args(1))).
      cast(DataFrameTarget(args(2))).
      conjure
  }
}
