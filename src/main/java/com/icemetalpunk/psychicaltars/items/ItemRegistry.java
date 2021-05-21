package com.icemetalpunk.psychicaltars.items;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ItemRegistry {
	private static HashMap<String, PAItem> registry = new HashMap<>();
	static {
	}

	public static void registerItems(final RegistryEvent.Register<Item> event) {
		for (PAItem item : registry.values()) {
			item.register(event);
		}
	}

	@Nullable
	public static PAItem get(String name) {
		return registry.get(name);
	}
}
