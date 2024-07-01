package implementslegendkt.vhpuzzles.block;

import iskallia.vault.block.entity.DungeonDoorTileEntity;
import iskallia.vault.block.entity.TreasureDoorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;

import java.util.zip.ZipFile;

public class PuzzleDoorBlockEntity extends TreasureDoorTileEntity {

    public static final BlockEntityType<PuzzleDoorBlockEntity> TYPE = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
                    (pos,state)->new PuzzleDoorBlockEntity(PuzzleDoorBlockEntity.TYPE,pos,state), PuzzleDoorBlock.INSTANCE
            )
            .build(null);
    static {
        TYPE.setRegistryName("vhpuzzles:puzzle_door");
    }

    private DungeonDoorTileEntity.Difficulty difficulty;


    public PuzzleDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    public DungeonDoorTileEntity.Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Difficulty", 10)) {
            this.difficulty = DungeonDoorTileEntity.Difficulty.fromNBT(nbt.getCompound("Difficulty"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.difficulty != null) {
            nbt.put("Difficulty", this.difficulty.toNBT());
        }
    }




}
