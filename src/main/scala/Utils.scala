object Utils {
  def encodeQueryParam(param: String): String = {
    java.net.URLEncoder.encode(param, "UTF-8")
  }
}
