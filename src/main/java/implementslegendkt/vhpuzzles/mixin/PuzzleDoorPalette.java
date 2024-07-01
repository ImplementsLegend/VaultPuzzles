package implementslegendkt.vhpuzzles.mixin;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import implementslegendkt.vhpuzzles.PuzzleDifficulty;
import implementslegendkt.vhpuzzles.block.NewPuzzleType;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.tile.BernoulliWeightedTileProcessor;
import iskallia.vault.core.world.processor.tile.VaultLootTileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Mixin(Palette.class)
public class PuzzleDoorPalette {

    @Shadow @Final private static Gson GSON;

    @Inject(method = "fromPath",at = @At(value = "HEAD"),remap = false,locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private static void loadPuzzleGen(String path, CallbackInfoReturnable<Palette> cir){

        try {

            var palette = GSON.fromJson(new FileReader(path), Palette.class);
            ((PalettePathAccessor)palette).setPath(path);
            cir.setReturnValue(palette);
            if ( "config/the_vault/gen/1.0/palettes/generic/dungeon_door_placeholder.json".equals(path)){
                if(palette.getTileProcessors().get(0) instanceof VaultLootTileProcessor l) {
                    final var doorTile = PartialTile.parse("vhpuzzles:puzzle_door{id:\"vhpuzzles:puzzle_door\"}");
                    l.levels.forEach((lvl,it)->{
                        if(it instanceof BernoulliWeightedTileProcessor bw){
                            bw.success.add(doorTile, 1);
                        }
                    });
                }
                var doorCFG1 = new WeightedTileProcessor();
                doorCFG1.target("vhpuzzles:puzzle_door");
                for (var puzzleType : NewPuzzleType.VALUES){
                    for (var difficulty : PuzzleDifficulty.values()){
                        for (var rewardType : List.of("gilded_chest","living_chest","coin_stack","ornate_chest")){

                            doorCFG1.into("vhpuzzles:puzzle_door[type="+puzzleType.getSerializedName()+"]"+
                                    "{Pool:\"vhpuzzles:puzzlerooms\","+
                                    "Target: \"the_vault:treasure_room\","+
                                    "Difficulty:"+difficulty.display+","+
                                    "Palettes:[\"the_vault:dungeon_rooms/"+difficulty.difficulty+
                                    "\",\"the_vault:dungeon_rooms/"+rewardType+"_placeholder\""+
                                    ",\"the_vault:dungeon_rooms/dungeon_discoverable_medium\""+
                                    ",\"vhpuzzles:"+puzzleType.getSerializedName()+"/"+difficulty.difficulty+"\""+ "]}",1);
                        }
                    }
                }/*
                doorCFG1.into("vhpuzzles:puzzle_door[type=minesweeper]{Pool:\"vhpuzzles:puzzlerooms\", Target: \"the_vault:treasure_room\"}",1);
                doorCFG1.into("vhpuzzles:puzzle_door[type=simon_says]{Pool: \"vhpuzzles:puzzlerooms\", Target: \"the_vault:treasure_room\"}",1);
                var doorCFG2 = new WeightedTileProcessor();
                doorCFG2.target("vhpuzzles:puzzle_door");
                doorCFG2.into("vhpuzzles:puzzle_door{Difficulty: {Name: Normal, Color: 697346}, Palettes: [\"the_vault:dungeon_rooms/normal\"]}", 1);
                doorCFG2.into("vhpuzzles:puzzle_door{Difficulty: {Name: Hard, Color: 14724096}, Palettes: [\"the_vault:dungeon_rooms/hard\"]}", 1);
                doorCFG2.into("vhpuzzles:puzzle_door{Difficulty: {Name: Challenging, Color: 12729345}, Palettes: [\"the_vault:dungeon_rooms/challenging\"]}", 1);
                doorCFG2.into("vhpuzzles:puzzle_door{Difficulty: {Name: Extreme, Color: 13172736}, Palettes: [\"the_vault:dungeon_rooms/extreme\"]}", 1);
                doorCFG2.into("vhpuzzles:puzzle_door{Difficulty: {Name: Impossible, Color: 6357465}, Palettes: [\"the_vault:dungeon_rooms/impossible\"]}", 1);
                var doorCFG3 = new WeightedTileProcessor();
                doorCFG3.target("vhpuzzles:puzzle_door");
                doorCFG3.into("vhpuzzles:puzzle_door{Palettes: [\"the_vault:dungeon_rooms/ornate_chest_placeholder\"]}", 1);
                doorCFG3.into("vhpuzzles:puzzle_door{Palettes: [\"the_vault:dungeon_rooms/gilded_chest_placeholder\"]}", 1);
                doorCFG3.into("vhpuzzles:puzzle_door{Palettes: [\"the_vault:dungeon_rooms/living_chest_placeholder\"]}", 1);
                doorCFG3.into("vhpuzzles:puzzle_door{Palettes: [\"the_vault:dungeon_rooms/coin_stacks_placeholder\"]}", 1);
                palette.processTile(doorCFG1);
                palette.processTile(doorCFG2);
                palette.processTile(doorCFG3);*/
                palette.processTile(doorCFG1);
            }
        }catch (IOException e){
            cir.setReturnValue(null);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
