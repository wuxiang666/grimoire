package feature
/**
  * Created by Aron on 17-6-28.
  */
import grimoire.Implicits.jstr2JsValue
import grimoire.ml.feature.transform.IDFTrainSpell
import grimoire.spark.globalSpark
import grimoire.spark.source.dataframe.HiveSource
import org.apache.spark.sql.SparkSession
import grimoire.Implicits._
import grimoire.ml.target.ModelTarget

object IDFTrain {
  def main(args: Array[String]): Unit = {
    globalSpark = SparkSession.
      builder().
      appName("IDFTrain").
      master("local[1]").
      enableHiveSupport().
      getOrCreate()

    HiveSource("""{"InputPath":"test2"}""").
      cast(IDFTrainSpell("""{"InputCol":"tf"}""")).
      cast(ModelTarget("""{"OutputPath":"hdfs:///user/model/idf2","Overwrite":true}""")).
      conjure
  }
}
