package models.exceptions

case class EvaluationInconnueException(idUtilisateur: Int, idJeu: Int)
  extends Throwable(s"Aucune évaluation de l'utilisateur $idUtilisateur pour le jeu $idJeu")
