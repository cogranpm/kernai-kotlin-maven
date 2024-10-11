/* definition of a relationship where one entity has a reference to another
   a composition relationship, or one to one
   ui needs to have a way to allow user to set or unset the reference
   setting involves looking up all rows of the entity
   examples of this might be:
   a note has a note segment type
 */

package com.parinherm.form.definitions

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceDef(val entity: EntityDef)
