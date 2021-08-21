package models.exceptions

import models.Evaluation

case class EvaluationExistanteException(evaluation: Evaluation)
  extends Throwable(s"Évaluation déjà existante de l'utilisateur ${evaluation.idUtilisateur} pour le jeu ${evaluation.idJeu}")
