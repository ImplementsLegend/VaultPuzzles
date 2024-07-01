package implementslegendkt.vhpuzzles.puzzles;

import implementslegend.mod.vaultfaster.mixin.LevelChunkAccessor;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.*;

import java.util.List;

public sealed abstract class PuzzleLogic permits MineSweeperLogic, SimonSaysLogic {


    public static final int BEFORE_FIRST_MOVE_PHASE = 0;
    public static final int PLAYING_PHASE = 1;
    public static final int LOST_PHASE = 2;
    public static final int WON_PHASE = 3;

    public abstract  <W extends LevelWriter & BlockGetter> void generate(W world);

    public abstract  <W extends LevelWriter & BlockGetter> void tick(W world,long tick);

    public abstract void read(CompoundTag nbt);
    public abstract void write(CompoundTag nbt);

    public abstract int getPhase();
}
