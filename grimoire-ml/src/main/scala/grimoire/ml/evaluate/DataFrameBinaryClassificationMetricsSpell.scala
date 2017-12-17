package grimoire.ml.evaluate

import grimoire.ml.configuration.param.{HasLabelCol, HasNumBins, HasPredictionCol}
import grimoire.ml.evaluate.result.BinaryClassificationMetricsResult
import grimoire.transform.Spell
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.{DataFrame, Row}
import play.api.libs.json._
import grimoire.Implicits._

/**
  * Created by Arno on 17-7-14.
  */
class DataFrameBinaryClassificationMetricsSpell  extends Spell[DataFrame,JsObject]
  with HasLabelCol with  HasPredictionCol {

  override def setup(dat:DataFrame): Boolean = {
    super.setup(dat)
  }
  override def transformImpl(dat: DataFrame): JsObject = {
    val dt = dat.select($(labelCol),$(predictionCol)).rdd.map{
      case Row(a:Double,b:Double) =>
        (a,b)
    }
    val bcm = new BinaryClassificationMetrics(dt)


    Json.obj(
      "precisionByThreshold"->bcm.precisionByThreshold.collect.map(x=>List(x._1,x._2)),
      "recallByThreshold" -> bcm.recallByThreshold.collect.map(x=>List(x._1,x._2)),
      "roc"->bcm.roc.collect.map(x=>List(x._1,x._2)),
      "fMeasureByThreshold"->bcm.fMeasureByThreshold.collect.map(x=>List(x._1,x._2)),
      "areaUnderPR"->bcm.areaUnderPR,
      "areaUnderROC"->bcm.areaUnderROC,
      "thresholds"->bcm.thresholds.collect
    )
  }
}

object DataFrameBinaryClassificationMetricsSpell{
  def apply(json: JsValue="""{}"""): DataFrameBinaryClassificationMetricsSpell =
    new DataFrameBinaryClassificationMetricsSpell().parseJson(json)
}

