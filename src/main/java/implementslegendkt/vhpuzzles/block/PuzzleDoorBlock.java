package implementslegendkt.vhpuzzles.block;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PuzzleDoorBlock extends DoorBlock implements EntityBlock {


    //public static final Property<PuzzleType> TYPE = EnumProperty.create("type", PuzzleType.class);

    public static final Property<NewPuzzleType> TYPE_NEW = new Property<>("type", NewPuzzleType.class) {

        @Override
        public Collection<NewPuzzleType> getPossibleValues() {
            return NewPuzzleType.VALUES;
        }

        @Override
        public String getName(NewPuzzleType p_61696_) {
            return p_61696_.getSerializedName();
        }

        @Override
        public Optional<NewPuzzleType> getValue(String p_61701_) {
            return Optional.ofNullable(NewPuzzleType.NAME_TO_VALUE.get(p_61701_));
        }
    };

    public static final PuzzleDoorBlock INSTANCE = new PuzzleDoorBlock();
    public static final BlockItem ITEM = new BlockItem(PuzzleDoorBlock.INSTANCE, new Item.Properties());

    static {
        INSTANCE.setRegistryName("vhpuzzles:puzzle_door");
        ITEM.setRegistryName("vhpuzzles:puzzle_door");
    }

    public PuzzleDoorBlock(){
        super(Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());

        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(OPEN, Boolean.FALSE)
                        .setValue(HINGE, DoorHingeSide.LEFT)
                        .setValue(POWERED, Boolean.FALSE)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(TYPE_NEW, NewPuzzleType.VALUES.get(0))
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(HALF, FACING, OPEN, HINGE, POWERED,TYPE_NEW);
    }



    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return PuzzleDoorBlockEntity.TYPE.create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockHelper.getTicker(pBlockEntityType, PuzzleDoorBlockEntity.TYPE, PuzzleDoorBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Boolean isOpen = state.getValue(OPEN);
        if (!isOpen) {
            this.setOpen(player, world, state, pos, true);
            if (world instanceof ServerLevel) {
                BlockEntity te = state.getValue(HALF) == DoubleBlockHalf.LOWER ? world.getBlockEntity(pos) : world.getBlockEntity(pos.below());
                this.announcePuzzle((ServerLevel)world, (PuzzleDoorBlockEntity)te);
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    public void announcePuzzle(ServerLevel world, PuzzleDoorBlockEntity tileEntity) {
        if (tileEntity != null) {
            Component difficultyDisplay = tileEntity.getDifficulty().getDisplay();
            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(TextComponent.EMPTY);
            ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(difficultyDisplay);
            AABB box = new AABB(tileEntity.getBlockPos().offset(-15, -15, -15), tileEntity.getBlockPos().offset(15, 15, 15));
            world.getEntities((Entity)null, box, e -> e instanceof ServerPlayer).forEach(entity -> {
                if (entity instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundClearTitlesPacket(true));
                    player.connection.send(titlePacket);
                    player.connection.send(subtitlePacket);
                }
            });
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        } else {
            CompoundTag nbt = context.getItemInHand().getTag();
            if (nbt != null) {
                var type = NewPuzzleType.NAME_TO_VALUE.get(nbt.getString("type"));
                if (type != null) {
                    state = state.setValue(TYPE_NEW, type);
                }
            }

            return state;
        }
    }


}
