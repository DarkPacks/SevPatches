package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.Locale;

import static tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin.*;

public class PatchMacMouse extends Patch {
    public PatchMacMouse(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode grabMouse = AsmUtils.findMethod(this.classNode, GRAB_MOUSE_CURSOR);
        MethodNode ungrabMouse = AsmUtils.findMethod(this.classNode, UNGRAB_MOUSE_CURSOR);
        if (grabMouse == null || ungrabMouse == null) return false;
        grabMouse.instructions.insert(generateInsns(true));
        ungrabMouse.instructions.insert(generateInsns(false));

        return true;
    }

    @Override
    public byte[] apply() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (!osName.contains("mac")) {
            LOGGER.info("Skipping mouse patch; os is not macOS");
            return inputClassBytes;
        }
        return super.apply();
    }

    private InsnList generateInsns(boolean checkGrab) {
        LabelNode label = new LabelNode();
        InsnList insns = new InsnList();
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/input/Mouse", "isGrabbed", "()Z", false));
        insns.add(new JumpInsnNode(checkGrab ? Opcodes.IFEQ : Opcodes.IFNE, label));
        insns.add(new InsnNode(Opcodes.RETURN));
        insns.add(label);

        return insns;
    }
}
