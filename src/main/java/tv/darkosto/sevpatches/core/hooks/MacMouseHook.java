package tv.darkosto.sevpatches.core.hooks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MacMouseHook {
    public static void setGrabbed(boolean grab) {
        if (grab) {
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
        } else {
            Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
        }
    }
}
