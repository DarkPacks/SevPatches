package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin.*;

public class PatchInfoAccCompass extends Patch {
    public PatchInfoAccCompass(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode addDirectionInfo = AsmUtils.findMethod(this.classNode, "addDirectionInfo");
        if (addDirectionInfo == null) return false;
        InsnList insnlist = addDirectionInfo.instructions;

        Iterable<AbstractInsnNode> insnsIter = insnlist::iterator;
        Stream<AbstractInsnNode> insns = StreamSupport.stream(insnsIter.spliterator(), false);

        // - int yaw = MathHelper.floor(player.rotationYaw % 360.0);
        List<AbstractInsnNode> labels = insns.filter(insn -> insn instanceof LabelNode).collect(Collectors.toList());
        int startIndex = insnlist.indexOf(labels.get(0)) + 2;
        int endIndex = insnlist.indexOf(labels.get(1));

        for (int i = startIndex; i < endIndex; i++) {
            AbstractInsnNode insn = insnlist.get(startIndex);
            insnlist.remove(insn);
        }

        // + int yaw = MathHelper.floor(MathHelper.positiveModulo(player.rotationYaw));
        InsnList modYaw = new InsnList();
        modYaw.add(new VarInsnNode(Opcodes.ALOAD, 1));
        modYaw.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/player/EntityPlayer", ENTITY_ROTATION_YAW, "F"));
        modYaw.add(new LdcInsnNode(360.0f));
        modYaw.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/math/MathHelper", POSITIVE_MODULO, "(FF)F", false));
        modYaw.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/math/MathHelper", FLOOR, "(F)I", false));
        modYaw.add(new VarInsnNode(Opcodes.ISTORE, 3));

        insnlist.insert(labels.get(0), modYaw);

        return true;
    }
}
