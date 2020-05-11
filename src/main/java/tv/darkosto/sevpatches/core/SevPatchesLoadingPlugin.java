package tv.darkosto.sevpatches.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("SevPatches")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SevPatchesLoadingPlugin implements IFMLLoadingPlugin {
    public static Logger LOGGER = LogManager.getLogger("sevpatches_core");

    public static String GET_SHORT;
    public static String SET_SHORT;

    public static String GET_INT;
    public static String SET_INT;

    public SevPatchesLoadingPlugin() {
        LOGGER.info("setting up mixin environment");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.sevpatches.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"tv.darkosto.sevpatches.core.SevPatchesTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        boolean dev = !(boolean) data.get("runtimeDeobfuscationEnabled");
        SevPatchesLoadingPlugin.GET_SHORT = dev ? "getShort" : "func_74765_d";
        SevPatchesLoadingPlugin.SET_SHORT = dev ? "setShort" : "func_74777_a";

        SevPatchesLoadingPlugin.GET_INT = dev ? "getInteger" : "func_74762_e";
        SevPatchesLoadingPlugin.SET_INT = dev ? "setInteger" : "func_74768_a";
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
