package com.example.skysys.utils

import com.example.skysys.dataClass.missao
import io.realm.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject

class realmIOSky : RealmObject {

    private val config = RealmConfiguration.create(setOf(missao::class))
    private val realm = Realm.open(config)



    fun create(missaoRealm: missao)
    {
        realm.writeBlocking {
            realmIOSky().apply {
                val menagedMissao = copyToRealm(missaoRealm)
            }
        }
    }

    fun readByName(name : String) : RealmResults<missao>
    {
        val personsByNameQuery: RealmQuery<missao> = realm.query<missao>("nome = $0", name)
        val missaoResult: RealmResults<missao> = personsByNameQuery.find()

        return missaoResult
    }

    fun readById(id : Int) : missao?
    {
        var missao : missao? = realm.query<missao>("_id == $0", id.toString()).first().find()
        return missao
    }

    fun update(id : String,missaoUpdate: missao)
    {
        realm.writeBlocking{
            var missao : missao? = realm.query<missao>("_id == $0", id).first().find()

            missao = missaoUpdate
        }
    }

    fun delete(id : String)
    {
        realm.writeBlocking{
            var missao : missao? = this.query<missao>("_id == $0", id).first().find()
            if (missao != null) {
                delete(missao)
            }
        }
    }

    fun close()
    {
        realm.close()
    }
}