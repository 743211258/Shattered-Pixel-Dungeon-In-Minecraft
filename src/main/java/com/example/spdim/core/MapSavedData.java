package com.example.spdim.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import com.example.spdim.core.interfaces.CodecToNBT;

import java.util.HashMap;
import java.util.Map;

public class MapSavedData<K, V> extends SavedData {

	private final Map<K, V> map;

	private final CodecToNBT<K> keyCodec;
	private final CodecToNBT<V> valueCodec;

	public MapSavedData(
		Map<K, V> map,
		CodecToNBT<K> keyCodec,
		CodecToNBT<V> valueCodec
	) {
		this.map = map;
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	public Map<K, V> getMap() {
		return map;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag list = new ListTag();

		for (Map.Entry<K, V> entry : map.entrySet()) {
			CompoundTag element = new CompoundTag();

			element.put("Key", keyCodec.encode(entry.getKey()));
			element.put("Value", valueCodec.encode(entry.getValue()));

			list.add(element);
		}

		tag.put("Map", list);

		return tag;
	}

	public static <K, V> MapSavedData<K, V> load(
		CompoundTag tag,
		Map<K, V> map,
		CodecToNBT<K> keyCodec,
		CodecToNBT<V> valueCodec
	) {
		map.clear();

		ListTag list = tag.getList("Map", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			CompoundTag element = list.getCompound(i);

			K key = keyCodec.decode(element.getCompound("Key"));
			V value = valueCodec.decode(element.getCompound("Value"));

			map.put(key, value);
		}

		return new MapSavedData<>(
				map,
				keyCodec,
				valueCodec
		);
	}
}
