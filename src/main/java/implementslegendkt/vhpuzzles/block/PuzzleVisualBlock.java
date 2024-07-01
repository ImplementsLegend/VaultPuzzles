package implementslegendkt.vhpuzzles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;


public class PuzzleVisualBlock extends Block {
    public static Property<Type> TYPE_PROPERTY = EnumProperty.create("variant",Type.class);
    public static final PuzzleVisualBlock INSTANCE = new PuzzleVisualBlock();
    public static final BlockItem ITEM = new BlockItem(INSTANCE, new Item.Properties());



    static {
        INSTANCE.setRegistryName("vhpuzzles:puzzle_visual");
        ITEM.setRegistryName("vhpuzzles:puzzle_visual");
    }

    public PuzzleVisualBlock() {
        super(Properties.of(Material.AMETHYST, MaterialColor.DIAMOND).strength(1.0F, 100f).sound(SoundType.AMETHYST).noOcclusion());
        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(TYPE_PROPERTY, Type.DEFAULT)
        );
    }

    @Override
    public int getSignal(BlockState p_60483_, BlockGetter p_60484_, BlockPos p_60485_, Direction p_60486_) {
        return p_60483_.getValue(TYPE_PROPERTY)==Type.DEFAULT?0:1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(TYPE_PROPERTY);
    }

    public static enum Type implements StringRepresentable {
        DEFAULT,
        MINES_0,
        MINES_1,
        MINES_2,
        MINES_3,
        MINES_4,
        MINES_5,
        MINES_6,
        MINES_7,
        MINES_8,
        SIMON_1,
        SIMON_2,
        SIMON_3,
        SIMON_4,
        SIMON_5,
        SIMON_6;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
