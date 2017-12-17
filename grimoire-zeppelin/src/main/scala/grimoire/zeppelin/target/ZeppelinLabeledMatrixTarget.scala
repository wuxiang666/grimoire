package grimoire.zeppelin.target

import grimoire.ml.linalg.LabeledMatrix
import grimoire.target.Target
import grimoire.zeppelin.Implicits._
import play.api.libs.json.JsValue
import grimoire.Implicits.jstr2JsValue
import grimoire.ml.configuration.param.HasThreshold

/**
  * Created by caphael on 2017/7/19.
  */
class ZeppelinLabeledMatrixTarget extends Target[LabeledMatrix] with HasThreshold{
  threshold.optional().default(0.8)

  override def transformImpl(dat: LabeledMatrix): Unit = {
    print(dat.setThreshold($(threshold)).toScript())
  }
}

object ZeppelinLabeledMatrixTarget{
  def apply(json: JsValue ="""{}"""): ZeppelinLabeledMatrixTarget = new ZeppelinLabeledMatrixTarget().parseJson(json)
}