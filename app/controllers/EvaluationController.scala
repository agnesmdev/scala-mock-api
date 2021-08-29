package controllers

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import models.exceptions._
import models.{Console, Evaluation, Jeu, Utilisateur}
import play.api.Logging
import play.api.libs.json._
import play.api.mvc._
import services.{TAuthentificationService, TEvaluationService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EvaluationController @Inject()(cc: ControllerComponents,
                                     authentificationService: TAuthentificationService,
                                     evaluationService: TEvaluationService)(implicit ec: ExecutionContext) extends AbstractController(cc) with Logging {

  def getEvaluationsUtilisateur(idUtilisateur: Int): Action[AnyContent] = authentification {
    Action.async {
      logger.info(s"Requête reçue pour récupérer les évaluations de l'utilisateur $idUtilisateur")

      evaluationService.getEvaluationsUtilisateur(idUtilisateur).map { evaluations =>
        logger.info(s"Requête en succès : ${evaluations.length} évaluations récupérées pour l'utilisateur $idUtilisateur")
        Ok(Json.toJson(evaluations))
      }.recover {
        case ex: UtilisateurInconnuException =>
          logger.warn(s"Requête en échec : ${ex.getMessage}")
          NotFound(ex.getMessage)
        case ex =>
          logger.error(s"Requête en échec : ${ex.getMessage}")
          InternalServerError(ex.getMessage)
      }
    }
  }

  def getMoyenneDureeJeu(idJeu: Int): Action[AnyContent] = authentification {
    Action.async {
      logger.info(s"Requête reçue pour récupérer la moyenne de durée du jeu $idJeu")

      evaluationService.getMoyenneDureeJeu(idJeu).map { duree =>
        logger.info(s"Requête en succès : durée moyenne de ${Json.toJson(duree)} récupérée pour le jeu $idJeu")
        Ok(Json.toJson(duree))
      }.recover {
        case ex: JeuInconnuException =>
          logger.warn(s"Requête en échec : ${ex.getMessage}")
          NotFound(ex.getMessage)
        case ex =>
          logger.error(s"Requête en échec : ${ex.getMessage}")
          InternalServerError(ex.getMessage)
      }
    }
  }

  def getEvaluationsJeu(idJeu: Int): Action[AnyContent] = authentification {
    Action.async {
      logger.info(s"Requête reçue pour récupérer les évaluations du jeu $idJeu")

      evaluationService.getEvaluationsJeu(idJeu).map { evaluations =>
        logger.info(s"Requête en succès : ${evaluations.length} évaluations récupérées pour le jeu $idJeu")
        Ok(Json.toJson(evaluations))
      }.recover {
        case ex: JeuInconnuException =>
          logger.warn(s"Requête en échec : ${ex.getMessage}")
          NotFound(ex.getMessage)
        case ex =>
          logger.error(s"Requête en échec : ${ex.getMessage}")
          InternalServerError(ex.getMessage)
      }
    }
  }

  def getJeuxConsole(console: String): Action[AnyContent] = authentification {
    Action.async {
      logger.info(s"Requête reçue pour récupérer les jeux de la console $console")

      Console.parse(console) match {
        case None =>
          logger.warn(s"Requête en échec : console $console inconnue")
          Future.successful(BadRequest(s"Console $console inconnue"))
        case Some(consoleM) => evaluationService.getJeuxConsole(consoleM).map { jeux =>
          logger.info(s"Requête en succès : ${jeux.length} jeux récupérés pour la console $console")
          Ok(Json.toJson(jeux))
        }.recover {
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def creerJeu: Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour créer un jeu")

      validerCorps[Jeu](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(jeu) => evaluationService.creerJeu(jeu).map { nouveauJeu =>
          logger.info(s"Requête en succès : jeu ${nouveauJeu.id} créé")
          Created(Json.toJson(nouveauJeu))
        }.recover {
          case ex: UtilisateurExistantException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            Conflict(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def creerUtilisateur: Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour créer un utilisateur")

      validerCorps[Utilisateur](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(utilisateur) => evaluationService.creerUtilisateur(utilisateur).map { nouveauUtilisateur =>
          logger.info(s"Requête en succès : utilisateur ${nouveauUtilisateur.id} créé")
          Created(Json.toJson(nouveauUtilisateur))
        }.recover {
          case ex: UtilisateurExistantException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            Conflict(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def creerEvaluation(idUtilisateur: Int, idJeu: Int): Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour créer une évaluation de l'utilisateur $idUtilisateur pour le jeu $idJeu")

      validerCorps[Evaluation](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(evaluation) => evaluationService.creerEvaluation(idUtilisateur, idJeu, evaluation).map { nouvelleEvaluation =>
          logger.info(s"Requête en succès : évaluation de l'utilisateur $idUtilisateur pour le jeu $idJeu créée")
          Created(Json.toJson(nouvelleEvaluation))
        }.recover {
          case ex@(_: UtilisateurInconnuException | _: JeuInconnuException) =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            NotFound(ex.getMessage)
          case ex: EvaluationExistanteException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            Conflict(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def modifierJeu(idJeu: Int): Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour modifier un jeu")

      validerCorps[Jeu](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(jeu) => evaluationService.modifierJeu(idJeu, jeu).map { nouveauJeu =>
          logger.info(s"Requête en succès : jeu ${nouveauJeu.id} modifié")
          Ok(Json.toJson(nouveauJeu))
        }.recover {
          case ex: JeuInconnuException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            NotFound(ex.getMessage)
          case ex: UtilisateurExistantException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            Conflict(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def modifierUtilisateur(idUtilisateur: Int): Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour modifier un utilisateur")

      validerCorps[Utilisateur](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(utilisateur) => evaluationService.modifierUtilisateur(idUtilisateur, utilisateur).map { nouveauUtilisateur =>
          logger.info(s"Requête en succès : utilisateur ${nouveauUtilisateur.id} modifié")
          Ok(Json.toJson(nouveauUtilisateur))
        }.recover {
          case ex: UtilisateurInconnuException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            NotFound(ex.getMessage)
          case ex: UtilisateurExistantException =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            Conflict(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  def modifierEvaluation(idUtilisateur: Int, idJeu: Int): Action[AnyContent] = authentification {
    Action.async { request =>
      logger.info(s"Requête reçue pour modifier une évaluation de l'utilisateur $idUtilisateur pour le jeu $idJeu")

      validerCorps[Evaluation](request) match {
        case Left(erreur) => Future.successful(BadRequest(erreur))
        case Right(evaluation) => evaluationService.modifierEvaluation(idUtilisateur, idJeu, evaluation).map { nouvelleEvaluation =>
          logger.info(s"Requête en succès : évaluation de l'utilisateur $idUtilisateur pour le jeu $idJeu modifiée")
          Ok(Json.toJson(nouvelleEvaluation))
        }.recover {
          case ex@(_: UtilisateurInconnuException | _: JeuInconnuException | _: EvaluationInconnueException) =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            NotFound(ex.getMessage)
          case ex =>
            logger.error(s"Requête en échec : ${ex.getMessage}")
            InternalServerError(ex.getMessage)
        }
      }
    }
  }

  private def validerCorps[A](request: Request[AnyContent])(implicit jsonFormat: Format[A]): Either[JsValue, A] = {
    request.body.asJson match {
      case None =>
        logger.warn(s"Requête en échec : corps au format JSON attendu")
        Left(JsString("JSON attendu"))
      case Some(json) => json.validate[A] match {
        case error: JsError =>
          logger.warn(s"Requête en échec : ${JsError.toJson(error)}")
          Left(JsError.toJson(error))
        case JsSuccess(a, _) => Right(a)
      }
    }
  }

  private def authentification[A](action: Action[A]): Action[A] = Action.async(action.parser) { request =>
    logger.info(s"Authentification pour la requête ${request.uri}")

    extractUserAndPassword(request) match {
      case Some((utilisateur, motDePasse)) =>
        if (!authentificationService.login(utilisateur, motDePasse)) {
          logger.warn(s"Authentification en échec : combinaison utilisateur/mot de passe incorrecte")
          Future.successful(Forbidden("Combinaison utilisateur/mot de passe incorrecte"))
        } else {
          logger.info(s"Authentification réussie : utilisateur $utilisateur connecté")
          action(request)
        }
      case None =>
        logger.warn(s"Authentification en échec : utilisateur et mot de passe obligatoires")
        Future.successful(Unauthorized("Utilisateur et mot de passe obligatoires"))
    }
  }

  private def extractUserAndPassword[A](request: Request[A]): Option[(String, String)] = {
    request.headers.get(AUTHORIZATION) match {
      case None => (request.headers.get("utilisateur"), request.headers.get("motDePasse")) match {
        case (Some(utilisateur), Some(motDePasse)) =>
          logger.info("Authentification via les headers")
          Some((utilisateur, motDePasse))
        case _ => None
      }
      case Some(authorization) =>
        logger.info("Authentification classique")
        val credentials = BasicHttpCredentials(authorization.replace("Basic ", ""))
        Some((credentials.username, credentials.password))
    }
  }
}
