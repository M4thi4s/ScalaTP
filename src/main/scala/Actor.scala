case class Actor(id: Int, name: FullName) {
  override def toString: String = s"$id $name"
}

case class FullName(firstname: String, lastname: String) {
  override def toString: String = s"$firstname $lastname"
}
