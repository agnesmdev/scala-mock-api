package models.exceptions

case class AucuneEvaluationException(idJeu: Int) extends Throwable(s"Aucune √©valuation disponible sur le jeu $idJeu")
