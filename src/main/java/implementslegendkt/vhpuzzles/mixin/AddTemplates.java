package implementslegendkt.vhpuzzles.mixin;

import implementslegendkt.vhpuzzles.Config;
import implementslegendkt.vhpuzzles.PuzzleDifficulty;
import implementslegendkt.vhpuzzles.block.NewPuzzleType;
import iskallia.vault.config.core.PalettesConfig;
import iskallia.vault.config.core.TemplatePoolsConfig;
import iskallia.vault.config.core.TemplatesConfig;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.template.StructureTemplate;
import iskallia.vault.core.world.template.data.DirectTemplateEntry;
import iskallia.vault.core.world.template.data.IndirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.init.ModConfigs;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModConfigs.class)
public class AddTemplates {
    @Redirect(method = "registerGen",at = @At(value = "INVOKE", target = "Liskallia/vault/config/core/TemplatePoolsConfig;toRegistry()Liskallia/vault/core/data/key/registry/KeyRegistry;"),remap = false)
    private static KeyRegistry addTemplatePools(TemplatePoolsConfig instance){
        var result = instance.toRegistry();
        var pool = new TemplatePool();
        var entry = new DirectTemplateEntry(new ResourceLocation("vhpuzzles","puzzleroom1"));
        pool.addLeaf(entry,1.0);
        var key = TemplatePoolKey.create(new ResourceLocation("vhpuzzles","puzzlerooms"), "Puzzle Room");
        for(var ver:Version.values())
            key=key.with(ver, pool);

        System.out.println("[vhpuzzles] added pool "+new ResourceLocation("vhpuzzles","puzzlerooms")+" = "+pool);
        result.register(key);
        return result;
    }
    @Redirect(method = "registerGen",at = @At(value = "INVOKE", target = "Liskallia/vault/config/core/TemplatesConfig;toRegistry()Liskallia/vault/core/data/key/registry/KeyRegistry;"),remap = false)
    private static KeyRegistry addTemplates(TemplatesConfig instance){
        var result = instance.toRegistry();
        var pth = Config.cfgDir.resolve("puzzleroom1.nbt").toString();
        StructureTemplate template = StructureTemplate.fromPath(pth);
        var key1 = TemplateKey.create(new ResourceLocation("vhpuzzles","puzzleroom1"), "Puzzle Room");
        System.out.println("[vhpuzzles] added room "+new ResourceLocation("vhpuzzles","puzzleroom1")+" = "+template + " path: "+pth);
        for(var ver:Version.values())
            key1=key1.with(ver, template);
        result.register(key1);
        return result;
    }
    @Redirect(method = "registerGen",at = @At(value = "INVOKE", target = "Liskallia/vault/config/core/PalettesConfig;toRegistry()Liskallia/vault/core/data/key/registry/KeyRegistry;"),remap = false)
    private static KeyRegistry addPalettes(PalettesConfig instance){
        var result = instance.toRegistry();
        var basePath = Config.cfgDir.resolve("palettes/");

        for (var type: NewPuzzleType.VALUES) {
            for (var difficulty : PuzzleDifficulty.values()) {
                var key = PaletteKey.create(new ResourceLocation("vhpuzzles", type.getSerializedName() + "/" + difficulty.difficulty), difficulty.name() + " " + type.type().getSimpleName()).with(Version.v1_0, Palette.fromPath(basePath.resolve(type.getSerializedName()).resolve(difficulty.difficulty+".json").toString()));
                result.register(key);

            }
        }

        return result;
    }
}
