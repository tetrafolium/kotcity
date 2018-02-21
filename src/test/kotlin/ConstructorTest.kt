import kotcity.data.*
import kotcity.data.assets.AssetManager
import org.junit.jupiter.api.Test

class ConstructorTest {
    @Test
    fun constructorTest() {
        val flatMap = CityMap.flatMap(128, 128)
        val assetManager = AssetManager(flatMap)
        val all = assetManager.all()
        assert(all.count() > 0)
        val industrial = all.filter { it.type == BuildingType.INDUSTRIAL }
        assert(industrial.count() > 0)
        industrial.forEach { assert(it.sprite != null && it.sprite != "") }
    }
}