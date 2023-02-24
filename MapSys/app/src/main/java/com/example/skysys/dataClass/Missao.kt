package com.example.skysys.dataClass

import com.mapbox.geojson.Point
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject


class missao : RealmObject
{
    var _id: ObjectId = ObjectId.create()
    var nome: String? = ""
    var altura_Voo: String? = ""
    var altura_Decolagem: String? = ""
    var velocidade: String? = ""
    var altura_RTH: String? = ""
    var distancia: String? = ""
    var tempo: String? = ""
    var coordenadas: Point? = Point.fromLngLat(0.0,0.0)
}