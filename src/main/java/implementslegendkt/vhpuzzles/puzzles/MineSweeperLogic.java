package implementslegendkt.vhpuzzles.puzzles;

import com.google.gson.annotations.SerializedName;
import implementslegendkt.vhpuzzles.block.PuzzleVisualBlock;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

public final class MineSweeperLogic extends PuzzleLogic{


    private static final BlockState DEFAULT_BLOCK_STATE = PuzzleVisualBlock.INSTANCE.defaultBlockState();
    private static final BlockState MINE_BLOCK_STATE = Blocks.TNT.defaultBlockState().setValue(TntBlock.UNSTABLE,true);
    private static final BlockState NUMBER_BLOCK_STATES[] = {
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_0),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_1),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_2),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_3),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_4),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_5),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_6),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_7),
            PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.MINES_8)
    };

    private float mineRatio = 0.25f;
    private BitSet mines = new BitSet(13*7);
    private int phase = 0;
    private BlockPos canvasOffset = new BlockPos(0,0,2);

    @Override
    public <W extends LevelWriter & BlockGetter> void generate(W world) {
        for (int x = -6; x <= 6; x++) {
            for (int y = 0; y < 7; y++) {
                setMine(Math.random()<mineRatio,x,y);
                world.setBlock(new BlockPos(x,y,0).offset(canvasOffset),DEFAULT_BLOCK_STATE,3);
            }
        }
    }

    private void setMine(boolean state,int x,int y){
        if(x<-6 || x>6 || y<0 || y>=7) return;
        int idx = (x+6)+y*8;
        mines.set(idx,state);
    }
    private boolean getMine(int x,int y){
        if(x<-6 || x>6 || y<0 || y>=7) return false;
        int idx = (x+6)+y*8;
        return mines.get(idx);
    }

    private <W extends LevelWriter & BlockGetter> void moveReveal(int x, int y,W world){
        if(x<-6 || x>6 || y<0 || y>=7) return;
        if (phase == BEFORE_FIRST_MOVE_PHASE){
            for (int x2 = -1; x2 < 2; x2++) { // prune mines so first move always hits '0'
                for (int y2 = -1; y2 < 2; y2++) {
                    setMine(false, x+x2,y+y2);
                }
            }
            phase = PLAYING_PHASE;
        }
        if(phase==PLAYING_PHASE) {
            if (getMine(x, y)) {
                phase = LOST_PHASE;

                for (int x2 = -6; x2 <= 6; x2++) {
                    for (int y2 = 0; y2 < 7; y2++) {
                        if(getMine(x2,y2))world.setBlock(new BlockPos(x2,y2,0).offset(canvasOffset),MINE_BLOCK_STATE,3);
                    }
                }
            }else {
                var state = world.getBlockState(new BlockPos(x,y,0).offset(canvasOffset));
                if(state==DEFAULT_BLOCK_STATE || state.isAir()) {
                    var count = countMines(x,y);
                    world.setBlock(new BlockPos(x, y, 0).offset(canvasOffset), NUMBER_BLOCK_STATES[count], 3);
                    if(count==0){

                        for (int x2 = -1; x2 < 2; x2++) { // prune mines so first move always hits '0'
                            for (int y2 = -1; y2 < 2; y2++) {
                                moveReveal(x+x2,y+y2,world);
                            }
                        }
                    }
                }
            }
            var winFlag = true;
            for (int x2 = -6; x2 <= 6; x2++) {
                for (int y2 = 0; y2 < 7; y2++) {
                    if(!getMine(x2,y2))winFlag&=world.getBlockState(new BlockPos(x2,y2,0).offset(canvasOffset))!=DEFAULT_BLOCK_STATE;
                }
            }
            if (winFlag){
                phase=WON_PHASE;
            }
        }

    }

    private int countMines(int x, int y) {
        int i = 0;

        for (int x2 = -1; x2 < 2; x2++) { // prune mines so first move always hits '0'
            for (int y2 = -1; y2 < 2; y2++) {
                if (getMine(x+x2,y+y2))i++;
            }
        }
        return i;
    }


    @Override
    public <W extends LevelWriter & BlockGetter> void tick(W world, long tick) {

        for (int x2 = -6; x2 <= 6; x2++) {
            for (int y2 = 0; y2 < 7; y2++) {
                if (world.getBlockState(new BlockPos(x2,y2,0).offset(canvasOffset)).isAir()) moveReveal(x2,y2,world);
            }
        }
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public void read(CompoundTag nbt) {
        mineRatio = nbt.getFloat("difficulty");
        var data = nbt.getLongArray("state");
        if(data.length!=0)mines = BitSet.valueOf(data);
        phase=nbt.getInt("phase");
        var offsetArray = nbt.getIntArray("canvasOffset");
        if(offsetArray.length!=0){
            canvasOffset= new BlockPos(offsetArray[0],offsetArray[1],offsetArray[2]);
        }

    }

    @Override
    public void write(CompoundTag nbt) {
        nbt.putFloat("difficulty",mineRatio);
        nbt.putInt("phase",phase);
        nbt.putLongArray("state",mines.toLongArray());
        nbt.putIntArray("canvasOffset",new int[]{canvasOffset.getX(),canvasOffset.getY(),canvasOffset.getZ()});
    }
}
