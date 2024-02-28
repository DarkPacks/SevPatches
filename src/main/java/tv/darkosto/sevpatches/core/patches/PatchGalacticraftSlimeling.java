package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PatchGalacticraftSlimeling extends Patch {
    public PatchGalacticraftSlimeling(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode favFoodSetter = AsmUtils.findMethod(this.classNode, "setRandomFavFood");

        if (favFoodSetter == null)
            return false;

        Iterable<AbstractInsnNode> insnsIter = () -> favFoodSetter.instructions.iterator();
        Stream<AbstractInsnNode> insns = StreamSupport.stream(insnsIter.spliterator(), false);

        Optional<FieldInsnNode> fieldInsn = insns
                .filter(insn -> insn instanceof FieldInsnNode)
                .map(insn -> (FieldInsnNode) insn)
                .filter(fieldInsnNode -> fieldInsnNode.desc.equals("Lnet/minecraft/item/Item;") && fieldInsnNode.name.equals(SevPatchesLoadingPlugin.WOODEN_HOE))
                .findAny();

        fieldInsn.ifPresent(insn -> insn.name = SevPatchesLoadingPlugin.PRISMARINE_SHARD);

        return fieldInsn.isPresent();
    }
}
