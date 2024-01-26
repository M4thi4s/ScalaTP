import java.net.URLEncoder
import scala.io.Source
import scala.util.{Failure, Success, Try}
import org.json4s.*
import org.json4s.native.JsonMethods.*
import org.json4s.native.Serialization

object TmdbApi {
  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)

  private val apiVersion = 3
  private val baseUrl = s"https://api.themoviedb.org/$apiVersion"
  private var apiKey = ""

  def setWrongApiKey(): Unit = {
    apiKey = "wrong"
  }

  def setValidApiKey(): Unit = {
    apiKey = "77a9a47fa5eccdd522d119af839587a1"
  }


  def findActorId(name: String, surname: String): Option[Int] = {
    val cacheVal = Cache.getCachedActorId(name, surname)
    if(cacheVal.isDefined) {
      return cacheVal
    }

    val fullName = s"$name $surname"
    val encodedFullName = URLEncoder.encode(fullName, "UTF-8")
    
    val url = s"$baseUrl/search/person?query=$encodedFullName&include_adult=false&language=en-US&page=1&api_key=$apiKey"
    
    val source = Try(Source.fromURL(url))

    source match {
      case Success(src) =>
        val contents = src.mkString
        src.close()

        val ret = (parse(contents) \ "results").extract[List[JValue]]
        .find (result => (result \ "id").extractOpt[Int].isDefined)
        .flatMap (result => (result \ "id").extractOpt[Int])

        //set in cache
        if(ret.isDefined)
          Cache.cacheActorId(name, surname, ret.get)

        ret

      case Failure(exception) =>
        println(s"Erreur lors de la requête : ${exception.getMessage}")
        None
    }
  }

  def findActorMovies(actorId: Int): Set[(Int, String)] = {
    val cacheVal = Cache.getCachedActorMovieData(actorId)
    if(cacheVal.isDefined) {
      return cacheVal.get
    }

    val url = s"$baseUrl/discover/movie?with_cast=$actorId&api_key=$apiKey"

    val source = Try(Source.fromURL(url))

    source match {
      case Success(src) =>
        val contents = src.mkString
        src.close()

        val json = parse(contents)
        val results = (json \ "results").extract[List[JValue]]

        val ret = results.flatMap { result =>
          val movieId = (result \ "id").extractOpt[Int]
          val title = (result \ "title").extractOpt[String]
          for {
            id <- movieId
            t <- title
          } yield (id, t)
        }.toSet

        //set in cache
        Cache.cacheActorMovieData(actorId, ret)

        ret

      case Failure(exception) =>
        println(s"Error during the request: ${exception.getMessage}")
        Set()
    }
  }

  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val cacheVal = Cache.getCachedMovieData(movieId)
    if(cacheVal.isDefined) {
      return cacheVal
    }

    val url = s"$baseUrl/movie/$movieId/credits?api_key=$apiKey"

    val source = Try(Source.fromURL(url))

    source match {
      case Success(src) =>
        val contents = src.mkString
        src.close()

        val json = parse(contents)
        val crew = (json \ "crew").extract[List[JValue]]

        // Find the director in the crew list
        val director = crew.find { member =>
          (member \ "job").extractOpt[String].contains("Director")
        }

        val ret = director.flatMap { dir =>
          val directorId = (dir \ "id").extractOpt[Int]
          val directorName = (dir \ "name").extractOpt[String]
          for {
            id <- directorId
            name <- directorName
          } yield (id, name)
        }

        //set in cache
        if(ret.isDefined)
          Cache.cacheMovieData(movieId, ret.get)

        ret

      case Failure(exception) =>
        println(s"Error during the request: ${exception.getMessage}")
        None
    }
  }

  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    val cacheVal = Cache.getCollaborationCache(actor1, actor2)
    if(cacheVal.isDefined) {
      return cacheVal.get
    }

    val actor1Id = findActorId(actor1.firstname, actor1.lastname)
    val actor2Id = findActorId(actor2.firstname, actor2.lastname)

    (actor1Id, actor2Id) match {
      case (Some(id1), Some(id2)) =>
        val actor1Movies = findActorMovies(id1)
        val actor2Movies = findActorMovies(id2)

        val ret = actor1Movies.intersect(actor2Movies).flatMap { case (movieId, title) =>
          val director = findMovieDirector(movieId)
          director match {
            case Some((_, directorName)) =>
              Some((title, directorName))
            case None =>
              println(s"Director not found for movie $title.")
              None
          }
        }

        //set in cache
        Cache.cacheCollaboration(actor1, actor2, ret)

        ret

      case _ =>
        println("One or both actors not found.")
        Set()
    }
  }
}