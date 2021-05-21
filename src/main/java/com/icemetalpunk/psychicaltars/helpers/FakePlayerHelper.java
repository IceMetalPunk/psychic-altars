package com.icemetalpunk.psychicaltars.helpers;

import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

public class FakePlayerHelper {
	private static WeakHashMap<Pair<ServerWorld, String>, FakePlayer> FAKE_PLAYERS = new WeakHashMap<>();
	private static WeakHashMap<UUID, GameProfile> UUID_PROFILE_MAP = new WeakHashMap<>();
	private static WeakHashMap<Pair<ServerWorld, UUID>, FakePlayer> UUID_PLAYER_MAP = new WeakHashMap<>();

	public static FakePlayer getOrCreate(ServerWorld world, String name) {
		if (FAKE_PLAYERS.containsKey(Pair.of(world, name))) {
			return FAKE_PLAYERS.get(Pair.of(world, name));
		}
		UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
		GameProfile profile = getGameProfileFromUUID(uuid).orElseGet(() -> {
			return new GameProfile(uuid, name);
		});
		FakePlayer player = new FakePlayer(world, profile);
		FAKE_PLAYERS.put(Pair.of(world, name), player);
		UUID_PROFILE_MAP.put(uuid, profile);
		UUID_PLAYER_MAP.put(Pair.of(world, uuid), player);
		return player;
	}

	public static FakePlayer create(ServerWorld world, String name) {
		return getOrCreate(world, name);
	}

	private static Optional<GameProfile> getGameProfileFromUUID(UUID uuid) {
		return Optional.ofNullable(UUID_PROFILE_MAP.get(uuid));
	}

	public static Optional<FakePlayer> getByUUID(ServerWorld world, UUID uuid) {
		return Optional.ofNullable(UUID_PLAYER_MAP.get(Pair.of(world, uuid)));
	}
}
