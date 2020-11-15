import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
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

// each data class functions component1 to componentN are
// automatically defined accessing properties in order they are
// defined in the class

fun show(chefs: List<Chef>) {

    // desctructuring makes code clearer and less verbose
    for((name, date) in chefs){
        println("Name: $name Date: date")
    }
}

show(listOf(Chef("Val"), Chef("Piedro")))

// companion object is designed for static members
data class Fitter(val name: String, val registered: Instant = Instant.now()) {

    companion object {
        fun create(xml: String): Fitter {
            println("do it mon")
        }
    }
}

// to call static member
Fitter.create("blah blah")

//objects: ie singletons
//cannot have constructors
object MyWindowAdapter: WindowAdapter() {
    override fun windowClosed(e: WindowEvent?) {
        super.windowClosed(e)
    }
}

// collections
// listOf is a package level function
val readOnlyChefs = listOf(Chef("fred"))
val mutChefs = mutableListOf(Chef("nigel"))

