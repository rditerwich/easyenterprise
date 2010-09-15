package claro.cms.util

object Formatting {
  
  def formatMoney(amount : Double, currency : String) = {
    val sign = currency match {
        case "EUR" => <span class="money-sign">&euro;</span>
        case "GBP" => <span class="money-sign">£</span>
        case "USD" => <span class="money-sign">$</span>
        case _ => <span class="money-sign">&euro;</span>
      }  

    val whole : Int = (amount / 100.0).asInstanceOf[Int]
    val cents : Int = (amount / 100.0 - whole).asInstanceOf[Int]
    <span class="money">
      {sign } 
      <span class="money-space"></span>  
      <span class="money-whole">{String.format("%d", int2Integer(whole))}</span>  
      <span class="money-sep">,</span> 
      <span class="money-cents">{String.format("%02d", int2Integer(cents))}</span>
    </span>
  } 
}