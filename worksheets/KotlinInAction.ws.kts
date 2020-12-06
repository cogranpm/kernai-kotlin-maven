println("hello")


//lambda with recievers
fun alphabet() = StringBuilder().apply{
    for(letter in 'A'..'Z'){
        append(letter)
    }
    append("zomezing else")
}.toString()

println(alphabet())
