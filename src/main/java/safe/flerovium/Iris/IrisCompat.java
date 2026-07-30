package safe.flerovium.Iris;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.loader.api.FabricLoader;
import java.lang.reflect.Field;

public final class IrisCompat {
    private IrisCompat() {
    }

    public static final boolean IS_IRIS_INSTALLED = FabricLoader.getInstance().isModLoaded("iris");

    static VertexFormat GetEntityVertexFormat() {
        try {
            Class<?> irisVertexFormats = Class.forName("net.irisshaders.iris.vertices.IrisVertexFormats");
            Field field = irisVertexFormats.getDeclaredField("ENTITY");
            return (VertexFormat) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    static VertexFormat GetTerrainVertexFormat() {
        try {
            Class<?> irisVertexFormats = Class.forName("net.irisshaders.iris.vertices.IrisVertexFormats");
            Field field = irisVertexFormats.getDeclaredField("TERRAIN");
            return (VertexFormat) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
