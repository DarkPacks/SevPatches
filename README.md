# SevPatches
A small coremod to patch some longstanding issues in SevTech Ages that either could not be resolved in another fashion,
or which have not been resolved in a timely manner.

## Patches
#### [DarkPacks/SevTech-Ages#3829](https://www.github.com/DarkPacks/SevTech-Ages/issues/3829)
Patches SimpleHarvest. Firing of HarvestDropsEvent moved to after the point at which the decision is taken to harvest.

Without this patch, SimpleHarvest fires the HarvestDropsEvent before deciding if it intends to break the block, allowing
mods such as AstralSorcery or IndustrialForegoing to consume the drops array before deciding not to take any action.

#### [DarkPacks/SevTech-Ages#3847](https://www.github.com/DarkPacks/SevTech-Ages/issues/3847)
Patches InControl. Changes the priority of InControl's LivingDropsEvent handler to LOW (was LOWEST). Whilst this change
can't be made to InControl due to compatibility issues in other contexts, no compatibility issues have yet been found in
SevTech. 

This allows mods which wish to consume the drops array to do so after InControl has made the desired changes.

#### [DarkPacks/SevTech-Ages#3732](https://www.github.com/DarkPacks/SevTech-Ages/issues/3732)
Patches Immersive Engineering. This patch adds an additional check to the onEntityCollision handler of the metal press.
The metal press will no longer pick up 'EntityItem's, but will accept all derived classes.

This prevents the metal press from picking up from the temporary vanilla item entity before Real Drops replaces it with
its own entity.

#### [DarkPacks/SevTech-Ages#4091](https://www.github.com/DarkPacks/SevTech-Ages/issues/4091)
Patches Astral Sorcery and Minecraft. There are two components to this patch. The first alters Astral's hooks to use
integer enchantment IDs. The second component reapplies Astral's ASM patches using Mixins as otherwise they are
overwritten by JEID.

This restores functionality to the Resplendent Prism, though in an ideal world the first component of this patch would
be handled by JEID, whilst the second would be unnecessary if JEID didn't use an overwrite mixin.
