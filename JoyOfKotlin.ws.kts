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
