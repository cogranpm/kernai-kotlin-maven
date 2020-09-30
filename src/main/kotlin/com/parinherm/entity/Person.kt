/* is it possible to have DAO entity with jetbrains exposed
and also do the JFace databinding as well
Not sure how and if it is worthwhile just to avoid the mapping
code of the DSL stuff
 */

package com.parinherm.entity

import com.parinherm.entity.schema.BeanTests
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.properties.Delegates

class Person(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<Person>(BeanTests)
   /*
    var name: String by Delegates.observable(name, observer)
    var income: BigDecimal by Delegates.observable(income, observer)
    var height: Double by Delegates.observable(height, observer)
    var age: Int by Delegates.observable(age, observer)
    var enteredDate: LocalDate by Delegates.observable(enteredDate, observer)
    var country: String by Delegates.observable(country, observer)
    var deceased: Boolean by Delegates.observable(deceased, observer)

    */

}