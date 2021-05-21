package com.icemetalpunk.psychicaltars.blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public interface PABlock {
	public default RenderType getRenderLayer() {
		return RenderType.solid();
	}

	public void registerBlockItem(final RegistryEvent.Register<Item> event);
}
