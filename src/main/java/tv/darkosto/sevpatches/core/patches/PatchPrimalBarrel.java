package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PatchPrimalBarrel extends Patch {
    public PatchPrimalBarrel(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode onEntityCollision = AsmUtils.findMethod(this.classNode, SevPatchesLoadingPlugin.ENTITY_ON_ENTITY_COLLISION);
        if (onEntityCollision == null) return false;

        InsnList insns = onEntityCollision.instructions;
        Iterable<AbstractInsnNode> insnsIter = insns::iterator;
        Stream<AbstractInsnNode> nodes = StreamSupport.stream(insnsIter.spliterator(), false);

        Optional<MethodInsnNode> targetOpt = nodes.filter(node -> node instanceof MethodInsnNode)
                .map(node -> (MethodInsnNode) node)
                .filter(invoke -> invoke.owner.equals("net/minecraft/item/ItemStack") && invoke.name.equals(SevPatchesLoadingPlugin.ITEMSTACK_IS_ITEM_EQUAL))
                .findFirst();

        if (!targetOpt.isPresent()) return false;
        MethodInsnNode target = targetOpt.get();

        InsnList newCondition = new InsnList();
        newCondition.add(new VarInsnNode(Opcodes.ALOAD, 13));
        newCondition.add(new TypeInsnNode(Opcodes.INSTANCEOF, "nmd/primal/core/common/compat/vanilla/VanillaTorchItem"));
        newCondition.add(new InsnNode(Opcodes.IAND));

        onEntityCollision.instructions.insert(target, newCondition);

        return true;
    }
}
