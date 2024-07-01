package implementslegendkt.vhpuzzles.mixin;

import iskallia.vault.core.world.processor.Palette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Palette.class)
public interface PalettePathAccessor {
    @Accessor void setPath(String s);
}
