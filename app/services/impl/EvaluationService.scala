package services.impl

import models.exceptions.{AucuneEvaluationException, EvaluationExistanteException, EvaluationInconnueException, JeuExistantException, JeuInconnuException, UtilisateurExistantException, UtilisateurInconnuException}
import models.{Console, Duree, Evaluation, Jeu, Utilisateur}
import play.api.Logging
import play.api.libs.json.{Format, Json}
import services.TEvaluationService

import javax.inject.Singleton
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.io.Source

@Singleton
class EvaluationService extends TEvaluationService with Logging {

  private val listeUtilisateur: ArrayBuffer[Utilisateur] = loadFichier[Utilisateur]("utilisateurs")
  private val listeJeu: ArrayBuffer[Jeu] = loadFichier[Jeu]("jeux")
  private val listeEvaluation: ArrayBuffer[Evaluation] = loadFichier[Evaluation]("evaluations")

  override def getEvaluationsUtilisateur(idUtilisateur: Int): Future[List[Evaluation]] = {
    logger.debug(s"Tentative de récupération des évaluations de l'utilisateur $idUtilisateur")

    listeUtilisateur.find(_.id == idUtilisateur) match {
      case None => Future.failed(UtilisateurInconnuException(idUtilisateur))
      case Some(_) => Future.successful(listeEvaluation.filter(_.idUtilisateur == idUtilisateur).toList)
    }
  }

  override def getEvaluationsJeu(idJeu: Int): Future[List[Evaluation]] = {
    logger.debug(s"Tentative de récupération des évaluations du jeu $idJeu")

    listeJeu.find(_.id == idJeu) match {
      case None => Future.failed(UtilisateurInconnuException(idJeu))
      case Some(_) => Future.successful(listeEvaluation.filter(_.idJeu == idJeu).toList)
    }
  }

  override def getJeuxConsole(console: Console): Future[List[Jeu]] = {
    logger.debug(s"Tentative de récupération des jeux de la console $console")

    Future.successful(listeJeu.filter(_.console == console).toList)
  }

  override def getMoyenneDureeJeu(idJeu: Int): Future[Duree] = {
    logger.debug(s"Tentative de récupération de la moyenne de durée du jeu $idJeu")

    listeJeu.find(_.id == idJeu) match {
      case None => Future.failed(JeuInconnuException(idJeu))
      case Some(_) =>
        val evaluations = listeEvaluation.filter(_.idJeu == idJeu).toList
        if (evaluations.isEmpty) {
          return Future.failed(AucuneEvaluationException(idJeu))
        }

        val dureeTotale = evaluations.foldRight(Duree()) {
          case (evaluation, duree) => duree.add(evaluation.tempsDeJeu)
        }
        Future.successful(dureeTotale.div(evaluations.length))
    }
  }

  override def creerUtilisateur(utilisateur: Utilisateur): Future[Utilisateur] = {
    logger.debug(s"Tentative de création de l'utilisateur ${utilisateur.nom}")

    listeUtilisateur.find(_.egal(utilisateur)) match {
      case Some(utilisateurExistant) => Future.failed(UtilisateurExistantException(utilisateurExistant.id))
      case None =>
        val nouvelUtilisateur = utilisateur.copy(id = listeUtilisateur.length + 1)
        listeUtilisateur += nouvelUtilisateur
        Future.successful(nouvelUtilisateur)
    }
  }

  override def creerJeu(jeu: Jeu): Future[Jeu] = {
    logger.debug(s"Tentative de création du jeu ${jeu.nom}")

    listeJeu.find(_.egal(jeu)) match {
      case Some(jeuExistant) => Future.failed(JeuExistantException(jeuExistant.id))
      case None =>
        val nouveauJeu = jeu.copy(id = listeJeu.length + 1)
        listeJeu += nouveauJeu
        Future.successful(nouveauJeu)
    }
  }

  override def creerEvaluation(idUtilisateur: Int, idJeu: Int, evaluation: Evaluation): Future[Evaluation] = {
    logger.debug(s"Tentative de création du evaluation du jeu $idJeu par l'utilisateur $idUtilisateur")

    listeUtilisateur.find(_.id == idUtilisateur) match {
      case None => Future.failed(UtilisateurInconnuException(idUtilisateur))
      case Some(_) => listeJeu.find(_.id == idJeu) match {
        case None => Future.failed(JeuInconnuException(idJeu))
        case Some(_) => listeEvaluation.find(_.egal(evaluation)) match {
          case Some(evaluationExistante) => Future.failed(EvaluationExistanteException(evaluationExistante))
          case None =>
            val nouvelleEvaluation = evaluation.copy(idUtilisateur = idUtilisateur, idJeu = idJeu)
            listeEvaluation += nouvelleEvaluation
            Future.successful(nouvelleEvaluation)
        }
      }
    }
  }

  override def modifierUtilisateur(idUtilisateur: Int, utilisateur: Utilisateur): Future[Utilisateur] = {
    logger.debug(s"Tentative de modification de l'utilisateur $idUtilisateur")

    listeUtilisateur.find(_.id == idUtilisateur) match {
      case None => Future.failed(UtilisateurInconnuException(idUtilisateur))
      case Some(ancienUtilisateur) =>
        val nouvelUtilisateur = utilisateur.copy(id = idUtilisateur)
        listeUtilisateur -= ancienUtilisateur
        listeUtilisateur += nouvelUtilisateur
        Future.successful(nouvelUtilisateur)
    }
  }

  override def modifierJeu(idJeu: Int, jeu: Jeu): Future[Jeu] = {
    logger.debug(s"Tentative de modification du jeu $idJeu")

    listeJeu.find(_.id == idJeu) match {
      case None => Future.failed(JeuInconnuException(idJeu))
      case Some(ancienJeu) =>
        val nouveauJeu = jeu.copy(id = idJeu)
        listeJeu -= ancienJeu
        listeJeu += nouveauJeu
        Future.successful(nouveauJeu)
    }
  }

  override def modifierEvaluation(idUtilisateur: Int, idJeu: Int, evaluation: Evaluation): Future[Evaluation] = {
    logger.debug(s"Tentative de modification de l'évaluation du jeu $idJeu par l'utilisateur $idUtilisateur")

    listeUtilisateur.find(_.id == idUtilisateur) match {
      case None => Future.failed(UtilisateurInconnuException(idUtilisateur))
      case Some(_) => listeJeu.find(_.id == idJeu) match {
        case None => Future.failed(JeuInconnuException(idJeu))
        case Some(_) => listeEvaluation.find(_.egal(evaluation)) match {
          case None => Future.failed(EvaluationInconnueException(idUtilisateur, idJeu))
          case Some(ancienneEvaluation) =>
            val nouvelleEvaluation = evaluation.copy(idUtilisateur = idUtilisateur, idJeu = idJeu)
            listeEvaluation -= ancienneEvaluation
            listeEvaluation += nouvelleEvaluation
            Future.successful(nouvelleEvaluation)
        }
      }
    }
  }

  private def loadFichier[T](nomFichier: String)(implicit jsonFormat: Format[T]): ArrayBuffer[T] = {
    logger.info(s"Chargement du fichier $nomFichier")
    val source = Source.fromFile(s"conf/$nomFichier.json")
    val contenu = source.getLines().mkString
    val elements = Json.parse(contenu).validate[List[T]].getOrElse(Nil)

    source.close()
    ArrayBuffer.from(elements)
  }
}
