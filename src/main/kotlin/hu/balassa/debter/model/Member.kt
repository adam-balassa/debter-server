package hu.balassa.debter.model

import kotlin.properties.Delegates

class Member {
    lateinit var id: String
    lateinit var name: String
    var sum by Delegates.notNull<Double>()
    var debt by Delegates.notNull<Double>()
}