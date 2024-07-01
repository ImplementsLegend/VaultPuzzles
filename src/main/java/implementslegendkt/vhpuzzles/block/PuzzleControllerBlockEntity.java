package implementslegendkt.vhpuzzles.block;

import implementslegendkt.vhpuzzles.puzzles.PuzzleLogic;
import iskallia.vault.block.entity.DungeonDoorTileEntity;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public class PuzzleControllerBlockEntity extends BlockEntity {

    public static final BlockEntityType<PuzzleControllerBlockEntity> TYPE = BlockEntityType.Builder.of(
                    (pos,state)->new PuzzleControllerBlockEntity(PuzzleControllerBlockEntity.TYPE,pos,state), PuzzleControllerBlock.INSTANCE
            )
            .build(null);
    static {
        TYPE.setRegistryName("vhpuzzles:puzzle_controller");
    }

    private WorldProjection projection = new WorldProjection();


    private BoundingBox[] toDestroy = new BoundingBox[0];

    @Nullable
    private PuzzleLogic puzzle;

    private int time = 0;



    public PuzzleControllerBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }


    private void tick() {


        if(puzzle!=null && time>0){
            var oldPhase = puzzle.getPhase();
            if (time==1){
                puzzle.generate(projection);
            }else {
                puzzle.tick(projection,time-1);
            }
            var newPhaze = puzzle.getPhase();
            if(newPhaze!=oldPhase){
                if(newPhaze==PuzzleLogic.WON_PHASE){
                    //celebrate
                    level.playSound(null,worldPosition, ModSounds.PUZZLE_COMPLETION_MAJOR, SoundSource.BLOCKS,1f,1f);
                    for (var v:toDestroy){
                        for (int x = v.minX(); x <= v.maxX(); x++) {
                            for (int y = v.minY(); y <= v.maxY(); y++) {
                                for (int z = v.minZ(); z <= v.maxZ(); z++) {
                                    projection.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),3);
                                }
                            }
                        }
                    }
                }
                if (newPhaze==PuzzleLogic.LOST_PHASE){
                    level.playSound(null,worldPosition, ModSounds.PUZZLE_COMPLETION_FAIL, SoundSource.BLOCKS,1f,1f);
                    //be sad
                }
            }
            time++;
        }
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("puzzle_type")) {
            try {
                this.puzzle = NewPuzzleType.NAME_TO_VALUE.get(nbt.getString("puzzle_type")).type().getConstructor().newInstance();
                this.puzzle.read(nbt.getCompound("puzzle_settings"));
                this.time = nbt.getInt("time");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        var list = nbt.getList("to_destroy", Tag.TAG_COMPOUND);
        toDestroy = new BoundingBox[list.size()];
        for (int i = 0; i < list.size(); i++) {
            var element = list.getCompound(i);
            var parsed = new BoundingBox(
                    element.getInt("lx"),
                    element.getInt("ly"),
                    element.getInt("lz"),
                    element.getInt("hx"),
                    element.getInt("hy"),
                    element.getInt("hz")
                    );
            toDestroy[i]=parsed;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.puzzle != null) {
            nbt.putString("puzzle_type", NewPuzzleType.VALUES.stream().filter(it->it.type()==puzzle.getClass()).findFirst().get().getSerializedName());
            var nbt2 = new CompoundTag();
            puzzle.write(nbt2);
            nbt.put("puzzle_settings",nbt2);
            nbt.putInt("time",time);

        }
        var list = new ListTag();
        for (int i = 0; i < toDestroy.length; i++) {
            var element = toDestroy[i];
            var serialized = new CompoundTag();
            serialized.putInt("lx",element.minX());
            serialized.putInt("ly",element.minY());
            serialized.putInt("lz",element.minZ());
            serialized.putInt("hx",element.maxX());
            serialized.putInt("hy",element.maxY());
            serialized.putInt("hz",element.maxZ());
            list.add(serialized);
        }
        nbt.put("to_destroy", list);
    }

    /*
    BlockState getBlockState(){
        return level.getBlockState(getBlockPos());
    }
    */
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, PuzzleControllerBlockEntity puzzleDoorBlockEntity) {
        puzzleDoorBlockEntity.tick();
    }

    public BlockPos transform(BlockPos original){
        return original.rotate(switch (getBlockState().getValue(HorizontalDirectionalBlock.FACING)){
            case DOWN, UP, NORTH -> Rotation.NONE;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            case EAST -> Rotation.CLOCKWISE_90;
        }).offset(this.worldPosition);
    }

    public boolean start() {
        if(time<1){
            time=1;
            return true;
        }
        return false;
    }


    private class WorldProjection implements LevelWriter, BlockGetter{

        @org.jetbrains.annotations.Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos p_45570_) {
            return level.getBlockEntity(transform(p_45570_));
        }

        @Override
        public BlockState getBlockState(BlockPos p_45571_) {
            return level.getBlockState(transform(p_45571_));
        }

        @Override
        public FluidState getFluidState(BlockPos p_45569_) {
            return level.getFluidState(transform(p_45569_));
        }

        @Override
        public int getHeight() {
            return level.getHeight();
        }

        @Override
        public int getMinBuildHeight() {
            return level.getMinBuildHeight();
        }

        @Override
        public boolean setBlock(BlockPos p_46947_, BlockState p_46948_, int p_46949_, int p_46950_) {
            return level.setBlock(transform(p_46947_),p_46948_,p_46949_,p_46950_);
        }

        @Override
        public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
            return level.removeBlock(transform(p_46951_),p_46952_);
        }

        @Override
        public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, @org.jetbrains.annotations.Nullable Entity p_46959_, int p_46960_) {
            return level.destroyBlock(transform(p_46957_),p_46958_,p_46959_,p_46960_);
        }
    }
}
