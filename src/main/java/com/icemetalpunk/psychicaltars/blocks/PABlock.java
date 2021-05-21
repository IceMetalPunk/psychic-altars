package com.icemetalpunk.psychicaltars.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public interface PABlock {
	@Nullable
	public default RenderType getRenderLayer() {
		return null;
	}

	public default void register(final RegistryEvent.Register<Block> event) {
		event.getRegistry().register((Block) this);
	}

	public void registerBlockItem(final RegistryEvent.Register<Item> event);
}
