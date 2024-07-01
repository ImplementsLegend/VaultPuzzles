package implementslegendkt.vhpuzzles;

import com.mojang.logging.LogUtils;
import implementslegendkt.vhpuzzles.block.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("vhpuzzles")
public class VHPuzzles {

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public VHPuzzles() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> registryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
            registryEvent.getRegistry().register(PuzzleDoorBlock.INSTANCE);
            registryEvent.getRegistry().register(PuzzleControllerBlock.INSTANCE);
            registryEvent.getRegistry().register(PuzzleVisualBlock.INSTANCE);

        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
            registryEvent.getRegistry().register(PuzzleDoorBlock.ITEM);
            registryEvent.getRegistry().register(PuzzleControllerBlock.ITEM);
            registryEvent.getRegistry().register(PuzzleVisualBlock.ITEM);

        }
        @SubscribeEvent
        public static void onBlockEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> registryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
            registryEvent.getRegistry().register(PuzzleDoorBlockEntity.TYPE);
            registryEvent.getRegistry().register(PuzzleControllerBlockEntity.TYPE);
        }
    }
}
