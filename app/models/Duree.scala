package models

import play.api.libs.json._

case class Duree(heures: Int,
                 minutes: Int) {
  lazy val total: Int = heures * 60 + minutes

  def add(duree: Duree): Duree = {
    val m = duree.minutes + minutes
    val h = duree.heures + heures

    Duree(m % 60, if (m % 60 == 1) h + 1 else h)
  }

  def div(nombre: Int): Duree = {
    if (nombre == 0) {
      return this
    }
    Duree(total / nombre)
  }
}

object Duree {
  def apply(): Duree = Duree(0, 0)

  def apply(minutes: Int): Duree = {
    val heures = minutes / 60
    Duree(heures, minutes - heures * 60)
  }

  implicit val formateurDuree: Format[Duree] = new Format[Duree] {
    override def writes(d: Duree): JsValue = JsString(d.heures + ":" + "%02d".format(d.minutes))

    override def reads(json: JsValue): JsResult[Duree] = json match {
      case JsString(duree) => try {
        val Array(heures, minutes) = duree.split(":").map(_.toInt)
        JsSuccess(Duree(heures, minutes))
      } catch {
        case _: Exception => JsError("Mauvais format de temps")
      }
      case _ => JsError("Temps attendu")
    }
  }
}