package services

import com.google.inject.ImplementedBy
import models.{Console, Duree, Evaluation, Jeu, Utilisateur}
import services.impl.EvaluationService

import scala.concurrent.Future

@ImplementedBy(classOf[EvaluationService])
trait TEvaluationService {

  def getEvaluationsUtilisateur(idUtilisateur: Int): Future[List[Evaluation]]

  def getEvaluationsJeu(idJeu: Int): Future[List[Evaluation]]

  def getJeuxConsole(console: Console): Future[List[Jeu]]

  def getMoyenneDureeJeu(idJeu: Int): Future[Duree]

  def creerUtilisateur(utilisateur: Utilisateur): Future[Utilisateur]

  def creerJeu(jeu: Jeu): Future[Jeu]

  def creerEvaluation(idUtilisateur: Int, idJeu: Int, evaluation: Evaluation): Future[Evaluation]

  def modifierUtilisateur(idUtilisateur: Int, utilisateur: Utilisateur): Future[Utilisateur]

  def modifierJeu(idJeu: Int, jeu: Jeu): Future[Jeu]

  def modifierEvaluation(idUtilisateur: Int, idJeu: Int, evaluation: Evaluation): Future[Evaluation]
}
