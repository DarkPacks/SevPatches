package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

public class PatchJMapStagesStutter extends Patch {
    public PatchJMapStagesStutter(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode toggleMinimap = AsmUtils.findMethod(this.classNode, "toggleMinimap");
        if (toggleMinimap == null) return false;

        InsnList insns = new InsnList();
        LabelNode ln = new LabelNode();

        /*
        if (enable == this.uiManager.isMiniMapEnabled()) return;
         */
        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insns.add(new FieldInsnNode(Opcodes.GETFIELD, "net/darkhax/jmapstages/JMapPermissionHandler", "uiManager", "Ljourneymap/client/ui/UIManager;"));
        insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "journeymap/client/ui/UIManager", "isMiniMapEnabled", "()Z", false));
        insns.add(new VarInsnNode(Opcodes.ILOAD, 1));
        insns.add(new JumpInsnNode(Opcodes.IF_ICMPNE, ln));
        insns.add(new InsnNode(Opcodes.RETURN));
        insns.add(ln);

        toggleMinimap.instructions.insert(insns);

        return true;
    }
}
