package claro.cms.documentation.showcase

class WorldComponent extends Component {
  
  val prefix = "world"
  
  def continents = List(
    Continent("North America", Country("United States"), Country("Canada"), Country("Mexico")),
    Continent("South America", Country("Argentinia"), Country("Brazil"), Country("Chile"), Country("Colombia")),
    Continent("Asia"),
    Continent("Europe", Country("United Kingdom"), Country("France"), Country("Greece")),
    Continent("Africa"),
    Continent("Antarctica"),
    Continent("Australia", Country("Australia"))
  ).sort ((x, y) => x.name < y.name)
  
  bindings.append {
    case _ : WorldComponent => Map(
        "continents" -> continents -> "continent",
        "countries" -> continents.flatMap(_.countries) -> "country"
      )
    case continent : Continent => Map(
        "name" -> continent.name,
        "countries" -> continent.countries -> "country"
      )
    case country : Country => Map(
    	"name" -> country.name,
    	"continent" -> continents.find(_.countries.contains(country)) -> "continent"
      )
   }
}

case class Continent(name : String, private val countries0 : Country*) {
  val countries = countries0.toList.sort ((x, y) => x.name < y.name)
} 

case class Country(name : String) {} 