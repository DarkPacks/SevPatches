package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.Optional;

public class PatchGalacticraftInventories extends Patch {
    public PatchGalacticraftInventories(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected byte[] writeClass() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    @Override
    protected boolean patch() {
        Optional<MethodNode> optIsEmpty = this.classNode.methods.stream()
                .filter((m) -> m.name.equals(SevPatchesLoadingPlugin.IS_EMPTY))
                .findAny();

        if (!optIsEmpty.isPresent()) return false;

        MethodNode isEmpty = optIsEmpty.get();

        LabelNode afterNull = new LabelNode();

        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "micdoodle8/mods/galacticraft/core/tile/TileEntityInventory",
                "getInventory",
                "()Lnet/minecraft/util/NonNullList;",
                false
        ));
        insnList.add(new InsnNode(Opcodes.ACONST_NULL));
        insnList.add(new JumpInsnNode(Opcodes.IF_ACMPNE, afterNull));
        insnList.add(new InsnNode(Opcodes.ICONST_0));
        insnList.add(new InsnNode(Opcodes.IRETURN));
        insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        insnList.add(afterNull);
        isEmpty.instructions.insert(insnList);

        return true;
    }
}
