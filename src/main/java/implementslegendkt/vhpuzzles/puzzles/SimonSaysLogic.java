package implementslegendkt.vhpuzzles.puzzles;

import implementslegendkt.vhpuzzles.block.PuzzleVisualBlock;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SimonSaysLogic extends PuzzleLogic {
    private static record Element(BlockPos position, BlockState activeState, int pitch){}


    private static final BlockState INACTIVE_STATE = PuzzleVisualBlock.INSTANCE.defaultBlockState();

    private static final Element[] ELEMENTS = {
            new Element(new BlockPos(-5,1,2), PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_1),2),
            new Element(new BlockPos(-3,1,2), PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_2),6),
            new Element(new BlockPos(-1,1,2), PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_3),10),
            new Element(new BlockPos(1,1,2),  PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_4),14),
            new Element(new BlockPos(3,1,2),  PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_5),18),
            new Element(new BlockPos(5,1,2),  PuzzleVisualBlock.INSTANCE.defaultBlockState().setValue(PuzzleVisualBlock.TYPE_PROPERTY, PuzzleVisualBlock.Type.SIMON_6),22)
    };

    private static final int PRESENTATION_LENGTH = 48;

    private int stageCount = 6;
    private int[] stages = new int[stageCount];

    private int currentStage = 0;

    private int phase = 0;

    @Override
    public <W extends LevelWriter & BlockGetter> void generate(W world) {
        stages=new int[stageCount];
        for (int i = 0; i < stages.length; i++) {
            do {

                stages[i] = (int) (Math.random()*ELEMENTS.length);
            }while (i>0 && stages[i] == stages[i-1]);
        }
    }

    @Override
    public <W extends LevelWriter & BlockGetter> void tick(W world, long tick) {
        var progress = ((double)tick)/PRESENTATION_LENGTH;

        if (progress<1) {
            for (int i = 0; i < ELEMENTS.length; i++) {
                var e = ELEMENTS[i];
                world.setBlock(e.position.above(), Blocks.NOTE_BLOCK.defaultBlockState().setValue(NoteBlock.NOTE, e.pitch), 3);
                world.setBlock(e.position.below(), Blocks.REDSTONE_LAMP.defaultBlockState(), 3);
                //world.setBlock(e.position, INACTIVE_STATE, 3);
                world.setBlock(e.position, stages[(int) (progress * stageCount)]==i?e.activeState:INACTIVE_STATE, 3);
            }
        }else {
            if (phase==BEFORE_FIRST_MOVE_PHASE){

                phase=PLAYING_PHASE;

                for (int i = 0; i < ELEMENTS.length; i++) {
                    var e = ELEMENTS[i];
                    world.setBlock(e.position, INACTIVE_STATE, 3);
                }
            }
            if(phase == PLAYING_PHASE){

                for (int i = 0; i < ELEMENTS.length; i++) {
                    var e = ELEMENTS[i];
                    if(world.getBlockState(e.position).isAir()){
                        var isCorrectStage = stages[currentStage]==i;

                        world.setBlock(e.position, isCorrectStage?e.activeState:INACTIVE_STATE, 3);
                        if (isCorrectStage){
                            currentStage++;
                            if(currentStage>=stageCount){
                                phase=WON_PHASE;
                                return;
                            }
                        }else phase=LOST_PHASE;
                    }
                }
            }
        }
    }

    @Override
    public int getPhase() {
        return phase;
    }


    @Override
    public void read(CompoundTag nbt) {
        stageCount = nbt.getInt("difficulty");
    }

    @Override
    public void write(CompoundTag nbt) {
        nbt.putInt("difficulty",stageCount);

    }
}
