case class Actor(id: Int, name: FullName) {
  override def toString: String = s"$id $name"
}

object Actor {
  def apply(id: Int, firstname: String, lastname: String): Actor = new Actor(id, FullName(firstname, lastname))
}

case class FullName(firstname: String, lastname: String) {
  override def toString: String = s"$firstname $lastname"
}

object FullName {
  def apply(firstname: String, lastname: String): FullName = new FullName(firstname, lastname)
}