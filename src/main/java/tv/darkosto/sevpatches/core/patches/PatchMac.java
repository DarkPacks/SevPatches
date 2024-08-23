package tv.darkosto.sevpatches.core.patches;

import java.util.Locale;

import static tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin.LOGGER;

public abstract class PatchMac extends Patch {
    public PatchMac(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    public byte[] apply() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (!osName.contains("mac")) {
            LOGGER.info("Not macOS, skipping {}", this.getClass().getName());
            return inputClassBytes;
        }
        return super.apply();
    }
}
