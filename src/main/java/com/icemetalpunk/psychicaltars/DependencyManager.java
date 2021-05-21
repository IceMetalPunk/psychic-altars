package com.icemetalpunk.psychicaltars;

import javax.annotation.Nullable;

import net.minecraftforge.fml.ModList;

public class DependencyManager {
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T optionalDependency(String modName, String className, Class<? extends T> stub) {
		if (ModList.get().isLoaded(modName)) {
			try {
				return (T) Class.forName("com.icemetalpunk.psychicaltars.compat." + className).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				return stub.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
