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
        println("Name: $name Date: $date")
    }
}

show(listOf(Chef("Val"), Chef("Piedro")))

// companion object is designed for static members
data class Fitter(val name: String, val registered: Instant = Instant.now()) {

    companion object {
        fun create(xml: String): Fitter {
            return Fitter(xml)
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

// visibility
// what is a module?
// used if visibility is internal
// it is a compilation unit, ie a maven project
// for example this would allow a unit test in the module
// access to internal members of a class

// nested (local) functions
// allow cleaner looking code
// by using ::isPrime function reference
// instead of embedding a closure
// remove the lambda and give a nice looking name instead
fun sumOfPrimes(limit: Int): Long {
    val seq: Sequence<Long> = sequenceOf(2L) +
            generateSequence(3L, {
                it + 2
            }).takeWhile { it < limit }

    fun isPrime(n: Long): Boolean =
        // you can access variable from outer function
        seq.takeWhile { it * it <= n }.all { n % it != 0L }

    return seq.filter ( ::isPrime ).sum()
}

// nulls
// safe call operator  ?.
fun couldBeNullOrNot() : String? {
    return null
}

val s: String? = couldBeNullOrNot()
val l = if (s != null) s.length else null
// or
val l1 = s?.length

// chaining
//val city: City? = map[somekey]?.manager?.address?.city

// elvis is get value or else ?:
//val city: City = map[company]?.manager?.address?.city ?: city.UNKNOWN

// when 2 ways to use it

val country = "Australia"
val capital = when (country) {
   "Australia" -> "Canberra"
    else -> "Unknown"
}

val tired = false
val biggestCity = when {
    tired -> "I dontknow"
    country == "Australia" -> "Sydney"
    else -> "unknown"
}


//ranges
val a = 0 until 10 step 2
val b = 0..10
val c = 10 downTo 3 step 2


// can use is in a when
// when (something) {
// is String -> "something"
// is Int -> "integer"

// safe syntax for casting as?
// val result = payload as? String

//generics remember there is use site variance
// eg out and in not at the type declaration but at the usage site
// ie in function parameter
// fun useBag(bag: Bag<in MyClass>): Boolean {
// return true
//}
// or in return type you can go : Bag<out MyClass>




