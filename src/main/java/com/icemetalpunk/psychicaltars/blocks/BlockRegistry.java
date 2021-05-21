package com.icemetalpunk.psychicaltars.blocks;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.blocks.altar.TelepathicAltarBlock;
import com.icemetalpunk.psychicaltars.multiblocks.MultiblockHelper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class BlockRegistry {
	private static HashMap<String, PABlock> registry = new HashMap<>();
	static {
		registry.put("omenstone",
				new PABasicBlock(AbstractBlock.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS), "omenstone"));
		registry.put("anchor_omen", new OmenBlock("anchor_omen", OmenTypes.ANCHOR));
		registry.put("speed_omen", new OmenBlock("speed_omen", OmenTypes.SPEED));
		registry.put("range_omen", new OmenBlock("range_omen", OmenTypes.RANGE));
		registry.put("efficiency_omen", new OmenBlock("efficiency_omen", OmenTypes.EFFICIENCY));
		registry.put("persistence_omen", new OmenBlock("persistence_omen", OmenTypes.PERSISTENCE));
		registry.put("craft_recipe_block", new CraftRecipeBlock(
				AbstractBlock.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS), "craft_recipe_block"));
		registry.put("telepathic_altar",
				new TelepathicAltarBlock(AbstractBlock.Properties.copy(Blocks.GLASS), "telepathic_altar"));
	}

	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		for (PABlock block : registry.values()) {
			event.getRegistry().register((Block) block);
		}
		MultiblockHelper.registerMatchers();
		MultiblockHelper.registerMultiblocks(registry.values());
	}

	public static void registerBlockItems(final RegistryEvent.Register<Item> event) {
		for (PABlock block : registry.values()) {
			block.registerBlockItem(event);
		}
	}

	public static void setRenderLayers() {
		for (PABlock block : registry.values()) {
			RenderTypeLookup.setRenderLayer((Block) block, block.getRenderLayer());
		}
	}

	@Nullable
	public static PABlock get(String name) {
		return registry.get(name);
	}
}
