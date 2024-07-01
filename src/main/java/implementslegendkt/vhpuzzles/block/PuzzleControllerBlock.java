package implementslegendkt.vhpuzzles.block;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PuzzleControllerBlock extends Block implements EntityBlock {

    public static final PuzzleControllerBlock INSTANCE = new PuzzleControllerBlock();
    public static final BlockItem ITEM = new BlockItem(INSTANCE, new Item.Properties());

    static {
        INSTANCE.setRegistryName("vhpuzzles:puzzle_controller");
        ITEM.setRegistryName("vhpuzzles:puzzle_controller");
    }


    public PuzzleControllerBlock() {
        super(Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());

        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return PuzzleControllerBlockEntity.TYPE.create(p_153215_,p_153216_);
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(FACING);
    }


    public BlockState rotate(BlockState p_52790_, Rotation p_52791_) {
        return p_52790_.setValue(FACING, p_52791_.rotate(p_52790_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_52787_, Mirror p_52788_) {
        return p_52788_ == Mirror.NONE ? p_52787_ : p_52787_.rotate(p_52788_.getRotation(p_52787_.getValue(FACING)));
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if(((PuzzleControllerBlockEntity)p_60504_.getBlockEntity(p_60505_)).start())return InteractionResult.SUCCESS;
        return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockHelper.getTicker(pBlockEntityType, PuzzleControllerBlockEntity.TYPE, PuzzleControllerBlockEntity::tick);
    }
}
