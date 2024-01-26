import java.io.{File, FileNotFoundException, PrintWriter}
import scala.io.Source
import scala.util.{Failure, Success, Try}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import scala.collection.mutable

case class IntWrapper(value: Int)

object Cache {
  final private var actorCache: Map[(String, String), Int] = Map()
  final private var actorMovieCache: Map[Int, Set[(Int, String)]] = Map()
  final private var movieDirectorCache: Map[Int, (Int, String)] = Map()
  final private var collaborationCache: Map[(FullName, FullName), Set[(String, String)]] = Map()

  private val cacheDir = "Datas"
  private val actorDir = s"$cacheDir/actor"
  private val actorMovieDir = s"$cacheDir/actorMovie"
  private val movieDirectorDir = s"$cacheDir/movieDirector"
  private val collaborationDir = s"$cacheDir/collaboration"

  // Initialize directories
  createDirectories()

  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  private def createDirectories(): Unit = {
    val rootDirectory = new File(cacheDir)
    if (!rootDirectory.exists()) rootDirectory.mkdir()

    Seq(actorDir, actorMovieDir, movieDirectorDir, collaborationDir).foreach { dir =>
      val directory = new File(dir)
      if (!directory.exists()) directory.mkdirs()
    }
  }

  private def readCacheFromFile[T](filePath: String)(implicit manifest: Manifest[T]): Option[T] = {
    Try {
      val source = Source.fromFile(filePath)
      val data = source.mkString
      source.close()
      read[T](data)
    } match {
      case Success(result) => Some(result)
      case Failure(_: FileNotFoundException) => None
      case Failure(e) => throw e
    }
  }

  private def writeCacheToFile[T <: AnyRef](filePath: String, data: T): Unit = {
    val json = write(data)
    val writer = new PrintWriter(new File(filePath))
    try writer.write(json) finally writer.close()
  }

  def getCollaborationCache(actor1: FullName, actor2: FullName): Option[Set[(String, String)]] = {
    collaborationCache.get((actor1, actor2))
      .orElse(readCacheFromFile[Set[(String, String)]](s"$cacheDir/collaboration/${actor1}_${actor2}.json"))
  }

  def cacheCollaboration(actor1: FullName, actor2: FullName, movies: Set[(String, String)]): Unit = {
    collaborationCache += ((actor1, actor2) -> movies)
    writeCacheToFile(s"$cacheDir/collaboration/${actor1}_${actor2}.json", movies)
  }

  def getCachedActorMovieData(actorId: Int): Option[Set[(Int, String)]] = {
    actorMovieCache.get(actorId)
      .orElse(readCacheFromFile[Set[(Int, String)]](s"$cacheDir/actorMovie/$actorId.json"))
  }

  def cacheActorMovieData(actorId: Int, data: Set[(Int, String)]): Unit = {
    actorMovieCache += (actorId -> data)
    writeCacheToFile(s"$cacheDir/actorMovie/$actorId.json", data)
  }

  def getCachedActorId(firstName: String, lastName: String): Option[Int] = {
    actorCache.get((firstName, lastName))
      .orElse(readCacheFromFile[IntWrapper](s"$cacheDir/actor/${firstName}_$lastName.json").map(_.value))
  }

  def cacheActorId(firstName: String, lastName: String, id: Int): Unit = {
    actorCache += ((firstName, lastName) -> id)
    writeCacheToFile(s"$cacheDir/actor/${firstName}_$lastName.json", IntWrapper(id))
  }
  def getCachedMovieData(movieId: Int): Option[(Int, String)] = {
    movieDirectorCache.get(movieId)
      .orElse(readCacheFromFile[(Int, String)](s"$cacheDir/movieDirector/$movieId.json"))
  }

  def cacheMovieData(movieId: Int, data: (Int, String)): Unit = {
    movieDirectorCache += (movieId -> data)
    writeCacheToFile(s"$cacheDir/movieDirector/$movieId.json", data)
  }

  def clearCache(): Unit = {
    actorCache = Map()
    actorMovieCache = Map()
    movieDirectorCache = Map()
    collaborationCache = Map()
  }

  def findMostFrequentActorPairs(): Unit = {
    println("Calcul des paires d'acteurs les plus fréquentes...")
    val pairCounts = mutable.Map[(Int, Int), Int]().withDefaultValue(0)

    for {
      (actorId1, movies1) <- actorMovieCache
      (actorId2, movies2) <- actorMovieCache if actorId1 < actorId2
    } {
      val commonMovies = movies1.map(_._1).intersect(movies2.map(_._1))
      if (commonMovies.nonEmpty) {
        pairCounts((actorId1, actorId2)) += commonMovies.size
      }
    }

    val mostFrequentPairs = pairCounts.toSeq.sortBy(-_._2).take(10) // Prendre les 10 paires les plus fréquentes
    mostFrequentPairs.foreach { case ((actorId1, actorId2), count) =>
      println(s"Acteurs $actorId1 et $actorId2 ont joué ensemble dans $count films.")
    }
  }
}
