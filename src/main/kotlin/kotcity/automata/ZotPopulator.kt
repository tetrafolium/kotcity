package kotcity.automata

import kotcity.data.*
import kotcity.util.Debuggable

class ZotPopulator(val cityMap: CityMap) : Debuggable {
    override var debug: Boolean = false

    fun tick() {
        cityMap.eachLocation { location ->

            // we gotta SKIP roads...
            if (location.building !is Road) {
                val newZots = when (location.building::class) {
                    Residential::class -> updateResidential(location)
                    Commercial::class -> updateCommercial(location)
                    Industrial::class -> updateIndustrial(location)
                    else -> {
                        mutableListOf()
                    }
                }
                val finalZots = newZots.plus(genericZots(location))
                debug("Final zots for ${location.building} are $finalZots")
                location.building.zots = finalZots
            }
        }
    }

    private fun updateIndustrial(location: Location): List<Zot> {
        val building = location.building
        val zotList = mutableListOf<Zot>()

        if (building.totalBeingBought(Tradeable.LABOR) == 0) {
            zotList.add(Zot.NO_WORKERS)
        }

        return zotList
    }

    private fun updateCommercial(location: Location): List<Zot> {
        val building = location.building
        val zotList = mutableListOf<Zot>()

        if (building.totalBeingBought(Tradeable.LABOR) == 0) {
            zotList.add(Zot.NO_WORKERS)
        }

        if (!hasTrafficNearby(location, 5, 50)) {
            zotList.add(Zot.NO_CUSTOMERS)
        }

        return zotList
    }

    private fun genericZots(location: Location): MutableList<Zot> {
        val building = location.building
        val zotList = mutableListOf<Zot>()
        if (!building.powered) {
            zotList.add(Zot.NO_POWER)
        }
        return zotList
    }

    private fun updateResidential(location: Location): List<Zot> {

        val building = location.building
        val zotList = mutableListOf<Zot>()

        if (building.quantityOnHand(Tradeable.GOODS) <= 0) {
            zotList.add(Zot.NO_GOODS)
        }

        if (hasTrafficNearby(location, 2, 5000)) {
            zotList.add(Zot.TOO_MUCH_TRAFFIC)
        }

        return zotList
    }

    private fun hasTrafficNearby(location: Location, radius: Int, quantity: Int): Boolean {
        val neighboringBlocks = location.coordinate.neighbors(radius)
        val nearbyRoads = neighboringBlocks.flatMap { cityMap.cachedLocationsIn(it) }
                                                       .filter { it.building is Road }

        val trafficCount = nearbyRoads.sumBy { cityMap.trafficLayer[it.coordinate]?.toInt() ?: 0 }
        return trafficCount > quantity
    }
}