
object App extends App {

  // Il nous manque : mise ne cache dans un fichier (test d'abord au sein de la structure, sinon recherche du fichier si possible et enreghistrement au sein du dictionnaire, et enfin sinon on fait la requete HTTP, on en registre dans le dictionnaire et on enregistre le fichier
  //  Essayez d'écrire du code générique en privilégiant les valeurs immutables, les compréhensions et la programmation d'ordre supérieur.
  var actorId: Option[Int] = TmdbApi.findActorId("Christian", "Bale")

  private val emmaWatson = FullName("Emma", "Watson")
  private val danielRadcliffe = FullName("Daniel", "Radcliffe")
  private var collabs = TmdbApi.collaboration(emmaWatson, danielRadcliffe)
  private val movieId = 27205


  /**
   * Test with wrong API key
   */
  // TEST findActorId
  TmdbApi.setWrongApiKey()

  actorId = TmdbApi.findActorId("Christian", "Bale")

  // TEST findMovieId
  collabs = TmdbApi.collaboration(emmaWatson, danielRadcliffe)
  println()

  // TEST findActorMovies
  movies = TmdbApi.findActorMovies(-1)

  // TEST findMovieDirector
  director = TmdbApi.findMovieDirector(movieId)

  /**
   * Test with valid API key
   */

  TmdbApi.setValidApiKey()

  // TEST findActorId
  actorId = TmdbApi.findActorId("Christian", "Bale")

  // TEST findMovieId
  collabs = TmdbApi.collaboration(emmaWatson, danielRadcliffe)

  // TEST findActorMovies
  var movies = TmdbApi.findActorMovies(actorId.get)

  // TEST findMovieDirector
  var director = TmdbApi.findMovieDirector(movieId)

  /**
   * Test cache with valid API key
   * Look at if coverage is 100%
   */

  // TEST findActorId
  TmdbApi.findActorId("Christian", "Bale")

  // TEST findMovieId
  TmdbApi.collaboration(emmaWatson, danielRadcliffe)

  // TEST findActorMovies
  TmdbApi.findActorMovies(actorId.get)

  // TEST findMovieDirector
  TmdbApi.findMovieDirector(movieId)

  // TEST mostFrequentPairs
  Cache.findMostFrequentActorPairs()


}

