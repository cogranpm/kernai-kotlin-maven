import java.time.Instant

// explicit constructor
// public, final by default
// open is opposite of final
// private, protected, internal
// internal means "module" in which it's declared
class Person constructor(name: String) {

    val name: String = name

}

val p = Person("fred")
println(p.name)

// override constructor
class Magician(val name: String, val registered: Instant = Instant.now()){

    constructor() : this("fred"){

    }
}

//private constructor
class Judge private constructor(val name: String)


// destructuring data objects
data class Chef(val name: String, val registered: Instant = Instant.now())


fun show(chefs: List<Chef>) {
    for((name, date) in chefs){
        println("Name: $name Date: date")
    }
}

show(listOf(Chef("Val"), Chef("Piedro")))