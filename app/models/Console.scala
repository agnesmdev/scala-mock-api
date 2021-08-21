package models

import play.api.libs.json._

sealed trait Console

case object Switch extends Console
case object PSVita extends Console
case object XBox360 extends Console
case object PC extends Console

object Console {
  def parse(value: String): Option[Console] = value.toUpperCase match {
    case "SWITCH" => Some(Switch)
    case "PSVITA" => Some(PSVita)
    case "XBOX360" => Some(XBox360)
    case "PC" => Some(PC)
    case _ => None
  }

  implicit val formateurConsole: Format[Console] = new Format[Console] {
    override def writes(c: Console): JsValue = JsString(c.toString)

    override def reads(value: JsValue): JsResult[Console] = value match {
      case JsString(console) => Console.parse(console) match {
        case Some(console) => JsSuccess(console)
        case None => JsError("Console inconnue")
      }
      case _ => JsError("Console attendue")
    }
  }
}