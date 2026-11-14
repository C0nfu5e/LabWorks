fun hellowrold(){
    println("Hello, world!!!")
}

fun computeGrade(scores: List<Double>, bonus: Double? = null):String {
    
    if (scores.isEmpty()){
        return "Нет данных"
    }
    
    var score:Double = 0.0
    
    for (i in scores) {
        score += i        
    }
    
    score = score/scores.size + (bonus ?: 0.0)
    
    return when {
        score >= 4.5 -> "Отлично" 
        score >= 3.5 -> "Хорошо" 
        score >= 2.5 -> "Удовлетворительно" 
        else -> "Неудовлетворительно" 
    }
}

fun formatResult(studentName:String, result:String, email:String? = null):String{
    return "${studentName}\n${result}\n${email?:"Email не указан"}"
}


fun main() {
    
    val numbers: List<Double> = listOf(3.0, 3.0, 3.0, 3.0, 3.0)
	val eMail = "alexey@g.com"
    
    println(formatResult("Алексей",computeGrade(numbers,10.0),eMail)+"\n")
    
    println(formatResult("Алексей",computeGrade(numbers),null)+"\n")
    
	val eNumbers = emptyList<Double>()
	println(formatResult("Алексей",computeGrade(eNumbers),eMail))
 
    
}
