package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.ListIterator;

/**
 * Make it possible for spawning to occur in the spawn chunks
 */
public class PatchMinecraftSpawnChunkSpawning extends Patch {
    public PatchMinecraftSpawnChunkSpawning(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode findChunksForSpawning = null;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING) && methodNode.desc.equals(SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING_DESC)) {
                findChunksForSpawning = methodNode;
            }
        }

        if (findChunksForSpawning == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method node: WorldEntitySpawner#findChunksForSpawning");
            return false;
        }

        MethodInsnNode distanceSq = null;
        for (ListIterator<AbstractInsnNode> it = findChunksForSpawning.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();
            if (insnNode instanceof MethodInsnNode) {
                MethodInsnNode mInsnNode = (MethodInsnNode) insnNode;
                if (mInsnNode.name.equals(SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ) && mInsnNode.desc.equals(SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ_DESC)) {
                    distanceSq = (MethodInsnNode) insnNode;
                }
            }
        }

        if (distanceSq == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method invocation: Vec3i#distanceSq");
            return false;
        }

        MethodInsnNode distanceSqRedirect = new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/SpawnHook",
                "distanceSqRedirect",
                "(Lnet/minecraft/util/math/BlockPos;DDD)D",
                false
        );
        findChunksForSpawning.instructions.insert(distanceSq, distanceSqRedirect);
        findChunksForSpawning.instructions.remove(distanceSq);

        return true;
    }
}
