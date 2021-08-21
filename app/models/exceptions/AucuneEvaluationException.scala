package models.exceptions

case class AucuneEvaluationException(idJeu: Int) extends Throwable(s"Aucune évaluation disponible sur le jeu $idJeu")
