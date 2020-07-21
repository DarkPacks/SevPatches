package tv.darkosto.sevpatches.core.utils;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.Arrays;

public class EventHandlerUtils {
    public static boolean setEventSubPriority(ClassNode input, String targetMethodName, String priority) {
        MethodNode targetMethod = AsmUtils.findMethod(input, targetMethodName);
        if (targetMethod == null) {
            SevPatchesLoadingPlugin.LOGGER.error("{} not found", targetMethodName);
            return false;
        }

        AnnotationNode annotationNode = AsmUtils.findAnnotation(targetMethod, "Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;");
        if (annotationNode == null) {
            SevPatchesLoadingPlugin.LOGGER.error("{} is not subscribed to the event bus, not altering priority", targetMethodName);
            return false;
        }

        if (annotationNode.values.contains("priority")) {
            SevPatchesLoadingPlugin.LOGGER.info("Changing priority of {} to {}", targetMethodName, priority);
            ((String[]) annotationNode.values.get(annotationNode.values.indexOf("priority") + 1))[1] = priority;
        } else {
            SevPatchesLoadingPlugin.LOGGER.info("Priority not currently set on {}, setting to {}", targetMethodName, priority);
            annotationNode.values.addAll(Arrays.asList(
                    "priority",
                    new String[]{"Lnet/minecraftforge/fml/common/eventhandler/EventPriority;", priority}
            ));
        }

        return true;
    }

    public static boolean deregisterEventHandler(ClassNode input, String targetMethod) {
        MethodNode targetMethodNode = AsmUtils.findMethod(input, targetMethod);
        if (targetMethodNode == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Failed to find handler: {}", targetMethod);
            return false;
        }

        AnnotationNode annotationNode = AsmUtils.findAnnotation(targetMethodNode, "Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;");
        if (annotationNode == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Failed to deregister event handler - was not registered: {}", targetMethod);
            return false;
        }

        targetMethodNode.visibleAnnotations.remove(annotationNode);
        return true;
    }
}
