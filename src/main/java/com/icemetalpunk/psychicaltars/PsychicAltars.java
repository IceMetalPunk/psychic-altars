package com.icemetalpunk.psychicaltars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.icemetalpunk.psychicaltars.blocks.BlockRegistry;
import com.icemetalpunk.psychicaltars.compat.CuriosStub;
import com.icemetalpunk.psychicaltars.compat.ICuriosCompat;
import com.icemetalpunk.psychicaltars.events.PAEventHandler;
import com.icemetalpunk.psychicaltars.helpers.FakePlayerHelper;
import com.icemetalpunk.psychicaltars.items.ItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// TODO: Read TODOS in Notes.txt; delete this comment when that list is done.

@Mod("psychicaltars")
public class PsychicAltars {
	// TODO: Remove this reference to LOGGER once mod is finalized and no debug
	// logging is needed
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "psychicaltars";
	public static final ItemGroup TAB = new ItemGroup(MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack((Block) BlockRegistry.get("anchor_omen").get());
		}
	};
	public static ICuriosCompat curiosRegistry;

	public PsychicAltars() {
		// TODO: Remove unneeded listeners once mod is finalized and we know
		// which aren't needed.
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new PAEventHandler());
	}

	private void setup(final FMLCommonSetupEvent event) {
		curiosRegistry = DependencyManager.optionalDependency("curios", "CuriosRegistry", CuriosStub.class);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		BlockRegistry.setRenderLayers();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		curiosRegistry.registerCurioSlots();
	}

	private void processIMC(final InterModProcessEvent event) {
		// event.getIMCStream().map(m ->
		// m.getMessageSupplier().get()).collect(Collectors.toList());
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		event.getServer().getAllLevels().forEach(world -> {
			FakePlayerHelper.create(world, "Breeder 0");
			FakePlayerHelper.create(world, "Breeder 1");
			FakePlayerHelper.create(world, "Breeder 2");
			FakePlayerHelper.create(world, "Breeder 3");
			FakePlayerHelper.create(world, "Breeder 4");
		});
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
			BlockRegistry.registerBlocks(event);
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
			BlockRegistry.registerBlockItems(event);
			ItemRegistry.registerItems(event);
		}
	}
}
